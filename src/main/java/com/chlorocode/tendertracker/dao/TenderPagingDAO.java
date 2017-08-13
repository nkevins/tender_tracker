package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.Tender;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TenderPagingDAO extends PagingAndSortingRepository<Tender, Integer> {
    //@Query("select c from UserRole ur join ur.company c where ur.user.id = ?1 and ur.role.id = 2 and c.status = 1 order by c.name")
    @Query("select t from Tender t where " +
            "t.title LIKE %:title " +
            "and t.company.name LIKE %:company " +
            "and t.tenderCategory.id=:category " +
            "and t.status=:status " +
            "order by t.createdDate desc")
    Page<Tender> searchTenders(@Param("title") String title, @Param("company") String company, @Param("category") int category, @Param("status") int status, Pageable pageable);
}
