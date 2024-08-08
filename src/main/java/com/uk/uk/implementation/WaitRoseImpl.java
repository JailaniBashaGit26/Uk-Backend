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
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
public class WaitRoseImpl {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    ProductMasterDataRepo ProductMasterDataRepo;
    @Autowired
    PricingInsightsRepo ProductInsightsRepo;

    public void getProductDetails() throws JsonProcessingException, InterruptedException {
        List<ProductMasterDataDAO> productMasterDataList = new ArrayList<>();

        // Filter the ProductMasterData table for "WaitRose"
        productMasterDataList = ProductMasterDataRepo.getProductMasterDataByShopName("WaitRose");

        // Set up FirefoxOptions for headless mode
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("-headless");

        // Open the Chrome Driver
        WebDriver driver = new FirefoxDriver(options);

        Integer idx = 0;

        for (ProductMasterDataDAO productMasterData : productMasterDataList) {
            if (!productMasterData.getUrl().isEmpty())
                insertPricingInsights(productMasterData, driver, idx);
        }

        driver.close();

    }

    void insertPricingInsights(ProductMasterDataDAO productMasterData, WebDriver driver, Integer idx) throws InterruptedException {
        // Navigate to the product url
        driver.get(productMasterData.getUrl());

        //Current Timestamp
        Timestamp now = new Timestamp(System.currentTimeMillis());

        // If the idx value is 0, then we need to click the "I Accept" cookie button
        if (idx == 0) {
            // Wait for 3 sec to load the Cookie tag in browser
            Thread.sleep(3000);

            try {
                // Locate the cookie button by Id
                WebElement cookieButton = driver.findElement(By.xpath("//*[@data-testid=\"reject-all\"]"));
                // Click on the cookie button
                cookieButton.click();
            } catch (Exception e) {
                System.out.println("Error in Cookie Button");
            }
        }

        // Wait for 1 sec for closing the Cookie tag
        Thread.sleep(1000);

        try {

            // Get the price by using the className
            String itemPriceString = driver.findElement(By.xpath("//*[@data-test='product-pod-price']")).getText().split("£")[1];
            // Convert the price string to double
            Double itemPrice = Double.parseDouble(itemPriceString);

            // Locate the image element
            WebElement imageElement = driver.findElement(By.id("productImage")).findElement(By.tagName("img"));

            // Get the src attribute value
            String imageRef = imageElement.getAttribute("src");

            //Insert into PricingInsights table
            ProductInsightsRepo.insertPricingInsights(productMasterData.getNo(), productMasterData.getTag(), productMasterData.getShopName(),
                    itemPrice, productMasterData.getUrl(), true, now, imageRef);

        } catch (Exception e) {

            //Insert into PricingInsights table and set price as 0.0 and availability as false.
            ProductInsightsRepo.insertPricingInsights(productMasterData.getNo(), productMasterData.getTag(), productMasterData.getShopName(),
                    0.0, productMasterData.getUrl(), true, now, "");

            System.out.println("Error URL :" + productMasterData.getUrl());
        }
        idx++;
    }
}