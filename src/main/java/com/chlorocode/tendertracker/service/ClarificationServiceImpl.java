package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.ClarificationDAO;
import com.chlorocode.tendertracker.dao.entity.Clarification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by andy on 8/8/2017.
 */
@Service
public class ClarificationServiceImpl implements ClarificationService{

    private ClarificationDAO clariDao;

    @Autowired
    public ClarificationServiceImpl(ClarificationDAO clariDao){
        this.clariDao = clariDao;
    }

    @Override
    public List<Clarification> findClarificationByTenderId(int tenderId) {
        return clariDao.findClarificationByTenderId(tenderId);
    }

    @Override
    public List<Clarification> findAllClarification() {
        return clariDao.findAll();
    }
}
