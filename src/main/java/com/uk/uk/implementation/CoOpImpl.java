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
public class CoOpImpl {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    ProductMasterDataRepo ProductMasterDataRepo;

    @Autowired
    PricingInsightsRepo ProductInsightsRepo;

    public void getProductDetails() throws JsonProcessingException {
        List<ProductMasterDataDAO> productMasterDataList = new ArrayList<>();

        // Filter the ProductMasterData table for "COOP"
        productMasterDataList = ProductMasterDataRepo.getProductMasterDataByShopName("COOP");

        for (ProductMasterDataDAO productMasterData : productMasterDataList) {
            if (!productMasterData.getUrl().isEmpty())
                insertPricingInsights(productMasterData);
        }
    }

    void insertPricingInsights(ProductMasterDataDAO productMasterData) {

        //Current Timestamp
        Timestamp now = new Timestamp(System.currentTimeMillis());

        try {

            //Get the response document
            Document document = Jsoup.connect(productMasterData.getUrl()).get();

            // Get the price
            String itemPriceString = document.getElementsByClass("coop-c-card__price")
                    .get(0).childNode(1).childNode(0)
                    .toString().split("£")[1];

//            String itemPriceString = document.getElementsByClass("bop-price__current ").text().toString().split("£")[1];

            // Convert the price string to double
            Double itemPrice = Double.parseDouble(itemPriceString);

            // Generate the url for product image
            String imageRef = document.getElementsByClass("coop-c-gallery__image")
                    .get(0).getElementsByTag("img").attr("src");

//                String imageRef = "https://www.ocado.com/";
//            imageRef = imageRef + document.getElementsByClass("bop-gallery__image").get(0).getElementsByTag("img").attr("src");


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