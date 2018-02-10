package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.UenEntityDAO;
import com.chlorocode.tendertracker.dao.entity.UenEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service implementation of UenEntityService
 */
@Service
public class UenEntityServiceImpl implements UenEntityService {

    private UenEntityDAO dao;

    /**
     * Constructor.
     *
     * @param dao UenEntityDAO
     */
    @Autowired
    public UenEntityServiceImpl(UenEntityDAO dao){
        this.dao = dao;
    }

    @Override
    public UenEntity findByUen(String uen) {
        return dao.findByUen(uen);
    }
}
