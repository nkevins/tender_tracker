package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.CorrigendumDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * This DAO used for CorrigendumDocument.
 */
@Repository
public interface CorrigendumDocumentDAO extends JpaRepository<CorrigendumDocument, Integer> {

}
