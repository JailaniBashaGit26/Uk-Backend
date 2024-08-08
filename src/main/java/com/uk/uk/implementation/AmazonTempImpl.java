package com.uk.uk.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.util.List;

@Service
public class AmazonTempImpl {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ProductMasterDataRepo productMasterDataRepo;

    @Autowired
    private PricingInsightsRepo productInsightsRepo;

    public void getProductDetails() throws JsonProcessingException, InterruptedException {

        List<ProductMasterDataDAO> productMasterDataList = productMasterDataRepo.getProductMasterDataByShopName("Amazon");

        Integer idx = 0;

        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("-headless");

        // Open the Chrome Driver
        WebDriver driver = new FirefoxDriver(options);

        for (ProductMasterDataDAO productMasterData : productMasterDataList) {
            insertPricingInsights(driver, idx, productMasterData);
            idx++;
        }

        driver.close();
    }

    public void insertPricingInsights(WebDriver driver, Integer idx, ProductMasterDataDAO productMasterData) throws InterruptedException {

        if (idx == 0) {

//            driver.get("https://www.amazon.co.uk/");

            driver.get(productMasterData.getUrl());

            Thread.sleep(2000);

//            WebElement cookieButton = driver.findElement(By.id("sp-cc-accept"));
//            // Click on the cookie button
//            cookieButton.click();

            WebElement locationButton = driver.findElement(By.id("nav-global-location-popover-link"));
            // Click on the cookie button
            locationButton.click();

            Thread.sleep(1000);

            WebElement addressBox = driver.findElement(By.id("Condo"));
            // Click on the cookie button
            WebElement textBox = addressBox.findElement(By.id("GLUXZipUpdateInput"));
            textBox.clear();
            textBox.sendKeys("W1A 1AA");
            Thread.sleep(2000);

            WebElement applyButton = driver.findElement(By.id("GLUXZipUpdate"));

            // Click on the cookie button
            applyButton.click();

            Thread.sleep(1000);

            driver.navigate().refresh();

            Thread.sleep(1500);

            try {
                // Locate the cookie button by XPath
                WebElement cookieButton = driver.findElement(By.xpath("//*[@data-testid=\"reject-all\"]"));
                // Click on the cookie button
                cookieButton.click();
            } catch (Exception e) {
                System.out.println("Error in Cookie Button");
            }

        }


        // Navigate to the product URL
//        driver.get(productMasterData.getUrl());

        // Current Timestamp
        Timestamp now = new Timestamp(System.currentTimeMillis());

        // If the idx value is 0, then we need to click the "I Accept" cookie button
//        if (idx == 0) {
//            // Wait for 3 sec to load the Cookie tag in browser
//            Thread.sleep(3000);
//
//            try {
//                // Locate the cookie button by XPath
//                WebElement cookieButton = driver.findElement(By.xpath("//*[@data-testid=\"reject-all\"]"));
//                // Click on the cookie button
//                cookieButton.click();
//            } catch (Exception e) {
//                System.out.println("Error in Cookie Button");
//            }
//        }

        // Wait for 1 sec for closing the Cookie tag
        Thread.sleep(1000);

        try {
            // Get the price by using the XPath
            String itemPriceString = null != driver.findElements(By.className("a-price-whole")) && null != driver.findElements(By.className("a-price-whole")).get(0) ?
                    driver.findElements(By.className("a-price-whole")).get(0).getText() : "0";
            String fractionalValue = null != driver.findElements(By.className("a-price-fraction")) && null != driver.findElements(By.className("a-price-fraction")).get(0) ?
                    driver.findElements(By.className("a-price-fraction")).get(0).getText() : String.valueOf('0');
            itemPriceString = itemPriceString + '.' + fractionalValue;
            // Convert the price string to double
            Double itemPrice = Double.parseDouble(itemPriceString);

            // Locate the image element
            WebElement imageElement = driver.findElement(By.id("landingImage"));

            // Get the src attribute value
            String imageRef = imageElement.getAttribute("src");

            // Insert into PricingInsights table
            productInsightsRepo.insertPricingInsights(productMasterData.getNo(), productMasterData.getTag(), productMasterData.getShopName(),
                    itemPrice, productMasterData.getUrl(), true, now, imageRef);

        } catch (Exception e) {
            // Insert into PricingInsights table
            productInsightsRepo.insertPricingInsights(productMasterData.getNo(), productMasterData.getTag(), productMasterData.getShopName(),
                    0.0, productMasterData.getUrl(), true, now, "");
            System.out.println("Error URL :" + productMasterData.getUrl());
        }
        idx++;
    }
}