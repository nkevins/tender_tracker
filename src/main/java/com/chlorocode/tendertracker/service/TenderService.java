package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.dto.TenderSearchDTO;
import com.chlorocode.tendertracker.dao.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TenderService {

    Tender findById(int id);
    List<Tender> findTender();
    Tender createTender(Tender t, List<MultipartFile> attachments);
    Tender updateTender(Tender tender);

    TenderItem findTenderItemById(int id);
    TenderItem addTenderItem(TenderItem tenderItem);
    TenderItem updateTenderItem(TenderItem tenderItem);
    void removeTenderItem(int tenderItemId);
    void moveUpTenderItem(int tenderItemId, int tenderId);
    void moveDownTenderItem(int tenderItemId, int tenderId);

    TenderDocument addTenderDocument(MultipartFile attachment, Tender tender, int createdBy);
    void removeTenderDocument(int id);

    TenderBookmark findTenderBookmark(int tenderId, int userId);
    TenderBookmark bookmarkTender(Tender tender, User user);
    void removeTenderBookmark(int tenderId, int userId);

    List<TenderCategorySubscription> findUserSubscription(int userId);
    void subscribeToTenderCategory(User user, List<TenderCategory> categories);

    List<TenderBookmark> findTenderBookmarkByUserId(int userId);

    Page<Tender> listAllByPage(Pageable pageable);
    Page<Tender> searchTender(TenderSearchDTO searchDTO, Pageable pageable);

    void sendBookmarkNoti(Tender tender, int changeType);

    void logVisit(Tender tender, String ipAddress);

    void autoCloseTenderAndNotify();
}
