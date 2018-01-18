package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.Clarification;
import com.chlorocode.tendertracker.dao.entity.Product;
import com.chlorocode.tendertracker.dao.entity.ProductClarification;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by andy on 18/1/2018.
 */
public interface ProductClarificationDAO extends JpaRepository<ProductClarification, Integer> {


}

