package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.entity.Bid;
import com.chlorocode.tendertracker.dao.entity.BidItem;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Interface service for bid.
 */
public interface BidService {

    /**
     * This method is used to find bid by id.
     *
     * @param id unique identifier of thi bid
     * @return Bid
     */
    Bid findById(int id);

    /**
     * This method is used to save bid.
     *
     * @param bid bid object to be saved
     * @param attachments bid attachments to be saved
     * @return Bid
     */
    Bid saveBid(Bid bid, List<MultipartFile> attachments);

    /**
     * This method is used to find a Bid by tender and company who submit the bid.
     *
     * @param companyId unique identifier of the company hwo submit the bid
     * @param tenderId unique identifier of the tender
     * @return Bid
     */
    Bid findBidByCompanyAndTender(int companyId, int tenderId);

    /**
     * This method is used to find a list of bids submitted by a particular company.
     *
     * @param companyId unique identifier of the company
     * @return list of Bid
     */
    List<Bid> findBidByCompany(int companyId);

    /**
     * This method is used to find a list of bid for a particular tender.
     *
     * @param tenderId unique identifier of the tender
     * @return list of bid
     */
    List<Bid> findBidByTender(int tenderId);

    /**
     * This method is used to find bid item for particular bid.
     *
     * @param id unique identifier of the tender
     * @return bid item
     */
    BidItem findBidItemById(int id);

    /**
     * This method is used to find bid item for particular bid.
     *
     * @param bidItem bid item object
     * @return bid item
     */
    BidItem updateBid(BidItem bidItem);

}
