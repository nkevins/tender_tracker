package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.entity.Bid;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BidService {

    Bid findById(int id);

    Bid saveBid(Bid bid, List<MultipartFile> attachments);

    Bid findBidByCompanyAndTender(int companyId, int tenderId);

}
