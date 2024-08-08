package com.uk.uk.implementation;

import com.uk.uk.entity.ProductMasterDataDAO;
import com.uk.uk.repository.PricingInsightsRepo;
import com.uk.uk.repository.ProductMasterDataRepo;
import lombok.AllArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.devtools.v96.browser.Browser;
import org.openqa.selenium.devtools.v96.browser.model.WindowID;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.Dimension;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.sql.Timestamp;
import java.time.Duration;
import java.util.*;
import java.util.List;

@Service
@AllArgsConstructor
public class SainsburysImpl {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    ProductMasterDataRepo ProductMasterDataRepo;
    @Autowired
    PricingInsightsRepo ProductInsightsRepo;

    public void getProductDetails() throws InterruptedException {
        List<ProductMasterDataDAO> productMasterDataList = new ArrayList<>();

        // Filter the ProductMasterData table for "Sainsburys"
        productMasterDataList = ProductMasterDataRepo.getProductMasterDataByShopName("Sainsburys");

        // Set up FirefoxOptions for headless mode
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("-headless");// Hide Firefox GUI

        // Open the Firefox Driver
        WebDriver driver = new FirefoxDriver(options);

        Integer idx = 0;

        for (ProductMasterDataDAO productMasterData : productMasterDataList) {
            if (!productMasterData.getUrl().isEmpty())
                insertPricingInsights(productMasterData, driver, idx);
        }
        driver.close();
    }

    public void insertPricingInsights(ProductMasterDataDAO productMasterData, WebDriver driver, Integer idx) throws InterruptedException {

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
                WebElement cookieButton = driver.findElement(By.id("onetrust-accept-btn-handler"));

                // Click on the cookie button
                cookieButton.click();
                Thread.sleep(3000);

            } catch (Exception e) {
                System.out.println("Error in Cookie Button");
            }
        }

        // Wait for 3 sec for closing the Cookie tag
        Thread.sleep(3000);

        try {

            // Get the price by using the className
            String itemPriceString = driver.findElement(By.className("pd__cost__retail-price")).getText().split("£")[1];

            // Convert the price string to double
            Double itemPrice = Double.parseDouble(itemPriceString);

            // Locate the image element
            WebElement imageElement = driver.findElement(By.cssSelector(".pd__image"));

            // Get the src attribute value
            String imageRef = imageElement.getAttribute("src");

            //Insert into PricingInsights table
            ProductInsightsRepo.insertPricingInsights(productMasterData.getNo(), productMasterData.getTag(),
                    productMasterData.getShopName(), itemPrice, productMasterData.getUrl(), true, now, imageRef);

        } catch (Exception e) {
            //Insert into PricingInsights table
            ProductInsightsRepo.insertPricingInsights(productMasterData.getNo(), productMasterData.getTag(),
                    productMasterData.getShopName(), 0.0, productMasterData.getUrl(), false, now, "");

            System.out.println("Error URL :" + productMasterData.getUrl());
        }
        idx++;
    }

    public void test() throws InterruptedException {

        System.setProperty("webdriver.gecko.driver", "D:\\Pixmonks Backup\\Uk\\Git\\Uk-Backend\\drivers\\geckodriver.exe");

//        FirefoxOptions options = new FirefoxOptions();
//        options.setHeadless(true); // Enable headless mode

        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("-headless"); // Add headless argumen

        WebDriver driver = new FirefoxDriver(options);

        // Navigate to a URL
        driver.get("https://www.tesco.com/groceries/en-GB/products/257546202");

        Thread.sleep(3000);

        // Use WebDriverWait to wait for the element to be present
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
//        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".eNIEDh")));
//        System.out.println("Element Text: " + element.getText());

        String itemPriceString = driver.findElement(By.cssSelector(".eNIEDh")).getText().split("£")[1];

//        WebElement textBox = driver.findElement(By.id("SIvCob"));
        System.out.println("#### >> " + itemPriceString);
//
//        // Fetch page title or other data
//        System.out.println("##### >>Page Title: " + driver.getTitle());

        // Quit the driver
        driver.quit();
    }

}
