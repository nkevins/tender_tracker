package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.entity.Product;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    Product createProduct(Product product, List<MultipartFile> attachments);
}
