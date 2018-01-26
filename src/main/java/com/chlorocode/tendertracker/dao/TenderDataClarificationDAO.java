package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.Clarification;
import com.chlorocode.tendertracker.dao.entity.Tender;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by andy on 8/8/2017.
 * This class extends DataTableRepository so that it can output the entity into DataTables.
 */
@Repository
public interface TenderDataClarificationDAO extends DataTablesRepository<Clarification, Integer> {

}
