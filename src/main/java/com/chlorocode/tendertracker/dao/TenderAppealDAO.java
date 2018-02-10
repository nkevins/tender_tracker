package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.TenderAppeal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * DAO class for TenderAppeal entity.
 */
@Repository
public interface TenderAppealDAO extends JpaRepository<TenderAppeal, Integer> {

    /**
     * This method is used to find TenderAppeal by tenderId and companyId.
     *
     * @param tenderId unique identifier of the tender
     * @param companyId unique identifier of the company
     * @return list of TenderAppeal
     */
    @Query("select r from TenderAppeal r where r.tender.id = ?1 and r.company.id=?2 order by r.createdDate desc")
    List<TenderAppeal> findTenderAppealsBy(int tenderId, int companyId);
}
