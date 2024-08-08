package com.uk.uk.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.uk.uk.entity.ProductMasterDataDAO;
import com.uk.uk.repository.PricingInsightsRepo;
import com.uk.uk.repository.ProductMasterDataRepo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
public class MorrisonsImpl {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    ProductMasterDataRepo ProductMasterDataRepo;
    @Autowired
    PricingInsightsRepo ProductInsightsRepo;

    public void getProductDetails() throws JsonProcessingException {
        List<ProductMasterDataDAO> productMasterDataList = new ArrayList<>();

        // Filter the ProductMasterData table for "Morrisons"
        productMasterDataList = ProductMasterDataRepo.getProductMasterDataByShopName("Morrisons");

        for (ProductMasterDataDAO productMasterData : productMasterDataList) {
            if (!productMasterData.getUrl().isEmpty())
                insertPricingInsights(productMasterData);
        }
    }

    public void insertPricingInsights(ProductMasterDataDAO productMasterData) {

        //Current Timestamp
        Timestamp now = new Timestamp(System.currentTimeMillis());

        // Split the url with urlSplitByHyphen (-)
        String[] urlSplitByHyphen = productMasterData.getUrl().split("-");

        // Get the pid value from splitted array
        String pid = urlSplitByHyphen[urlSplitByHyphen.length - 1];

        // Generate the url with the dynamic pid value
        String url = "https://groceries.morrisons.com/products/dummy-" + pid;
        try {

            //Get the response document
            Document document = Jsoup.connect(url).get();

            // Get the price
            String itemPriceString = document.getElementsByClass("bop-price__current").get(0).childNode(2)
                    .toString().split("£")[1];

            // Convert the price string to double
            Double itemPrice = Double.parseDouble(itemPriceString);

            // Generate the url for product image
            String imageRef = "https://groceries.morrisons.com/productImages/" + pid.substring(0, Math.min(pid.length(), 3)) + "/" +
                    pid + "_0_640x640.jpg";

            //Insert into PricingInsights table
            ProductInsightsRepo.insertPricingInsights(productMasterData.getNo(), productMasterData.getTag(), productMasterData.getShopName(),
                    itemPrice, productMasterData.getUrl(), true, now, imageRef);
        } catch (IOException e) {

            //Insert into PricingInsights table and set price as 0.0 and availability as false.
            ProductInsightsRepo.insertPricingInsights(productMasterData.getNo(), productMasterData.getTag(), productMasterData.getShopName(),
                    0.0, productMasterData.getUrl(), false, now, "");

            e.printStackTrace();
        }
    }

}
