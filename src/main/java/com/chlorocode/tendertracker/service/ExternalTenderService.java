package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.entity.ExternalTender;

import java.util.List;

/**
 * Created by Kyaw Min Thu on 5/1/2017.
 */
public interface ExternalTenderService {
    public String createTenderWCList(List<ExternalTender> tenders);
}
