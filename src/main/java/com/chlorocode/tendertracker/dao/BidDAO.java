package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.Bid;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This DAO is used for Bid.
 */
@Repository
public interface BidDAO extends DataTablesRepository<Bid, Integer> {

    /**
     * This method is used to find Bid by company and tender.
     *
     * @param companyId unique identifier of company
     * @param tenderId unique identifier of tender
     * @return Bid
     * @see Bid
     */
    @Query("select b from Bid b where b.company.id = ?1 and b.tender.id = ?2")
    Bid findBidByCompanyAndTender(int companyId, int tenderId);

    /**
     * This method is used to find Bid by company.
     *
     * @param companyId unique identifier of company
     * @return List
     * @see Bid
     */
    @Query("select b from Bid b where b.company.id = ?1")
    List<Bid> findBidByCompany(int companyId);

    /**
     * This method is used to find Bid by tender.
     *
     * @param tenderId unique identifier of tender
     * @return List
     * @see Bid
     */
    @Query("select b from Bid b where b.tender.id = ?1")
    List<Bid> findBidByTender(int tenderId);

}
