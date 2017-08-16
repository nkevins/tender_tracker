package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.entity.UenEntity;

/**
 * Created by andy on 16/8/2017.
 */
public interface UenEntityService {
    UenEntity findByUen(String uen);
}
