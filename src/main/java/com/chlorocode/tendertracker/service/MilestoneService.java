package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.entity.Milestone;

import java.util.List;

/**
 * Created by Kyaw Min Thu on 6/1/2018.
 */
public interface MilestoneService {
    List<Milestone> findMilestoneByTender(int tenderId);
    Milestone create(Milestone milestone);
    Milestone findMilestoneById(int id);
    Milestone update(Milestone milestone);
    void removeMilestone(int id);
    void notifyApproachMilestone();
}
