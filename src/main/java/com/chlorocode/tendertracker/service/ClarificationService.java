package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.entity.Clarification;

import java.util.List;

/**
 * Service interface for tender clarification.
 */
public interface ClarificationService {

    /**
     * This method is used to find the list of clarifications for a particular tender.
     *
     * @param tenderId unique identifier of the tender
     * @return list of Clarification
     */
    List<Clarification> findClarificationByTenderId(int tenderId);

    /**
     * This method is used to list all clarifications.
     *
     * @return list of Clarification
     */
    List<Clarification> findAllClarification();

    /**
     * This method is used to update tender clarification with response.
     *
     * @param id unique identifier of the clarification
     * @param response new response to be updated
     * @return Clarification
     */
    Clarification updateReponse(int id, String response);

    /**
     * This method is used to find a tender clarification response by clarification id.
     *
     * @param id unique identifier of the clarification
     * @return Clarification
     */
    Clarification findById(int id);

    /**
     * This method is used to save a new tender clarification.
     *
     * @param classification clarification message
     * @param tenderId unique identifier of the tender
     * @param companyId unique identifier of the company who submit the clarification
     * @return Clarification
     */
    Clarification create(String classification, int tenderId, int companyId);

}
