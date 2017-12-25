package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.Product;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductDAO extends DataTablesRepository<Product, Integer> {
    @Query(value = "SELECT * FROM Product", nativeQuery = true)
    List<Object[]> getProductsByCompany(int companyId);
}
