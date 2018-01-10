package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.BidDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BidDocumentDAO extends JpaRepository<BidDocument, Integer> {

}
