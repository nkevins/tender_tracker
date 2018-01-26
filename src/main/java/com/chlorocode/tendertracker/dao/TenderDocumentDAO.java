package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.TenderDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * This DAO is used for Tender Document.
 */
@Repository
public interface TenderDocumentDAO extends JpaRepository<TenderDocument, Integer> {

}
