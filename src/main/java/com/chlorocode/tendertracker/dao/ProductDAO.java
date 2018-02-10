package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.Product;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Repository;

/**
 * DAO class for Product entity.
 * This class extends DataTableRepository so that it can output the entity into DataTables.
 */
@Repository
public interface ProductDAO extends DataTablesRepository<Product, Integer> {

}
