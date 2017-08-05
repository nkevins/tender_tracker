package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.Bid;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BidDAO extends DataTablesRepository<Bid, Integer> {

    @Query("select b from Bid b where b.company.id = ?1 and b.tender.id = ?2")
    Bid findBidByCompanyAndTender(int companyId, int tenderId);

}
