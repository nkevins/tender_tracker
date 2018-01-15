package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.Clarification;
import com.chlorocode.tendertracker.dao.entity.TenderAppeal;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by andy on 14/1/2018.
 */
@Repository
public interface TenderAppealDAO extends JpaRepository<TenderAppeal, Integer> {

    @Query("select r from TenderAppeal r where r.tender.id = ?1 and r.company.id=?2 order by r.createdDate desc")
    List<TenderAppeal> findTenderAppealsBy(int tenderId, int companyId);
}
