package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.Clarification;
import com.chlorocode.tendertracker.dao.entity.ProductClarification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by andy on 18/1/2018.
 */
@Repository
public interface ProductClarificationDAO extends JpaRepository<ProductClarification, Integer> {

    @Query("select r from ProductClarification r where r.product.productCode = ?1 order by r.createdDate desc")
    List<ProductClarification> findClarificationByProdId(int prodId);
}

