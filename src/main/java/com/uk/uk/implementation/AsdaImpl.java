package com.uk.uk.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.uk.uk.repository.ProductMasterDataRepo;
import com.uk.uk.repository.PricingInsightsRepo;
import com.uk.uk.entity.ProductMasterDataDAO;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class AsdaImpl {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    ProductMasterDataRepo ProductMasterDataRepo;
    @Autowired
    PricingInsightsRepo ProductInsightsRepo;

    // Url for the POST Api
    String url = "https://groceries.asda.com/api/bff/graphql";

    public void getProductDetails() throws JsonProcessingException {
        List<ProductMasterDataDAO> productMasterDataList = new ArrayList<>();

        // Filter the ProductMasterData table for "ASDA"
        productMasterDataList = ProductMasterDataRepo.getProductMasterDataByShopName("ASDA");

        for (ProductMasterDataDAO productMasterData : productMasterDataList) {
            if (!productMasterData.getUrl().isEmpty())
                insertPricingInsights(productMasterData);
        }
    }

    private HttpHeaders asdaHeader() {
        // Declaring and set Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Request-Origin", "gi");
        return headers;
    }

    public void insertPricingInsights(ProductMasterDataDAO productMasterData) {

        //Current Timestamp
        Timestamp now = new Timestamp(System.currentTimeMillis());

        // Split the url with slash
        String[] urlSplitBySlash = productMasterData.getUrl().split("/");

        // Get the pid value from splitted array
        String pid = urlSplitBySlash[urlSplitBySlash.length - 1];

        // Generate the request body with the dynamic pid value
        String requestBody = "{\n" +
                "    \"requestorigin\": \"gi\",\n" +
                "    \"contract\": \"web/cms/product-details-page\",\n" +
                "    \"variables\": {\n" +
                "        \"is_eat_and_collect\": false,\n" +
                "        \"store_id\": \"4565\",\n" +
                "        \"type\": \"content\",\n" +
                "        \"request_origin\": \"gi\",\n" +
                "        \"payload\": {\n" +
                "            \"page_id\": \"" + pid + "\",\n" +
                "            \"page_type\": \"productDetailsPage\",\n" +
                "            \"page_meta_info\": true\n" +
                "        }\n" +
                "    }\n" +
                "}";

        // Generate the HttpEntity
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, asdaHeader());

        try {
            // Hit the external Api and get the response
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            // Get the response body
            String responseBody = response.getBody();

            // Map the response with the object mapper
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> map = mapper.readValue(responseBody, Map.class);

            // Search for the "price" in the response
            Map<String, Object> dataMap = (Map<String, Object>) map.get("data");
            Map<String, Object> tempoCmsContentMap = (Map<String, Object>) dataMap.get("tempo_cms_content");
            ArrayList zonesList = (ArrayList) tempoCmsContentMap.get("zones");
            Map<String, Object> zonesFirstIdx = (Map<String, Object>) zonesList.getFirst();
            Map<String, Object> configsMap = (Map<String, Object>) zonesFirstIdx.get("configs");
            Map<String, Object> productsMap = (Map<String, Object>) configsMap.get("products");
            ArrayList itemsMap = (ArrayList) productsMap.get("items");
            Map<String, Object> itemsFirstIdx = (Map<String, Object>) itemsMap.getFirst();
            Map<String, Object> priceMap = (Map<String, Object>) itemsFirstIdx.get("price");
            Map<String, Object> priceInfoMap = (Map<String, Object>) priceMap.get("price_info");

            // Get the price
            String itemPriceString = priceInfoMap.get("price").toString().split("£")[1];

            Map<String, Object> itemMap = (Map<String, Object>) itemsFirstIdx.get("item");
            ArrayList upcNumberList = (ArrayList) itemMap.get("upc_numbers");

            // Generate the url for product image
            String imageRef = "https://ui.assets-asda.com/dm/asdagroceries/" + upcNumberList.getFirst().toString() +
                    "_T1?defaultImage=asdagroceries/noImage&resMode=sharp2&id=InbSJ1&fmt=webp&dpr=off&fit=constrain,1&wid=188&hei=188";

            // Convert the price string to double
            Double itemPrice = Double.parseDouble(itemPriceString);

            //Insert into PricingInsights table
            ProductInsightsRepo.insertPricingInsights(productMasterData.getNo(), productMasterData.getTag(), productMasterData.getShopName(),
                    itemPrice, productMasterData.getUrl(), true, now, imageRef);

        } catch (Exception e) {

            //Insert into PricingInsights table and set price as 0.0 and availability as false.
            ProductInsightsRepo.insertPricingInsights(productMasterData.getNo(), productMasterData.getTag(), productMasterData.getShopName(),
                    0.0, productMasterData.getUrl(), false, now, "");

            System.out.println("Error URL :" + productMasterData.getUrl());
        }
    }
}
