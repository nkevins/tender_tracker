package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.entity.TenderVisit;

/**
 * Service interface for IP Geo Location.
 * This service will get geographical information for a IP address.
 */
public interface IPGeoLocationService {

    /**
     * This method will get tender visit information based on IP address.
     *
     * @param ip ip address
     * @return TenderVisit
     * @see TenderVisit
     */
    TenderVisit getIPDetails(String ip);
}
