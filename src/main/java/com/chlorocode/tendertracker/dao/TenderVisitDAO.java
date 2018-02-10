package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.TenderVisit;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * This DAO is used for TenderVisit.
 */
public interface TenderVisitDAO extends JpaRepository<TenderVisit, Integer> {

}
