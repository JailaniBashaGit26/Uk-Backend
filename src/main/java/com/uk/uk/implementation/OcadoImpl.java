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
public class OcadoImpl {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    ProductMasterDataRepo ProductMasterDataRepo;

    @Autowired
    PricingInsightsRepo ProductInsightsRepo;

    public void getProductDetails() throws JsonProcessingException {
        List<ProductMasterDataDAO> productMasterDataList = new ArrayList<>();

        // Filter the ProductMasterData table for "OCADO"
        productMasterDataList = ProductMasterDataRepo.getProductMasterDataByShopName("OCADO");

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
            String itemPriceString = document.getElementsByClass("bop-price__current")
                    .get(0).childNode(0).attributes().get("content");

            // Convert the price string to double
            Double itemPrice = Double.parseDouble(itemPriceString);

            // Generate the url for product image
            String imageRef = "https://www.ocado.com/" + document.getElementsByClass("bop-galleryWrapper")
                    .get(0).getElementsByTag("img").attr("src");

            //Insert into PricingInsights table
            ProductInsightsRepo.insertPricingInsights(productMasterData.getNo(), productMasterData.getTag(), productMasterData.getShopName(),
                    itemPrice, productMasterData.getUrl(), true, now, imageRef);
        } catch (IOException e) {

            //Insert into PricingInsights table and set price as 0.0 and availability as false.
            ProductInsightsRepo.insertPricingInsights(productMasterData.getNo(), productMasterData.getTag(), productMasterData.getShopName(),
                    0.0, productMasterData.getUrl(), true, now, "");

            e.printStackTrace();
        }
    }
}