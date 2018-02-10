package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.constants.TTConstants;
import com.chlorocode.tendertracker.dao.TenderAppealDAO;
import com.chlorocode.tendertracker.dao.entity.TenderAppeal;
import com.chlorocode.tendertracker.exception.ApplicationException;
import com.chlorocode.tendertracker.logging.TTLogger;
import com.chlorocode.tendertracker.service.notification.NotificationService;
import com.chlorocode.tendertracker.service.notification.NotificationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service implementation of TenderAppeal service.
 */
@Service
public class TenderAppealServiceImpl implements TenderAppealService {

    private TenderAppealDAO dao;
    private NotificationService notificationService;
    private UserRoleService userRoleService;

    private String className;

    @Autowired
    public TenderAppealServiceImpl(TenderAppealDAO dao, NotificationService notificationService, UserRoleService userRoleService) {
        this.className = this.getClass().getName();
        this.dao = dao;
        this.notificationService = notificationService;
        this.userRoleService = userRoleService;
    }

    @Override
    public TenderAppeal create(TenderAppeal appeal) {
        // Validate duplicate submission
        List<TenderAppeal> existingAppeals = dao.findTenderAppealsBy(appeal.getTender().getId(), appeal.getCompany().getId());
        if (existingAppeals != null && existingAppeals.size() > 0) {
            TTLogger.error(className, "Duplicate appeal detected");
            throw new ApplicationException("Not allowed to submit duplicate appeal.");
        }

        try {
            appeal = dao.saveAndFlush(appeal);
            // Send the email notification to party who submit the tender appeal
            if (appeal != null) {
                Set<String> adminEmails = userRoleService.findCompanyAdminEmails(appeal.getCompany().getId());
                if (adminEmails != null && adminEmails.size()> 0) {
                    Map<String, Object> params = new HashMap<>();
                    params.put(TTConstants.PARAM_TENDER_ID, appeal.getTender().getId());
                    params.put(TTConstants.PARAM_TENDER_TITLE, appeal.getTender().getTitle());
                    params.put(TTConstants.PARAM_APPEAL_COMPANY, appeal.getCompany().getName());
                    params.put(TTConstants.PARAM_EMAILS, adminEmails.toArray(new String[adminEmails.size()]));
                    notificationService.sendNotification(NotificationServiceImpl.NOTI_MODE.appeal_create_noti, params);
                }
            }
            return appeal;
        } catch (Exception ex) {
            TTLogger.error(className, "error: " , ex);
        }

        return null;
    }

    @Override
    public List<TenderAppeal> findTenderAppealsBy(int tenderId, int companyId) {
        return dao.findTenderAppealsBy(tenderId,companyId);
    }

    @Override
    public TenderAppeal findById(int id) {
        return dao.findOne(id);
    }

    @Override
    public boolean processTenderAppeal(int id, int rejectedBy, int status) {
        TenderAppeal appeal = dao.findOne(id);
        try {
            appeal.setStatus(status);
            appeal.setLastUpdatedBy(rejectedBy);
            appeal.setLastUpdatedDate(new Date());
            dao.saveAndFlush(appeal);
            //Send email notification to appealer. if status is 1, means the tender appeal accepted and process by tenderer preparer, if it is 2, means it is rejected by preparer
            Set<String> adminEmails = userRoleService.findCompanyAdminEmails(appeal.getCompany().getId());
            if (adminEmails != null && adminEmails.size()> 0) {
                Map<String, Object> params = new HashMap<>();
                params.put(TTConstants.PARAM_TENDER_ID, appeal.getTender().getId());
                params.put(TTConstants.PARAM_TENDER_TITLE, appeal.getTender().getTitle());
                params.put(TTConstants.PARAM_APPEAL_COMPANY, appeal.getCompany().getName());
                params.put(TTConstants.PARAM_APPEAL_ACTION, appeal.getStatus());
                params.put(TTConstants.PARAM_EMAILS, adminEmails.toArray(new String[adminEmails.size()]));
                notificationService.sendNotification(NotificationServiceImpl.NOTI_MODE.appeal_update_noti, params);
            }
            return true;
        } catch (Exception ex) {
            TTLogger.error(className, "error: " , ex);
        }

        return false;
    }
}
