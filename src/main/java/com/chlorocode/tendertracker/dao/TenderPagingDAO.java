package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.Tender;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * This DAO is used to show the tender in search screen by paging and sorting features.
 */
public interface TenderPagingDAO extends PagingAndSortingRepository<Tender, Integer>, JpaSpecificationExecutor<Tender> {
}
