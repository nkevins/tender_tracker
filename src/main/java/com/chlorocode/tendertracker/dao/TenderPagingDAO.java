package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.Tender;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;
import java.util.List;

public interface TenderPagingDAO extends PagingAndSortingRepository<Tender, Integer>, JpaSpecificationExecutor<Tender> {
}
