package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.Product;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ProductPagingDAO extends PagingAndSortingRepository<Product, Integer>, JpaSpecificationExecutor<Product> {
}
