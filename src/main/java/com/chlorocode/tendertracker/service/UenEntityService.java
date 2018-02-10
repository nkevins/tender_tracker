package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.entity.UenEntity;

/**
 * Service interface for UEN.
 */
public interface UenEntityService {

    /**
     * This method is used to find UEN details.
     *
     * @param uen uen
     * @return UenEntity
     */
    UenEntity findByUen(String uen);
}
