package com.uk.uk.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uk.uk.entity.ProductMasterDataDAO;
import com.uk.uk.repository.ProductMasterDataRepo;
import com.uk.uk.repository.PricingInsightsRepo;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class ProductMasterDataImpl {
    @Autowired
    private ProductMasterDataRepo ProductMasterDataRepo;
    @Autowired
    private PricingInsightsRepo PricingInsightsRepo;
    @Autowired
    private AsdaImpl AsdaImpl;
    @Autowired
    private MorrisonsImpl MorrisonsImpl;
    @Autowired
    private SainsburysImpl SainsburysImpl;
    @Autowired
    private TescoImpl TescoImpl;
    @Autowired
    private WaitRoseImpl WaitRoseImpl;
    @Autowired
    private AmazonImpl AmazonImpl;
    @Autowired
    private AmazonTempImpl AmazonTempImpl;
    @Autowired
    private CoOpImpl CoOpImpl;
    @Autowired
    private OcadoImpl OcadoImpl;


    public Boolean insertProductMasterData(List<ProductMasterDataDAO> ProductMasterDataDAOList) throws JsonProcessingException, InterruptedException {

        System.setProperty("webdriver.gecko.driver", "D:\\Pixmonks Backup\\Uk\\Git\\Uk-Backend\\drivers\\geckodriver.exe");


        Integer tagMaxNo;
        tagMaxNo = ProductMasterDataRepo.getMaxTagNo();

        if (null != tagMaxNo)
            tagMaxNo++;
        else
            tagMaxNo = 1;

        for (ProductMasterDataDAO productMasterData : ProductMasterDataDAOList) {
            if (productMasterData.getShopName().equalsIgnoreCase("Amazon")) {
                String[] validUrl = productMasterData.getUrl().split("/ref");
                productMasterData.setUrl(validUrl[0]);
            }
            ProductMasterDataRepo.insertProductMasterData(productMasterData.getProductName(), productMasterData.getQuantity(),
                    productMasterData.getMeasurement(), productMasterData.getShopName(), productMasterData.getUrl(),
                    productMasterData.getCategory(), tagMaxNo, true);
        }

        System.out.println("** #1 **");
        Integer finalTagMaxNo = tagMaxNo;
        CompletableFuture.runAsync(() -> {
            try {
                additionalProcessing(finalTagMaxNo);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        System.out.println("** #2 **");

        return true;
    }

    public Boolean hideProductByTag(Integer tag) {
        try {
            ProductMasterDataRepo.updateTagStatus(tag);
            return true;
        } catch (Exception e) {
            System.out.println("Hide Product By Tag Error : " + e);
            return false;
        }
    }

    public void additionalProcessing(Integer tagMaxNo) throws InterruptedException {
        // Long running task
        System.out.println("**  MAIN METHOD 1 **" + tagMaxNo);

        List<ProductMasterDataDAO> productMasterDataDAOListByTagNo = new ArrayList<>();

        productMasterDataDAOListByTagNo = ProductMasterDataRepo.getProductMasterDataByTagNo(tagMaxNo);

        System.out.println("**  MAIN METHOD 2 **" + productMasterDataDAOListByTagNo.size());


        for (ProductMasterDataDAO productMasterData : productMasterDataDAOListByTagNo) {

            String shopName = productMasterData.getShopName();

            System.out.println(shopName + "SHOP NAME");

            switch (shopName) {
                case "Morrisons":
                    MorrisonsImpl.insertPricingInsights(productMasterData);
                    break;
                case "Sainsburys":
                case "Tesco":
                case "WaitRose":
                case "Amazon":

                    FirefoxOptions options = new FirefoxOptions();
                    options.addArguments("-headless"); // Add headless argument

                    WebDriver driver = new FirefoxDriver(options);

                    if (shopName.equalsIgnoreCase("Sainsburys"))
                        SainsburysImpl.insertPricingInsights(productMasterData, driver, 0);
                    else if (shopName.equalsIgnoreCase("Tesco"))
                        TescoImpl.insertPricingInsights(productMasterData, driver);
                    else if (shopName.equalsIgnoreCase("WaitRose"))
                        WaitRoseImpl.insertPricingInsights(productMasterData, driver, 0);
                    else
                        AmazonTempImpl.insertPricingInsights(driver, 0, productMasterData);
                    driver.close();
                    break;
//                case "Amazon":
//                    AmazonTempImpl.insertPricingInsights(driver, 0, productMasterData);
//                    break;
                case "Ocado":
                    OcadoImpl.insertPricingInsights(productMasterData);
                    break;
                case "CoOp":
                    CoOpImpl.insertPricingInsights(productMasterData);
                    break;
                case "ASDA":
                    AsdaImpl.insertPricingInsights(productMasterData);
                    break;
            }
        }

    }


    public Boolean updateProductMasterByTag(List<ProductMasterDataDAO> ProductMasterDataDAOList) throws InterruptedException {
        Boolean updateStatus = false;
        Boolean runScrap = true;

        if (ProductMasterDataDAOList.size() > 0) {
            for (ProductMasterDataDAO productMasterData : ProductMasterDataDAOList) {

                //Check the shop name is existing already if yes call the update query, else call insert query

                ProductMasterDataDAO productMasterDataByShopNameAndTag = ProductMasterDataRepo.getProductMasterDataByShopNameAndTag(
                        productMasterData.getShopName(), productMasterData.getTag()
                );

                if (null != productMasterDataByShopNameAndTag) {

                    if (productMasterData.getUrl().isEmpty()) {
                        ProductMasterDataRepo.deleteProductMasterByNo(productMasterData.getShopName());
                        PricingInsightsRepo.deletePricingInsightsByNo(productMasterDataByShopNameAndTag.getNo());
                        runScrap = false;
                    } else {
                        ProductMasterDataRepo.updateProductMasterData(productMasterData.getTag(), productMasterData.getProductName(),
                                productMasterData.getQuantity(), productMasterData.getMeasurement(), productMasterData.getUrl(),
                                productMasterData.getCategory(), productMasterData.getShopName());
                    }

                } else {
                    ProductMasterDataRepo.insertProductMasterData(productMasterData.getProductName(), productMasterData.getQuantity(),
                            productMasterData.getMeasurement(), productMasterData.getShopName(), productMasterData.getUrl(),
                            productMasterData.getCategory(), productMasterData.getTag(), true);
                }

                String shopName = productMasterData.getShopName();


                if (runScrap) {
                    CompletableFuture.runAsync(() -> {
                        try {
                            insertPricingInsightsShopWise(shopName, productMasterData);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }

                System.out.println(shopName + "SHOP NAME");

            }
            updateStatus = true;
        }
        return updateStatus;
    }

    public void insertPricingInsightsShopWise(String shopName, ProductMasterDataDAO productMasterData) throws InterruptedException {
        switch (shopName) {
            case "Morrisons":
                MorrisonsImpl.insertPricingInsights(productMasterData);
                break;
            case "Sainsburys":
            case "Tesco":
            case "WaitRose":
            case "Amazon":

                FirefoxOptions options = new FirefoxOptions();
                options.addArguments("-headless"); // Add headless argument

                WebDriver driver = new FirefoxDriver(options);

                if (shopName.equalsIgnoreCase("Sainsburys"))
                    SainsburysImpl.insertPricingInsights(productMasterData, driver, 0);
                else if (shopName.equalsIgnoreCase("Tesco"))
                    TescoImpl.insertPricingInsights(productMasterData, driver);
                else if (shopName.equalsIgnoreCase("WaitRose"))
                    WaitRoseImpl.insertPricingInsights(productMasterData, driver, 0);
                else {
                    String[] validUrl = productMasterData.getUrl().split("/ref");
                    productMasterData.setUrl(validUrl[0]);
                    AmazonTempImpl.insertPricingInsights(driver, 0, productMasterData);
                }
                driver.close();
                break;
//                case "Amazon":
//                    AmazonTempImpl.insertPricingInsights(driver, 0, productMasterData);
//                    break;
            case "Ocado":
                OcadoImpl.insertPricingInsights(productMasterData);
                break;
            case "CoOp":
                CoOpImpl.insertPricingInsights(productMasterData);
                break;
            case "ASDA":
                AsdaImpl.insertPricingInsights(productMasterData);
                break;
        }
    }
}
