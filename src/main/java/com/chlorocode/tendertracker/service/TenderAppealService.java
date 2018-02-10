package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.entity.TenderAppeal;

import java.util.List;

/**
 * Service interface for tender appeal.
 */
public interface TenderAppealService {

    /**
     * This method is used to create TenderAppeal.
     *
     * @param appeal TenderAppeal object to be created
     * @return TenderAppeal
     */
    TenderAppeal create(TenderAppeal appeal);

    /**
     * This method is used to find TenderAppeal by tenderId and companyId.
     *
     * @param tenderId unique identifier of the tender
     * @param companyId unique identifier of the company
     * @return list of TenderAppeal
     */
    List<TenderAppeal> findTenderAppealsBy(int tenderId, int companyId);

    /**
     * This method is used to find TenderAppeal by id.
     *
     * @param id unique identifier of the TenderAppeal
     * @return TenderAppeal
     */
    TenderAppeal findById(int id);

    /**
     * This method is used to update tender appeal status.
     *
     * @param id unique identifier of the TenderAppeal
     * @param rejectedBy userId who submit the update request
     * @param status TenderAppeal status to be updated
     * @return boolean
     */
    boolean processTenderAppeal(int id, int rejectedBy, int status);
}
