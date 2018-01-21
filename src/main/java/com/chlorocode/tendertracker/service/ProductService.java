package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.dto.ProductSearchDTO;
import com.chlorocode.tendertracker.dao.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    Product createProduct(Product product);
    Product updateProduct(Product product);
    Page<Product> listAllByPage(Pageable pageable);
    Product findById(int id);
    Page<Product> searchProduct(ProductSearchDTO productSearchDTO, Pageable pageable);
    Product blacklistProduct(int productCode);
}
