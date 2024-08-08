package com.uk.uk.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.cache.annotation.Cacheable;

@Getter
@Setter
@Cacheable
@NoArgsConstructor
public class DashboardGridPriceUrlDAO {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Double price;
    public String url;

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
