package com.uk.uk.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.cache.annotation.Cacheable;

import com.uk.uk.entity.DashboardGridPriceUrlDAO;

import java.util.List;

@Getter
@Setter
@Cacheable
@NoArgsConstructor
public class DashboardGridDataDAO {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer no;
    public Integer tag;
    public String category;
    public String productName;
    public Integer quantity;
    public String measurement;
    public String size;
    public String imageUrl;
    public List<String> lowestPriceShopNameList;
    public DashboardGridPriceUrlDAO asda;
    public DashboardGridPriceUrlDAO morrisons;
    public DashboardGridPriceUrlDAO sainsburys;
    public DashboardGridPriceUrlDAO tesco;
    public DashboardGridPriceUrlDAO coop;
    public DashboardGridPriceUrlDAO ocado;
    public DashboardGridPriceUrlDAO waitrose;
    public DashboardGridPriceUrlDAO amazon;

    public List<String> getLowestPriceShopNameList() {
        return lowestPriceShopNameList;
    }

    public void setLowestPriceShopNameList(List<String> lowestPriceShopNameList) {
        this.lowestPriceShopNameList = lowestPriceShopNameList;
    }

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    public Integer getTag() {
        return tag;
    }

    public void setTag(Integer tag) {
        this.tag = tag;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getMeasurement() {
        return measurement;
    }

    public void setMeasurement(String measurement) {
        this.measurement = measurement;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public DashboardGridPriceUrlDAO getAsda() {
        return asda;
    }

    public void setAsda(DashboardGridPriceUrlDAO asda) {
        this.asda = asda;
    }

    public DashboardGridPriceUrlDAO getMorrisons() {
        return morrisons;
    }

    public void setMorrisons(DashboardGridPriceUrlDAO morrisons) {
        this.morrisons = morrisons;
    }

    public DashboardGridPriceUrlDAO getSainsburys() {
        return sainsburys;
    }

    public void setSainsburys(DashboardGridPriceUrlDAO sainsburys) {
        this.sainsburys = sainsburys;
    }

    public DashboardGridPriceUrlDAO getTesco() {
        return tesco;
    }

    public void setTesco(DashboardGridPriceUrlDAO tesco) {
        this.tesco = tesco;
    }

    public DashboardGridPriceUrlDAO getCoop() {
        return coop;
    }

    public void setCoop(DashboardGridPriceUrlDAO coop) {
        this.coop = coop;
    }

    public DashboardGridPriceUrlDAO getOcado() {
        return ocado;
    }

    public void setOcado(DashboardGridPriceUrlDAO ocado) {
        this.ocado = ocado;
    }

    public DashboardGridPriceUrlDAO getWaitrose() {
        return waitrose;
    }

    public void setWaitrose(DashboardGridPriceUrlDAO waitrose) {
        this.waitrose = waitrose;
    }

    public DashboardGridPriceUrlDAO getAmazon() {
        return amazon;
    }

    public void setAmazon(DashboardGridPriceUrlDAO amazon) {
        this.amazon = amazon;
    }
}
