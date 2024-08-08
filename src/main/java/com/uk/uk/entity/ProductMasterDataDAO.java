package com.uk.uk.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.cache.annotation.Cacheable;

import jakarta.persistence.*;

@Entity
@Table(name="ProductMasterData")
@Getter
@Setter
@Cacheable
@NoArgsConstructor

public class ProductMasterDataDAO {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    public Integer no;
    public String shopName;
    public String productName;
    public Integer quantity;
    public String measurement;
    public String category;

    @Column(name = "Url", columnDefinition = "NVARCHAR(MAX)")
    public String url;
    public Integer tag;
    public Boolean tagStatus;

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



    public Boolean getTagStatus() {
        return tagStatus;
    }

    public void setTagStatus(Boolean tagStatus) {
        this.tagStatus = tagStatus;
    }

    public Integer getTag() {
        return tag;
    }

    public void setTag(Integer tag) {
        this.tag = tag;
    }

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
