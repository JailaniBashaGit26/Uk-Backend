package com.uk.uk.implementation;

import com.uk.uk.entity.ProductMasterDataDAO;
import com.uk.uk.repository.PricingInsightsRepo;
import com.uk.uk.repository.ProductMasterDataRepo;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.util.*;

@Service
public class TescoImpl {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    ProductMasterDataRepo ProductMasterDataRepo;
    @Autowired
    PricingInsightsRepo ProductInsightsRepo;

    public void getProductDetails() throws InterruptedException {
        List<ProductMasterDataDAO> productMasterDataList = new ArrayList<>();

        // Filter the ProductMasterData table for "Tesco"
        productMasterDataList = ProductMasterDataRepo.getProductMasterDataByShopName("Tesco");

        // Set up FirefoxOptions for headless mode
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("-headless");

        // Open the Firefox Driver
        WebDriver driver = new FirefoxDriver(options);

        for (ProductMasterDataDAO productMasterData : productMasterDataList) {
            if (!productMasterData.getUrl().isEmpty())
                insertPricingInsights(productMasterData, driver);
        }

        driver.close();
    }

    public void insertPricingInsights(ProductMasterDataDAO productMasterData, WebDriver driver) throws InterruptedException {

        // Navigate to the product url
        driver.get(productMasterData.getUrl());

        //Current Timestamp
        Timestamp now = new Timestamp(System.currentTimeMillis());

        Thread.sleep(3000);
        try {

            // Get the price by using the cssSelector
            String itemPriceString = driver.findElement(By.cssSelector(".eNIEDh")).getText().split("£")[1];

            // Convert the price string to double
            Double itemPrice = Double.parseDouble(itemPriceString);

            // Locate the image element
            WebElement imageElement = driver.findElement(By.tagName("img"));

            // Get the src attribute value
            String imageRef = imageElement.getAttribute("src");

            //Insert into PricingInsights table
            ProductInsightsRepo.insertPricingInsights(productMasterData.getNo(), productMasterData.getTag(),
                    productMasterData.getShopName(), itemPrice, productMasterData.getUrl(), true, now, imageRef);

        } catch (Exception e) {

            //Insert into PricingInsights table and set price as 0.0 and availability as false.
            ProductInsightsRepo.insertPricingInsights(productMasterData.getNo(), productMasterData.getTag(),
                    productMasterData.getShopName(), 0.0, productMasterData.getUrl(), false, now, "");

            System.out.println("Error URL :" + productMasterData.getUrl());
        }
    }
}
