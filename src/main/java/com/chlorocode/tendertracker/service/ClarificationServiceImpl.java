package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.ClarificationDAO;
import com.chlorocode.tendertracker.dao.entity.Clarification;
import com.chlorocode.tendertracker.dao.entity.CurrentUser;
import com.chlorocode.tendertracker.logging.TTLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Null;
import java.util.Date;
import java.util.List;

/**
 * Created by andy on 8/8/2017.
 */
@Service
public class ClarificationServiceImpl implements ClarificationService{

    private ClarificationDAO clariDao;
    private String className;

    @Autowired
    public ClarificationServiceImpl(ClarificationDAO clariDao){
        this.className = this.getClass().getName();
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

    @Override
    public Clarification updateReponse(int id, String response) {

        try{
           Clarification dbOjb = clariDao.findOne(id);

           if(dbOjb == null){
               TTLogger.error(className, "tender clarification not found.ID " + id);
               return null;
           }

           CurrentUser usr = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
           dbOjb.setResponse(response);
           dbOjb.setLastUpdatedDate(new Date());
           dbOjb.setLastUpdatedBy(usr.getUser().getId());
           return clariDao.save(dbOjb);

        }catch(Exception ex){
            TTLogger.error(className, "error: " , ex);
        }
        return null;
    }

    @Override
    public Clarification findById(int id) {
        return clariDao.findOne(id);
    }
}
