package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.ProductClarification;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by andy on 20/1/2018.
 */
@Repository
public interface ProductDataClarificationDAO extends DataTablesRepository<ProductClarification, Integer> {

}
