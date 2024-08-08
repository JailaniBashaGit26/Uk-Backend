package com.uk.uk.repository;

import com.uk.uk.entity.ProductMasterDataDAO;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.transaction.Transactional;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;

public interface ProductMasterDataRepo extends JpaRepository<ProductMasterDataDAO, Integer> {

    @Modifying
    @Query(value = "Insert into ProductMasterData (ProductName,Quantity,Measurement,ShopName,Url,Category,Tag,TagStatus) values" +
            "(?1,?2,?3,?4,?5,?6,?7,?8)", nativeQuery = true)
    @Transactional
    void insertProductMasterData(String productName, Integer quantity, String measurement, String shopName, String url,
                                 String category, Integer tag, Boolean tagStatus);

    @Modifying
    @Query(value = "update ProductMasterData set ProductName=?2,Quantity=?3, Measurement=?4, " +
            " Url=?5, Category=?6 where Tag=?1 and ShopName=?7", nativeQuery = true)
    @Transactional
    void updateProductMasterData(Integer tag, String productName, Integer quantity, String measurement, String url,
                                 String category, String shopName);

    @Query(value = "SELECT * FROM ProductMasterData where ShopName=?1 and TagStatus=1", nativeQuery = true)
    List<ProductMasterDataDAO> getProductMasterDataByShopName(String shopName);

    @Query(value = "SELECT * FROM ProductMasterData where ShopName=?1 and tag=?2 and TagStatus=1", nativeQuery = true)
    ProductMasterDataDAO getProductMasterDataByShopNameAndTag(String shopName, Integer tag);

    @Query(value = "SELECT MAX(Tag) FROM ProductMasterData", nativeQuery = true)
    Integer getMaxTagNo();

    @Query(value = "SELECT * FROM ProductMasterData where Tag=?1 and TagStatus=1", nativeQuery = true)
    List<ProductMasterDataDAO> getProductMasterDataByTagNo(Integer tag);

    @Modifying
    @Query(value = "update ProductMasterData set TagStatus=0 where Tag=?1", nativeQuery = true)
    @Transactional
    void updateTagStatus(Integer tagStatus);

}
