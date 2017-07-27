package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.entity.Tender;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TenderService {

    Tender createTender(Tender t, List<MultipartFile> attachments);

    Tender findById(int id);

    List<Tender> findTender();
}
