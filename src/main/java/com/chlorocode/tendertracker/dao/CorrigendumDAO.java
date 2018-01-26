package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.Corrigendum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This DAO is used for Corrigendum.
 */
@Repository
public interface CorrigendumDAO extends JpaRepository<Corrigendum, Integer> {
    /**
     * This method is used to find the list of Corrigendum by tenderId.
     *
     * @param tenderId unique identifier of the tender
     * @return List
     * @see Corrigendum
     */
    @Query("select c from Corrigendum c where c.tender.id = ?1 order by c.createdDate desc")
    List<Corrigendum> findCorrigendumByTender(int tenderId);
}
