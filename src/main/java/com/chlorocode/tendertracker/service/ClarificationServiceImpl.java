package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.ClarificationDAO;
import com.chlorocode.tendertracker.dao.entity.Clarification;
import com.chlorocode.tendertracker.dao.entity.Company;
import com.chlorocode.tendertracker.dao.entity.CurrentUser;
import com.chlorocode.tendertracker.dao.entity.Tender;
import com.chlorocode.tendertracker.logging.TTLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Service implementation of ClarificationService.
 */
@Service
public class ClarificationServiceImpl implements ClarificationService{

    private ClarificationDAO clariDao;
    private String className;

    /**
     * Constructor.
     *
     * @param clariDao ClarificationDAO
     */
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
    public Clarification create(String classification, int tenderId, int companyId ){
        try{
            Clarification dbOjb = new Clarification();
            Company cp = new Company();
            dbOjb.setDescription(classification);
            cp.setId(companyId);
            dbOjb.setCompany(cp);

            CurrentUser usr = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            dbOjb.setLastUpdatedBy(usr.getId());
            dbOjb.setCreatedBy(usr.getId());
            dbOjb.setCreatedDate(new Date());
            dbOjb.setLastUpdatedDate(new Date());
            Tender td = new Tender();
            td.setId(tenderId);
            dbOjb.setTender(td);

            return clariDao.saveAndFlush(dbOjb);
        }catch(Exception ex){
            TTLogger.error(className, "error: " , ex);
        }

        return null;
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
