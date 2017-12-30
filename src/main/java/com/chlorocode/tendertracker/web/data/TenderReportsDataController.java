package com.chlorocode.tendertracker.web.data;

import com.chlorocode.tendertracker.dao.TenderDAO;
import com.chlorocode.tendertracker.dao.TenderDataClarificationDAO;
import com.chlorocode.tendertracker.dao.dto.ProcurementReportDTO;
import com.chlorocode.tendertracker.dao.entity.Clarification;
import com.chlorocode.tendertracker.dao.entity.CurrentUser;
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
 * Created by andy on 8/8/2017.
 */
@RestController
public class TenderReportsDataController {
    private TenderDAO tenderDAO;

    @Autowired
    public TenderReportsDataController(TenderDAO tenderDAO){
        this.tenderDAO = tenderDAO;
    }

//    @JsonView(DataTablesOutput.View.class)
//    @RequestMapping(value = "/admin/tender/reports", method = RequestMethod.GET)
//    public DataTablesOutput<ProcurementReportDTO> getTenders(@Valid DataTablesInput input) {
//
////        CurrentUser usr = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
////        int companyId = usr.getSelectedCompany().getId();
//
////        return clariDao.findAll(input, null, (root, criteriaQuery, criteriaBuilder) -> {
////           // return criteriaBuilder.equal(root.join("tender.company").get("id"), companyId);
////            );
////
////        });
//        return tenderDAO.findAllByDateRange(input, null, (root, criteriaQuery, cb) -> {
//            criteriaQuery.distinct(true);
//            return cb.and(
//                    cb.equal(root.join("tender").join("company").get("id"), companyId)
//            );
//        });
//    }
}
