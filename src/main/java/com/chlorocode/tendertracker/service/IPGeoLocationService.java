package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.entity.TenderVisit;

public interface IPGeoLocationService {

    TenderVisit getIPDetails(String ip);
}
