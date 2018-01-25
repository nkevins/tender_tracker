package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.entity.Product;
import com.chlorocode.tendertracker.dao.entity.ProductClarification;

import java.util.List;

/**
 * Created by andy on 18/1/2018.
 */
public interface ProductClarificationService {
    ProductClarification findById(int id);
    ProductClarification create(ProductClarification product);
    List<ProductClarification> findClarificationByProdId(int prodId);
    ProductClarification updateResponse(int id, String response);
}
