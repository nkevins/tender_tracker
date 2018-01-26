package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.constants.TTConstants;
import com.chlorocode.tendertracker.dao.*;
import com.chlorocode.tendertracker.dao.entity.*;
import com.chlorocode.tendertracker.service.notification.NotificationService;
import com.chlorocode.tendertracker.service.notification.NotificationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

/**
 * Service implementation of TenderSubscriptionService.
 */
@Service
public class TenderSubscriptionServiceImpl implements TenderSubscriptionService {

    private TenderDAO tenderDAO;
    private TenderCategorySubscriptionDAO tenderCategorySubscriptionDAO;
    private TenderBookmarkDAO tenderBookmarkDAO;
    private NotificationService notificationService;
    private UserRoleService userRoleService;

    /**
     * Constructor.
     *
     * @param tenderDAO TenderDAO
     * @param tenderBookmarkDAO TenderBookmarkDAO
     * @param tenderCategorySubscriptionDAO TenderCategorySubscriptionDAO
     * @param notificationService NotificationService
     * @param userRoleService UserRoleService
     */
    @Autowired
    public TenderSubscriptionServiceImpl(TenderDAO tenderDAO, TenderBookmarkDAO tenderBookmarkDAO
                            , TenderCategorySubscriptionDAO tenderCategorySubscriptionDAO
                            , NotificationService notificationService, UserRoleService userRoleService) {
        this.tenderDAO = tenderDAO;
        this.tenderCategorySubscriptionDAO = tenderCategorySubscriptionDAO;
        this.tenderBookmarkDAO = tenderBookmarkDAO;
        this.notificationService = notificationService;
        this.userRoleService=userRoleService;
    }

    @Override
    public TenderBookmark findTenderBookmark(int tenderId, int userId) {
        return tenderBookmarkDAO.findTenderBookmarkByUserAndTender(tenderId, userId);
    }

    @Override
    public TenderBookmark bookmarkTender(Tender tender, User user) {
        TenderBookmark tenderBookmark = new TenderBookmark();
        tenderBookmark.setUser(user);
        tenderBookmark.setTender(tender);
        tenderBookmark.setCreatedBy(user.getId());
        tenderBookmark.setCreatedDate(new Date());
        tenderBookmark.setLastUpdatedBy(user.getId());
        tenderBookmark.setLastUpdatedDate(new Date());

        return tenderBookmarkDAO.save(tenderBookmark);
    }

    @Override
    public void removeTenderBookmark(int tenderId, int userId) {
        TenderBookmark tenderBookmark = findTenderBookmark(tenderId, userId);
        tenderBookmarkDAO.delete(tenderBookmark);
    }

    @Override
    public List<TenderCategorySubscription> findUserSubscription(int userId) {
        return tenderCategorySubscriptionDAO.findUserSubscription(userId);
    }

    @Override
    @Transactional
    public void subscribeToTenderCategory(User user, List<TenderCategory> categories) {
        tenderCategorySubscriptionDAO.removeExistingSubscription(user.getId());

        List<TenderCategorySubscription> subsList = new LinkedList<>();

        for (TenderCategory i : categories) {
            TenderCategorySubscription subs = new TenderCategorySubscription();
            subs.setUser(user);
            subs.setTenderCategory(i);
            subs.setCreatedBy(user.getId());
            subs.setCreatedDate(new Date());
            subs.setLastUpdatedBy(user.getId());
            subs.setLastUpdatedDate(new Date());

            subsList.add(subs);
        }

        tenderCategorySubscriptionDAO.save(subsList);
    }

    @Override
    public List<TenderBookmark> findTenderBookmarkByUserId(int userId) {
        return tenderBookmarkDAO.findTenderBookmarkByUserId(userId);
    }

    @Override
    public void sendBookmarkNoti(Tender tender, int changeType) {
        if (tender != null) {
            // Send tender information to bookmark users.
            List<TenderBookmark>  tenderBookmarks = tenderBookmarkDAO.findTenderBookmarkByTender(tender.getId());
            if (tenderBookmarks != null) {
                Set<String> emails = new HashSet<>();
                for (TenderBookmark bookmark : tenderBookmarks) {
                    if (bookmark.getUser() != null) emails.add(bookmark.getUser().getEmail());
                }
                if (!emails.isEmpty()) {
                    Map<String, Object> params = new HashMap<>();
                    params.put(TTConstants.PARAM_TENDER, tender);
                    params.put(TTConstants.PARAM_EMAILS, emails.toArray(new String[emails.size()]));
                    params.put(TTConstants.PARAM_CHANGE_TYPE, changeType);
                    notificationService.sendNotification(NotificationServiceImpl.NOTI_MODE.tender_bookmark_noti, params);
                }
            }
        }
    }

    @Override
    public void autoCloseTenderAndNotify() {
        //Date currentDateTime = DateUtility.getCurrentDateTime();
        List<Tender> closingTenders = tenderDAO.findClosingTender();
        if (closingTenders != null && !closingTenders.isEmpty()) {
            for (Tender t : closingTenders) {
                // Notify to company administrator.
                Set<String> adminEmails = userRoleService.findCompanyAdminEmails(t.getCompany().getId());
                if (adminEmails != null && adminEmails.size()> 0) {
                    Map<String, Object> params = new HashMap<>();
                    params.put(TTConstants.PARAM_TENDER_ID, t.getId());
                    params.put(TTConstants.PARAM_TENDER_TITLE, t.getTitle());
                    params.put(TTConstants.PARAM_EMAILS, adminEmails.toArray(new String[adminEmails.size()]));
                    notificationService.sendNotification(NotificationServiceImpl.NOTI_MODE.tender_closed_noti, params);
                }
                // Change the status of tender to close tender.
                tenderDAO.closeTender(t.getId());
            }
        }
    }
}
