package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.dto.TenderSearchDTO;
import com.chlorocode.tendertracker.dao.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service interface for tender.
 */
public interface TenderSubscriptionService {

    /**
     * This method is used to get tender bookmark by tender and user search criteria.
     *
     * @param tenderId unique identifier of the tender
     * @param userId unique identifier of the user
     * @return TenderBookmark
     */
    TenderBookmark findTenderBookmark(int tenderId, int userId);

    /**
     * This method is used to save a tender bookmark.
     *
     * @param tender tender to be bookmarked
     * @param user user who bookmark the tender
     * @return TenderBookmark
     */
    TenderBookmark bookmarkTender(Tender tender, User user);

    /**
     * This method is used to remove a tender bookmark.
     *
     * @param tenderId unique identifier of the tender
     * @param userId unique identifier of the user
     */
    void removeTenderBookmark(int tenderId, int userId);

    /**
     * This method is used to ge the list of tender category that user subscribe.
     *
     * @param userId unique identifier of the user
     * @return list of TenderCategorySubscription
     */
    List<TenderCategorySubscription> findUserSubscription(int userId);

    /**
     * This method is used to add tender category subscription.
     *
     * @param user user who wants to subscribe
     * @param categories list of tender category to be subscribed
     */
    void subscribeToTenderCategory(User user, List<TenderCategory> categories);

    /**
     * This method is used to find list of tender bookmarks for a particular user.
     *
     * @param userId unique identifier of the user
     * @return list of TenderBookmark
     */
    List<TenderBookmark> findTenderBookmarkByUserId(int userId);

    /**
     * This method is used to send notification to tender bookmark subscribers.
     *
     * @param tender tender object where the changes made
     * @param changeType type of changes made
     */
    void sendBookmarkNoti(Tender tender, int changeType);

    /**
     * This method is used by scheduler task to auto close tender status and send notification.
     * Tender where the closing date already passed will be automatically updated to closed status and email notification
     * will be sent to company administrator.
     */
    void autoCloseTenderAndNotify();
}
