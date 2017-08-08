package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.entity.Clarification;
import com.chlorocode.tendertracker.dao.entity.Tender;

import java.util.List;

/**
 * Created by andy on 8/8/2017.
 */
public interface ClarificationService {
    List<Clarification> findClarificationByTenderId(int tenderId);
    List<Clarification> findAllClarification();
    Clarification updateReponse(int id, String response);
    Clarification findById(int id);
    Clarification create(String classification, int tenderId, int companyId);

}
