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
public interface TenderService {

    /**
     * This method is used to find tender by id.
     *
     * @param id unique identifier of the tender
     * @return Tender
     */
    Tender findById(int id);

    /**
     * This method is used to list all tenders.
     *
     * @return list of tender
     */
    List<Tender> findTender();

    /**
     * This method is used to create a new tender.
     *
     * @param t tender object to be created
     * @param attachments tender attachments document
     * @return Tender
     */
    Tender createTender(Tender t, List<MultipartFile> attachments);

    /**
     * This method is used to update tender.
     *
     * @param tender tender object to be updated
     * @return Tender
     */
    Tender updateTender(Tender tender);

    /**
     * This method is used to get tender item by id.
     *
     * @param id unique identifier of the tender item
     * @return Tender Item
     */
    TenderItem findTenderItemById(int id);

    /**
     * This method is used to add tender item.
     *
     * @param tenderItem tender item to be saved
     * @return TenderItem
     */
    TenderItem addTenderItem(TenderItem tenderItem);

    /**
     * This method is used to update tender item.
     *
     * @param tenderItem tender item to be updated
     * @return TenderItem
     */
    TenderItem updateTenderItem(TenderItem tenderItem);

    /**
     * This method is used to remove tender item.
     *
     * @param tenderItemId unique identifier of the tender item
     */
    void removeTenderItem(int tenderItemId);

    /**
     * This method is used to move up tender item order.
     *
     * @param tenderItemId unique identifier of the tender item
     * @param tenderId unique identifier of the tender
     */
    void moveUpTenderItem(int tenderItemId, int tenderId);

    /**
     * This method is used to move down tender item order.
     *
     * @param tenderItemId unique identifier of the tender item
     * @param tenderId unique identifier of the tender
     */
    void moveDownTenderItem(int tenderItemId, int tenderId);

    /**
     * This method is used to add tender document.
     *
     * @param attachment document to be attached
     * @param tender tender object
     * @param createdBy user id who add the document
     * @return TenderDocument
     */
    TenderDocument addTenderDocument(MultipartFile attachment, Tender tender, int createdBy);

    /**
     * This method is used to remove tender document.
     *
     * @param id unique identifier of the tender document
     */
    void removeTenderDocument(int id);

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
     * This method is used to list all tenders with paging.
     *
     * @param pageable paging criteria
     * @return page of Tender
     */
    Page<Tender> listAllByPage(Pageable pageable);

    /**
     * This method is used to search tenders based on search criteria.
     *
     * @param searchDTO tender search criteria
     * @param pageable paging criteria
     * @return page of Tender
     */
    Page<Tender> searchTender(TenderSearchDTO searchDTO, Pageable pageable);

    /**
     * This method is used to send notification to tender bookmark subscribers.
     *
     * @param tender tender object where the changes made
     * @param changeType type of changes made
     */
    void sendBookmarkNoti(Tender tender, int changeType);

    /**
     * This method is used to log tender view statistic information.
     *
     * @param tender visited tender
     * @param ipAddress ip address of the visitor
     */
    void logVisit(Tender tender, String ipAddress);

    /**
     * This method is used to save a tender award.
     *
     * @param tenderAward tender award object to be saved
     */
    void awardTender(TenderAward tenderAward);

    /**
     * This method is used by scheduler task to auto close tender status and send notification.
     * Tender where the closing date already passed will be automatically updated to closed status and email notification
     * will be sent to company administrator.
     */
    void autoCloseTenderAndNotify();
}
