package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.ClarificationDAO;
import com.chlorocode.tendertracker.dao.TenderAppealDAO;
import com.chlorocode.tendertracker.dao.entity.TenderAppeal;
import com.chlorocode.tendertracker.logging.TTLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by andy on 14/1/2018.
 */
@Service
public class TenderAppealServiceImpl implements TenderAppealService {

    private TenderAppealDAO dao;

    private String className;
    @Autowired
    public TenderAppealServiceImpl(TenderAppealDAO dao){
        this.className = this.getClass().getName();
        this.dao = dao;
    }

    @Override
    public TenderAppeal Create(TenderAppeal appeal) {
        try{

            return dao.saveAndFlush(appeal);
        }catch (Exception ex){
            TTLogger.error(className, "error: " , ex);
        }

        return null;
    }

    @Override
    public List<TenderAppeal> findTenderAppealsBy(int tenderId, int companyId) {
        return dao.findTenderAppealsBy(tenderId,companyId);
    }

    @Override
    public TenderAppeal findById(int id) {
        return dao.findOne(id);
    }

    @Override
    public boolean processTender(int id, int rejectedBy, int status) {
        TenderAppeal tender = dao.findOne(id);
        try{
            tender.setStatus(status);
            tender.setLastUpdatedBy(rejectedBy);
            tender.setLastUpdatedDate(new Date());
            dao.saveAndFlush(tender);
            return true;
        }catch (Exception ex){
            TTLogger.error(className, "error: " , ex);
        }

        return false;
    }
}
