package com.chlorocode.tendertracker.web.data;

import com.chlorocode.tendertracker.dao.ProductDataClarificationDAO;
import com.chlorocode.tendertracker.dao.TenderDataClarificationDAO;
import com.chlorocode.tendertracker.dao.entity.Clarification;
import com.chlorocode.tendertracker.dao.entity.CurrentUser;
import com.chlorocode.tendertracker.dao.entity.ProductClarification;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Created by andy on 20/1/2018.
 */
@RestController
public class ProductDataClarificationController {
    private ProductDataClarificationDAO dao;

    @Autowired
    public ProductDataClarificationController(ProductDataClarificationDAO dao){
        this.dao = dao;
    }

    @JsonView(DataTablesOutput.View.class)
    @RequestMapping(value = "/admin/data/productClarification", method = RequestMethod.GET)
    public DataTablesOutput<ProductClarification> getProductClarification(@Valid DataTablesInput input) {

        CurrentUser usr = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int companyId = usr.getSelectedCompany().getId();

        return dao.findAll(input, null, (root, criteriaQuery, cb) -> {
            criteriaQuery.distinct(true);
            return cb.and(
                    cb.equal(root.join("product").join("company").get("id"), companyId)
            );
        });
    }



}
