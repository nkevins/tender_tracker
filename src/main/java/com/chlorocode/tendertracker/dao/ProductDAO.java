package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.Product;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductDAO extends DataTablesRepository<Product, Integer> {

}
