package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.entity.*;
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

    TenderDocument addTenderDocument(MultipartFile attachment, Tender tender, int createdBy);
    void removeTenderDocument(int id);

    TenderBookmark findTenderBookmark(int tenderId, int userId);
    TenderBookmark bookmarkTender(Tender tender, User user);
    void removeTenderBookmark(int tenderId, int userId);

    List<TenderCategorySubscription> findUserSubscription(int userId);
    void subscribeToTenderCategory(User user, List<TenderCategory> categories);
}
