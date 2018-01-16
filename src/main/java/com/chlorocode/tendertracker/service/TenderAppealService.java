package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.entity.TenderAppeal;

import java.util.List;

/**
 * Created by andy on 14/1/2018.
 */
public interface TenderAppealService {
    TenderAppeal Create(TenderAppeal appeal);
    List<TenderAppeal> findTenderAppealsBy(int tenderId, int companyId);
}
