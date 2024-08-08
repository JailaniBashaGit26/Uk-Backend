package com.uk.uk.implementation;

import com.uk.uk.entity.PricingInsightsDAO;
import com.uk.uk.entity.DashboardGridDataDAO;
import com.uk.uk.entity.DashboardGridPriceUrlDAO;
import com.uk.uk.entity.ProductMasterDataDAO;
import com.uk.uk.repository.ProductMasterDataRepo;
import com.uk.uk.repository.PricingInsightsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class PricingInsightsImpl {
    @Autowired
    private ProductMasterDataRepo ProductMasterDataRepo;
    @Autowired
    private PricingInsightsRepo PricingInsightsRepo;

    public List<ProductMasterDataDAO> getProductMasterByTag(Integer tag) {
        List<ProductMasterDataDAO> productMasterDataDAOList = new ArrayList<>();
        productMasterDataDAOList = ProductMasterDataRepo.getProductMasterDataByTagNo(tag);
        return productMasterDataDAOList;
    }

    public List<DashboardGridDataDAO> getGridData() {
        List<PricingInsightsDAO> productInsightsDAOList = new ArrayList<>();
        productInsightsDAOList = PricingInsightsRepo.getAll();

        Map<Integer, List<PricingInsightsDAO>> productGroupByTag = productInsightsDAOList.stream().collect(Collectors.groupingBy(PricingInsightsDAO::getTag));

        List<DashboardGridDataDAO> gridDataList = new ArrayList<>();

        // Iterate over the map
        for (Map.Entry<Integer, List<PricingInsightsDAO>> entry : productGroupByTag.entrySet()) {
            Integer key = entry.getKey();
            List<PricingInsightsDAO> daoList = entry.getValue();

            DashboardGridDataDAO gridData = new DashboardGridDataDAO();
            List<ProductMasterDataDAO> productMasterDataDAOList = ProductMasterDataRepo.getProductMasterDataByTagNo(key);
            ProductMasterDataDAO productMasterDataDAO = productMasterDataDAOList.getFirst();

            Integer idx = 0;
            Double startingPrice = 0.00;
            List<String> lowestPriceShopNameList = new ArrayList<>();

            for (PricingInsightsDAO pricingInsights : daoList) {

                if (idx.intValue() == 0) {
                    gridData.setNo(productMasterDataDAO.getNo());
                    gridData.setTag(productMasterDataDAO.getTag());
                    gridData.setCategory(productMasterDataDAO.getCategory());
                    gridData.setProductName(productMasterDataDAO.getProductName());
                    gridData.setQuantity(productMasterDataDAO.getQuantity());
                    gridData.setMeasurement(productMasterDataDAO.getMeasurement());
                    gridData.setSize(productMasterDataDAO.getQuantity().toString() + " " + productMasterDataDAO.getMeasurement());
                }

                DashboardGridPriceUrlDAO dashboardGridPriceUrlDAO = new DashboardGridPriceUrlDAO();
                dashboardGridPriceUrlDAO.setPrice(pricingInsights.getPrice());
                dashboardGridPriceUrlDAO.setUrl(pricingInsights.getUrl());

                if (startingPrice == 0.00) {
                    startingPrice = pricingInsights.getPrice();
                    lowestPriceShopNameList.add(pricingInsights.getShopName());
                } else {
                    if (pricingInsights.getPrice() <= startingPrice && pricingInsights.getPrice() != 0) {
                        if (pricingInsights.getPrice() < startingPrice) {
                            lowestPriceShopNameList.clear();
                            lowestPriceShopNameList.add(pricingInsights.getShopName());
                        } else
                            lowestPriceShopNameList.add(pricingInsights.getShopName());
                        startingPrice = pricingInsights.getPrice();
                    }
                }

                if (null != pricingInsights.getImageRef() && !pricingInsights.getImageRef().isEmpty() &&
                        !pricingInsights.getShopName().equalsIgnoreCase("Amazon"))
                    gridData.setImageUrl(pricingInsights.getImageRef());

                switch (pricingInsights.getShopName()) {

                    case "Morrisons":
                        gridData.setMorrisons(dashboardGridPriceUrlDAO);
                        break;
                    case "Tesco":
                        gridData.setTesco(dashboardGridPriceUrlDAO);
                        break;
                    case "Amazon":
                        gridData.setAmazon(dashboardGridPriceUrlDAO);
                        break;
                    case "Sainsburys":
                        gridData.setSainsburys(dashboardGridPriceUrlDAO);
                        break;
                    case "CoOp":
                        gridData.setCoop(dashboardGridPriceUrlDAO);
                        break;
                    case "Ocado":
                        gridData.setOcado(dashboardGridPriceUrlDAO);
                        break;
                    case "WaitRose":
                        gridData.setWaitrose(dashboardGridPriceUrlDAO);
                        break;
                    case "ASDA":
                        gridData.setAsda(dashboardGridPriceUrlDAO);
                        break;

                }

                idx++;

            }

            gridData.setLowestPriceShopNameList(lowestPriceShopNameList);
            gridDataList.add(gridData);
        }


        return gridDataList;
    }

    public Boolean deletePricingInsightsByTag(Integer tag) {
        Boolean deleteStatus = false;

        try {
            PricingInsightsRepo.deletePricingInsightsByTag(tag);
            deleteStatus = true;
        } catch (Exception e) {
            System.out.println("Delete Pricing Insights By Tag Error : " + e);
            deleteStatus = false;
        }

        return deleteStatus;

    }
}
