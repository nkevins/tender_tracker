package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.ProductClarification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by andy on 18/1/2018.
 */
@Repository
public interface ProductClarificationDAO extends JpaRepository<ProductClarification, Integer> {


}

