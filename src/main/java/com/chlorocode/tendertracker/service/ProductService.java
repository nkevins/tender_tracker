package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.dto.ProductSearchDTO;
import com.chlorocode.tendertracker.dao.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    Product createProduct(Product product, List<MultipartFile> attachments);
    Page<Product> listAllByPage(Pageable pageable);
    Product findById(int id);
    Page<Product> searchProduct(ProductSearchDTO productSearchDTO, Pageable pageable);
}
