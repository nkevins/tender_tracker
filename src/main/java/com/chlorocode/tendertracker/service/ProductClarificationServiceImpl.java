package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.ProductClarificationDAO;
import com.chlorocode.tendertracker.dao.entity.CurrentUser;
import com.chlorocode.tendertracker.dao.entity.ProductClarification;
import com.chlorocode.tendertracker.logging.TTLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Service implementation of ProductClarificationService
 */
@Service
public class ProductClarificationServiceImpl implements ProductClarificationService {

    ProductClarificationDAO dao;
    private String className;

    /**
     * Constructor.
     *
     * @param dao ProductClarificationDAO
     */
    @Autowired
    public ProductClarificationServiceImpl(ProductClarificationDAO dao){
        this.className = this.getClass().getName();
        this.dao = dao;
    }

    @Override
    public ProductClarification findById(int id) {
        return dao.findOne(id);
    }

    @Override
    public ProductClarification create(ProductClarification product) {
        return dao.save(product);
    }

    @Override
    public List<ProductClarification> findClarificationByProdId(int prodId) {
        return dao.findClarificationByProdId(prodId);
    }

    @Override
    public ProductClarification updateResponse(int id, String response) {
        try{
            ProductClarification dbOjb = dao.findOne(id);

            if(dbOjb == null){
                TTLogger.error(className, "tender clarification not found.ID " + id);
                return null;
            }

            CurrentUser usr = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            dbOjb.setResponse(response);
            dbOjb.setLastUpdatedDate(new Date());
            dbOjb.setLastUpdatedBy(usr.getUser().getId());
            return dao.save(dbOjb);

        }catch(Exception ex){
            TTLogger.error(className, "error: " , ex);
        }
        return null;
    }
}
