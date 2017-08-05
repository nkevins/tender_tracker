package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.Tender;
import com.chlorocode.tendertracker.dao.entity.TenderItem;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TenderDAO extends DataTablesRepository<Tender, Integer> {

    @Query("select i from TenderItem i where i.id = ?1")
    TenderItem findTenderItemById(int id);
}
