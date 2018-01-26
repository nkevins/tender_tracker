package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.Milestone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by Kyaw Min Thu on 1/8/2017.
 * This DAO is used for Milestone.
 */
@Repository
public interface MilestoneDAO extends JpaRepository<Milestone, Integer> {
    /**
     * This method is used to find the milestone by tenderId.
     *
     * @param tenderId unique identifier of the tender
     * @return List
     * @see Milestone
     */
    @Query("select m from Milestone m where m.tender.id = ?1")
    List<Milestone> findMilestoneByTender(int tenderId);

    /**
     * This method is used to find the approach mile stone by due date.
     *
     * @param dueDate target date
     * @return List
     * @see Milestone
     */
    @Query("select m from Milestone m where m.dueDate < ?1 and m.notifyStatus=0")
    List<Milestone> findApproachMilestone(Date dueDate);
}
