package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.MilestoneDAO;
import com.chlorocode.tendertracker.dao.entity.Milestone;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Kyaw Min Thu on 3/8/2017.
 */
@Service
public class MilestoneServiceImpl implements MilestoneService {
    private MilestoneDAO milestoneDAO;

    private MilestoneServiceImpl(MilestoneDAO milestoneDAO)
    {
        this.milestoneDAO = milestoneDAO;
    }

    @Override
    public List<Milestone> findMilestoneByTender(int tenderId) {
        return milestoneDAO.findMilestoneByTender(tenderId);
    }

    public Milestone create(Milestone svc){
       return milestoneDAO.saveAndFlush(svc);
    }

    public Milestone update(Milestone svc){
        return milestoneDAO.save(svc);
    }

    public Milestone findMilestoneById(int id){
        return milestoneDAO.findOne(id);
    }

    @Override
    public void removeMilestone(int id) {
        Milestone milestone = milestoneDAO.findOne(id);
        if (milestone != null) {
            milestoneDAO.delete(milestone);
        }
    }
}
