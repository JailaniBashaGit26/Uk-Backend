package com.uk.uk.controller;

import com.uk.uk.entity.DashboardGridDataDAO;
import com.uk.uk.entity.ProductMasterDataDAO;
import com.uk.uk.implementation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.uk.uk.repository.PricingInsightsRepo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class Controller {
    @Autowired
    private PricingInsightsRepo ProductInsightsRepo;
    @Autowired
    private AsdaImpl AsdaImpl;
    @Autowired
    private MorrisonsImpl MorrisonsImpl;
    @Autowired
    private SainsburysImpl SainsburysImpl;
    @Autowired
    private TescoImpl TescoImpl;
    @Autowired
    private ProductMasterDataImpl ProductMasterDataImpl;
    @Autowired
    private PricingInsightsImpl PricingInsightsImpl;
    @Autowired
    private WaitRoseImpl WaitRoseImpl;
    @Autowired
    private AmazonImpl AmazonImpl;
    @Autowired
    private OcadoImpl OcadoImpl;
    @Autowired
    private CoOpImpl CoOpImpl;
    @Autowired
    private AmazonTempImpl AmazonTempImpl;

    @GetMapping("/insertPricingInsights")
    public Boolean insertPricingInsights() throws IOException, InterruptedException {
        MorrisonsImpl.getProductDetails();
        SainsburysImpl.getProductDetails();
        TescoImpl.getProductDetails();
        WaitRoseImpl.getProductDetails();
//        AmazonImpl.getProductDetails();
        AmazonTempImpl.getProductDetails();
        OcadoImpl.getProductDetails();
        CoOpImpl.getProductDetails();
        AsdaImpl.getProductDetails();
        return true;
    }

    @PostMapping("/insertProductMasterData")
    public Boolean insertProductMasterData(@RequestBody List<ProductMasterDataDAO> ProductMasterDataDAOList) throws IOException, InterruptedException {
        ProductMasterDataImpl.insertProductMasterData(ProductMasterDataDAOList);
        return true;
    }

    @GetMapping("/getGridData")
    public List<DashboardGridDataDAO> getUser() {
        List<DashboardGridDataDAO> gridDataList = new ArrayList<>();
        gridDataList = PricingInsightsImpl.getGridData();
        return gridDataList;
    }

    @GetMapping("/getProductMasterByTag")
    public List<ProductMasterDataDAO> getProductMasterByTag(@RequestParam("tag") Integer tag) {
        List<ProductMasterDataDAO> productMasterDataDAOList = new ArrayList<>();
        productMasterDataDAOList = PricingInsightsImpl.getProductMasterByTag(tag);
        return productMasterDataDAOList;
    }

    @PostMapping("/editProductMasterByTag")
    public Boolean editProductMasterByTag(@RequestBody List<ProductMasterDataDAO> ProductMasterDataDAOList) throws InterruptedException {
        Boolean updateStatus = false;
        updateStatus = ProductMasterDataImpl.updateProductMasterByTag(ProductMasterDataDAOList);
        return updateStatus;
    }

    @GetMapping("/hideProductByTag")
    public Boolean hideProductByTag(@RequestParam("tag") Integer tag) {
        ProductMasterDataImpl.hideProductByTag(tag);
        PricingInsightsImpl.deletePricingInsightsByTag(tag);
        return true;
    }

    @GetMapping("/test")
    public Boolean test() throws IOException, InterruptedException {
//        AmazonTempImpl.getProductDetails();
        SainsburysImpl.test();
        return true;
    }
}
