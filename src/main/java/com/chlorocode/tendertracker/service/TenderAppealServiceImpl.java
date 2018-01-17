package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.constants.TTConstants;
import com.chlorocode.tendertracker.dao.ClarificationDAO;
import com.chlorocode.tendertracker.dao.TenderAppealDAO;
import com.chlorocode.tendertracker.dao.entity.TenderAppeal;
import com.chlorocode.tendertracker.dao.entity.User;
import com.chlorocode.tendertracker.logging.TTLogger;
import com.chlorocode.tendertracker.service.notification.NotificationService;
import com.chlorocode.tendertracker.service.notification.NotificationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by andy on 14/1/2018.
 */
@Service
public class TenderAppealServiceImpl implements TenderAppealService {

    private TenderAppealDAO dao;
    private NotificationService notificationService;
    private UserService userService;

    private String className;
    @Autowired
    public TenderAppealServiceImpl(TenderAppealDAO dao, NotificationService notificationService, UserService userService){
        this.className = this.getClass().getName();
        this.dao = dao;
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @Override
    public TenderAppeal Create(TenderAppeal appeal) {
        try{
            appeal = dao.saveAndFlush(appeal);
            // Send the email notification to party who submit the tender appeal
            if (appeal != null) {
                Map<String, Object> params = new HashMap<>();
                params.put(TTConstants.PARAM_TENDER_ID, appeal.getTender().getId());
                params.put(TTConstants.PARAM_TENDER_TITLE, appeal.getTender().getTitle());
                params.put(TTConstants.PARAM_APPEAL_COMPANY, appeal.getCompany().getName());
                User user = userService.findById(appeal.getCompany().getCreatedBy());
                params.put(TTConstants.PARAM_EMAIL, user.getEmail());
                notificationService.sendNotification(NotificationServiceImpl.NOTI_MODE.appeal_create_noti, params);
            }
            return appeal;
        }catch (Exception ex){
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
    public boolean processTender(int id, int rejectedBy, int status) {
        TenderAppeal tender = dao.findOne(id);
        try{
            tender.setStatus(status);
            tender.setLastUpdatedBy(rejectedBy);
            tender.setLastUpdatedDate(new Date());
            dao.saveAndFlush(tender);
            //Send email notification to appealer. if status is 1, means the tender appeal accepted and process by tenderer preparer, if it is 2, means it is rejected by preparer
            Map<String, Object> params = new HashMap<>();
            params.put(TTConstants.PARAM_TENDER_ID, tender.getTender().getId());
            params.put(TTConstants.PARAM_TENDER_TITLE, tender.getTender().getTitle());
            params.put(TTConstants.PARAM_APPEAL_COMPANY, tender.getCompany().getName());
            params.put(TTConstants.PARAM_APPEAL_ACTION, tender.getStatus());
            User user = userService.findById(tender.getCompany().getCreatedBy());
            params.put(TTConstants.PARAM_EMAIL, user.getEmail());
            notificationService.sendNotification(NotificationServiceImpl.NOTI_MODE.appeal_update_noti, params);
            return true;
        }catch (Exception ex){
            TTLogger.error(className, "error: " , ex);
        }

        return false;
    }
}
