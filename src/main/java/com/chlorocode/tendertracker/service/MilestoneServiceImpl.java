package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.constants.TTConstants;
import com.chlorocode.tendertracker.dao.MilestoneDAO;
import com.chlorocode.tendertracker.dao.entity.CodeValue;
import com.chlorocode.tendertracker.dao.entity.Milestone;
import com.chlorocode.tendertracker.dao.entity.User;
import com.chlorocode.tendertracker.service.notification.NotificationService;
import com.chlorocode.tendertracker.service.notification.NotificationServiceImpl;
import com.chlorocode.tendertracker.utils.DateUtility;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Kyaw Min Thu on 3/8/2017.
 */
@Service
public class MilestoneServiceImpl implements MilestoneService {
    private MilestoneDAO milestoneDAO;
    private UserService userService;
    private NotificationService notificationService;
    private CodeValueService codeValueService;

    private MilestoneServiceImpl(MilestoneDAO milestoneDAO, UserService userService, NotificationService notificationService, CodeValueService codeValueService)
    {
        this.milestoneDAO = milestoneDAO;
        this.userService = userService;
        this.notificationService = notificationService;
        this.codeValueService = codeValueService;
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

    @Override
    public void notifyApproachMilestone() {
        List<Milestone> approachMilestones = milestoneDAO.findApproachMilestone(DateUtility.getFutureDateTime(0, 3, 0, 0));
        if (approachMilestones != null) {
            for (Milestone milestone : approachMilestones) {
                // Send notification to company admin.
                User user = userService.findById(milestone.getTender().getCompany().getCreatedBy());
                String statusDescription = codeValueService.getDescription("milestone_status", milestone.getStatus());
                if (user != null) {
                    Map<String, Object> params = new HashMap<>();
                    params.put(TTConstants.PARAM_TENDER_ID, milestone.getTender().getId());
                    params.put(TTConstants.PARAM_TENDER_TITLE, milestone.getTender().getTitle());
                    params.put(TTConstants.PARAM_MILESTONE_DESCRIPTION, milestone.getDescription());
                    params.put(TTConstants.PARAM_MILESTONE_DUE_DATE, milestone.getDueDate().toString());
                    params.put(TTConstants.PARAM_MILESTONE_STATUS, statusDescription);
                    params.put(TTConstants.PARAM_EMAIL, user.getEmail());
                    notificationService.sendNotification(NotificationServiceImpl.NOTI_MODE.milestone_approach_noti, params);
                }
                // Update notifyStatus in milestone.
                milestone.setNotifyStatus(1);
                update(milestone);
            }
        }
    }
}
