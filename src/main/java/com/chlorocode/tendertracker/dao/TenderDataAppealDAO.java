package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.Tender;
import com.chlorocode.tendertracker.dao.entity.TenderAppeal;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;

/**
 * Created by andy on 16/1/2018.
 * This class extends DataTableRepository so that it can output the entity into DataTables.
 */
public interface TenderDataAppealDAO extends DataTablesRepository<TenderAppeal, Integer> {
}
