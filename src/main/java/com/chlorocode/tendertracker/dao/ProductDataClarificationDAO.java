package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.ProductClarification;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by andy on 20/1/2018.
 * This class extends DataTableRepository so that it can output the entity into DataTables.
 */
@Repository
public interface ProductDataClarificationDAO extends DataTablesRepository<ProductClarification, Integer> {

}
