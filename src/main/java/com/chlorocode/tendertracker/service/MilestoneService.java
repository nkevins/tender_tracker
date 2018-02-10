package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.entity.Milestone;

import java.util.List;

/**
 * Service interface for tender milestone.
 */
public interface MilestoneService {
    /**
     * This method is used to find all milestones for a particular tender.
     *
     * @param tenderId unique identifier of the tender
     * @return list of Milestone
     */
    List<Milestone> findMilestoneByTender(int tenderId);

    /**
     * This method is used to create new milestone.
     *
     * @param milestone milestone object to be created
     * @return Milestone
     */
    Milestone create(Milestone milestone);

    /**
     * This method is used to find milestone by id.
     *
     * @param id unique identifier of the milestone
     * @return Milestone
     */
    Milestone findMilestoneById(int id);

    /**
     * This method is used to update milestone.
     *
     * @param milestone milestone object to be updated
     * @return Milestone
     */
    Milestone update(Milestone milestone);

    /**
     * This method is used to remove milestone.
     *
     * @param id unique identifier of the milestone
     */
    void removeMilestone(int id);

    /**
     * This method is used to send email notification to company administrator when milestone is approaching.
     */
    void notifyApproachMilestone();
}
