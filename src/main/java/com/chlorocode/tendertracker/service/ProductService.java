package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.dto.ProductSearchDTO;
import com.chlorocode.tendertracker.dao.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for marketplace product.
 */
public interface ProductService {

    /**
     * This method is used to create new product.
     *
     * @param product product object to be created
     * @return Product
     */
    Product createProduct(Product product);

    /**
     * This method is used to update product.
     *
     * @param product product object to be updated
     * @return Product
     */
    Product updateProduct(Product product);

    /**
     * This method is used to get all products with paging feature.
     *
     * @param pageable Pageable
     * @return page of product
     */
    Page<Product> listAllByPage(Pageable pageable);

    /**
     * This method is used to find a product by id.
     *
     * @param id unique identifier of the product
     * @return Product
     */
    Product findById(int id);

    /**
     * This method is used to search product based on search criteria.
     *
     * @param productSearchDTO DTO containing the search criteria
     * @param pageable Pageable
     * @return page of product
     * @see ProductSearchDTO
     */
    Page<Product> searchProduct(ProductSearchDTO productSearchDTO, Pageable pageable);

    /**
     * This method is used to blacklist a product.
     *
     * @param productCode unique identifier of the product
     * @return Product
     */
    Product blacklistProduct(int productCode);
}
