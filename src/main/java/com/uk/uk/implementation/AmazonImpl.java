package com.uk.uk.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.uk.uk.entity.ProductMasterDataDAO;
import com.uk.uk.repository.PricingInsightsRepo;
import com.uk.uk.repository.ProductMasterDataRepo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class AmazonImpl {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    com.uk.uk.repository.ProductMasterDataRepo ProductMasterDataRepo;

    @Autowired
    PricingInsightsRepo ProductInsightsRepo;

    public void getProductDetails() throws JsonProcessingException {
        List<ProductMasterDataDAO> productMasterDataList = new ArrayList<>();

        // Filter the ProductMasterData table for "Amazon"
        productMasterDataList = ProductMasterDataRepo.getProductMasterDataByShopName("Amazon");

        for (ProductMasterDataDAO productMasterData : productMasterDataList) {
            if (!productMasterData.getUrl().isEmpty())
                insertPricingInsights(productMasterData);
        }

//        ProductMasterDataDAO productMasterData = new ProductMasterDataDAO();
//        productMasterData.setUrl("https://www.amazon.co.uk/Bombay-Sapphire-Distilled-London-liters/dp/B06XDC7JVK/ref=sr_1_15?crid=KT86XE7Q1VOF&dib=eyJ2IjoiMSJ9.ylEaEXD1f5Y_7JZ4_niyxKwdzsplmbYN86sqXA3qe7_VqDwNubbbbIw3VKaSRLlZHIaKnbU_0CCsKGn_kb7E5zOO8xRjieU6wLq5b3ZA1uV2K_HxTb-eNmym3GG73ocaBR2kf2OCDYD_GFeP2doa7iUx2puBixvdKO-HiVOivjYEOjBqeL0_mJTRCvgGMGconqwHQAFTCCAsSOSGVOL1m1SZJAMnZFH4rmt7Ilk99396FIxsXve7-z3XGC0MNlTY11BokXS35y_ue5Yex-VLSE3BDJzvTZL7pt9UJDVgiBw.xjDuyO9rFnpm1v1fkOszwC9pBTeQLFsNDrMPvAYrEbI&dib_tag=se&keywords=gin&qid=1722056340&sprefix=gi%2Caps%2C329&sr=8-15");
//        insertPricingInsights(productMasterData);
    }

    void insertPricingInsights(ProductMasterDataDAO productMasterData) {
        //Current Timestamp
        Timestamp now = new Timestamp(System.currentTimeMillis());

        try {
            //Get the response document
            Document document = Jsoup.connect(productMasterData.getUrl()).get();

            // Get the price
            String itemPriceString = null == document.getElementById("corePriceDisplay_desktop_feature_div") ? "0" :
                    (document.getElementById("corePriceDisplay_desktop_feature_div")
                            .getElementsByClass("a-price-whole").get(0).childNode(0).toString());

            // Convert the price string to double
            Double itemPrice = Double.parseDouble(itemPriceString);

            // Generate the url for product image
            String imageRef = document.getElementById("imgTagWrapperId")
                    .getElementsByTag("img").attr("src");

            System.out.println("## AMAZON INSERT ##");
            //Insert into PricingInsights table
            ProductInsightsRepo.insertPricingInsights(productMasterData.getNo(), productMasterData.getTag(), productMasterData.getShopName(),
                    itemPrice, productMasterData.getUrl(), true, now, imageRef);

        } catch (IOException e) {

            //Insert into PricingInsights table
            ProductInsightsRepo.insertPricingInsights(productMasterData.getNo(), productMasterData.getTag(), productMasterData.getShopName(),
                    0.0, productMasterData.getUrl(), true, now, "");
            e.printStackTrace();
        }
    }

    public void scrapTest() throws JsonProcessingException, InterruptedException {

        String url = "https://www.amazon.co.uk/Gordons-London-Award-winning-Botanical-Litre/dp/B01HZ6YAUW";

        List<ProductMasterDataDAO> productMasterDataList = new ArrayList<>();

        // Filter the ProductMasterData table for "Amazon"
        productMasterDataList = ProductMasterDataRepo.getProductMasterDataByShopName("Amazon");

        ChromeOptions options = new ChromeOptions();

        WebDriver driver = new ChromeDriver(options);

        for (ProductMasterDataDAO productMasterData : productMasterDataList) {
            if (!productMasterData.getUrl().isEmpty())
                insertPricingInsights(productMasterData);
        }


        driver.get(url);

        WebElement cookieButton = driver.findElement(By.id("sp-cc-accept"));

        cookieButton.click();

        Thread.sleep(2000);

        WebElement button = driver.findElement(By.xpath("//*[@id=\"nav-main\"]/div[1]/div/div/div[3]/span[2]/span"));
        button.click();

        Thread.sleep(2000);

        WebElement inputField = driver.findElement(By.xpath("//*[@id=\"GLUXZipUpdateInput\"]"));
        inputField.sendKeys("OX1 1BS");

        WebElement changeLocationApplyButton = driver.findElement(By.xpath("//*[@id=\"GLUXZipUpdate\"]/span"));
        changeLocationApplyButton.click();

        Thread.sleep(500);

        driver.navigate().refresh();

        Thread.sleep(1500);


        driver.close();

    }
}