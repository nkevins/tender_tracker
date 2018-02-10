package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.BidDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * This DAO is used for Bid document.
 */
@Repository
public interface BidDocumentDAO extends JpaRepository<BidDocument, Integer> {

}
