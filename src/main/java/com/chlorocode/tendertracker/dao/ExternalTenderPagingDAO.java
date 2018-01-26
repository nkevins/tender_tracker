package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.ExternalTender;
import com.chlorocode.tendertracker.dao.entity.Tender;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * This DAO is used for showing External Tender by pagination.
 */
public interface ExternalTenderPagingDAO extends PagingAndSortingRepository<ExternalTender, Long>, JpaSpecificationExecutor<ExternalTender> {
}
