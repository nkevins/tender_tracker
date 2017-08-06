package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.entity.Tender;
import com.chlorocode.tendertracker.dao.entity.TenderBookmark;
import com.chlorocode.tendertracker.dao.entity.TenderItem;
import com.chlorocode.tendertracker.dao.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TenderService {

    Tender createTender(Tender t, List<MultipartFile> attachments);

    Tender findById(int id);
    TenderItem findTenderItemById(int id);

    List<Tender> findTender();

    TenderBookmark findTenderBookmark(int tenderId, int userId);
    TenderBookmark bookmarkTender(Tender tender, User user);
    void removeTenderBookmark(int tenderId, int userId);
}
