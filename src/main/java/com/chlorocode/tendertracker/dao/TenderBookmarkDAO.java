package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.TenderBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TenderBookmarkDAO extends JpaRepository<TenderBookmark, Integer> {

    @Query("select t from TenderBookmark t where t.tender.id = ?1 and t.user.id = ?2")
    TenderBookmark findTenderBookmarkByUserAndTender(int tenderId, int userId);
}
