package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.ClarificationDAO;
import com.chlorocode.tendertracker.dao.ProductClarificationDAO;
import com.chlorocode.tendertracker.dao.entity.Clarification;
import com.chlorocode.tendertracker.dao.entity.CurrentUser;
import com.chlorocode.tendertracker.dao.entity.Product;
import com.chlorocode.tendertracker.dao.entity.ProductClarification;
import com.chlorocode.tendertracker.logging.TTLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

/**
 * Created by andy on 18/1/2018.
 */
@Service
public class ProductClarificationServiceImpl implements ProductClarificationService {

    ProductClarificationDAO dao;
    private String className;

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
    public ProductClarification Create(ProductClarification product) {
        return dao.save(product);
    }

    @Override
    public List<ProductClarification> findClarificationByProdId(int prodId) {
        return dao.findClarificationByProdId(prodId);
    }

    @Override
    public ProductClarification UpdateResponse(int id, String response) {
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
