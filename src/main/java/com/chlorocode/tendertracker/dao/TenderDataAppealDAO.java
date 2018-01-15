package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.Tender;
import com.chlorocode.tendertracker.dao.entity.TenderAppeal;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;

/**
 * Created by andy on 16/1/2018.
 */
public interface TenderDataAppealDAO extends DataTablesRepository<TenderAppeal, Integer> {
}
