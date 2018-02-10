package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.TenderBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This DAO is used for TenderBookmark.
 */
@Repository
public interface TenderBookmarkDAO extends JpaRepository<TenderBookmark, Integer> {

    /**
     * This method is used for find the tender bookmark by tenderId and userId.
     *
     * @param tenderId unique identifier of tender
     * @param userId unique identifier of user
     * @return TenderBookmark
     * @see TenderBookmark
     */
    @Query("select t from TenderBookmark t where t.tender.id = ?1 and t.user.id = ?2")
    TenderBookmark findTenderBookmarkByUserAndTender(int tenderId, int userId);

    /**
     * This method is used for find the tender bookmark by userId.
     *
     * @param userId unique identifier of user
     * @return List
     * @see TenderBookmark
     */
    @Query("select t from TenderBookmark t where t.user.id = ?1")
    List<TenderBookmark> findTenderBookmarkByUserId(int userId);

    /**
     * This method is used for find the tender bookmark by tenderId.
     *
     * @param tenderId unique identifier of tender
     * @return List
     * @see TenderBookmark
     */
    @Query("select t from TenderBookmark t where t.tender.id = ?1")
    List<TenderBookmark> findTenderBookmarkByTender(int tenderId);
}
