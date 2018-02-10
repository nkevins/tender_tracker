package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.constants.TTConstants;
import com.chlorocode.tendertracker.dao.MilestoneDAO;
import com.chlorocode.tendertracker.dao.entity.Milestone;
import com.chlorocode.tendertracker.service.notification.NotificationService;
import com.chlorocode.tendertracker.service.notification.NotificationServiceImpl;
import com.chlorocode.tendertracker.utils.DateUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Service implementation of MilestoneService.
 */
@Service
public class MilestoneServiceImpl implements MilestoneService {
    private MilestoneDAO milestoneDAO;
    private UserRoleService userRoleService;
    private NotificationService notificationService;
    private CodeValueService codeValueService;

    /**
     * Constructor
     *
     * @param milestoneDAO MilestoneDAO
     * @param notificationService NotificationService
     * @param codeValueService CodeValueService
     * @param userRoleService UserRoleService
     */
    @Autowired
    public MilestoneServiceImpl(MilestoneDAO milestoneDAO, NotificationService notificationService,
                                CodeValueService codeValueService, UserRoleService userRoleService)
    {
        this.milestoneDAO = milestoneDAO;
        this.notificationService = notificationService;
        this.codeValueService = codeValueService;
        this.userRoleService = userRoleService;
    }

    @Override
    public List<Milestone> findMilestoneByTender(int tenderId) {
        return milestoneDAO.findMilestoneByTender(tenderId);
    }

    @Override
    public Milestone create(Milestone svc){
       return milestoneDAO.saveAndFlush(svc);
    }

    @Override
    public Milestone update(Milestone svc){
        return milestoneDAO.save(svc);
    }

    @Override
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
                Set<String> adminEmails = userRoleService.findCompanyAdminEmails(milestone.getTender().getCompany().getId());
                String statusDescription = codeValueService.getDescription("milestone_status", milestone.getStatus());
                if (adminEmails != null && adminEmails.size() > 0) {
                    Map<String, Object> params = new HashMap<>();
                    params.put(TTConstants.PARAM_TENDER_ID, milestone.getTender().getId());
                    params.put(TTConstants.PARAM_TENDER_TITLE, milestone.getTender().getTitle());
                    params.put(TTConstants.PARAM_MILESTONE_DESCRIPTION, milestone.getDescription());
                    params.put(TTConstants.PARAM_MILESTONE_DUE_DATE, milestone.getDueDate().toString());
                    params.put(TTConstants.PARAM_MILESTONE_STATUS, statusDescription);
                    params.put(TTConstants.PARAM_EMAILS, adminEmails.toArray(new String[adminEmails.size()]));
                    notificationService.sendNotification(NotificationServiceImpl.NOTI_MODE.milestone_approach_noti, params);
                }
                // Update notifyStatus in milestone.
                milestone.setNotifyStatus(1);
                update(milestone);
            }
        }
    }
}
