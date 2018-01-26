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

}
