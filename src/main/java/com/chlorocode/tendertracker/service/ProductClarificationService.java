package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.entity.ProductClarification;

import java.util.List;

/**
 * Service interface for product clarification.
 */
public interface ProductClarificationService {

    /**
     * This method is used to get a product clarification by id.
     *
     * @param id unique identifier of the product clarification
     * @return ProductClarification
     */
    ProductClarification findById(int id);

    /**
     * This method is used to save new product clarification.
     *
     * @param product product clarification object to be saved
     * @return ProductClarification
     */
    ProductClarification create(ProductClarification product);

    /**
     * This method is used to find all clarifications for a particular product.
     *
     * @param prodId unique identifier of the product
     * @return list of ProductClarification
     */
    List<ProductClarification> findClarificationByProdId(int prodId);

    /**
     * This method is used to update product clarification response.
     *
     * @param id unique identifier of the product clarification
     * @param response clarification response
     * @return ProductClarification
     */
    ProductClarification updateResponse(int id, String response);
}
