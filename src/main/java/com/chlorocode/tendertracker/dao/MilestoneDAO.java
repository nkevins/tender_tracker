package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.Milestone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Kyaw Min Thu on 1/8/2017.
 */
@Repository
public interface MilestoneDAO extends JpaRepository<Milestone, Integer> {

    @Query("select m from Milestone m where m.tender.id = ?1")
    List<Milestone> findMilestoneByTender(int tenderId);
}
