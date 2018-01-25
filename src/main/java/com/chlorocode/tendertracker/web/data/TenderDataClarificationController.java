package com.chlorocode.tendertracker.web.data;

import com.chlorocode.tendertracker.dao.TenderDataClarificationDAO;
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
 * REST controller for tender clarification.
 */
@RestController
public class TenderDataClarificationController {

    private TenderDataClarificationDAO clariDao;

    /**
     * Constructor.
     *
     * @param clariDao TenderDataClarificationDAO
     */
    @Autowired
    public TenderDataClarificationController(TenderDataClarificationDAO clariDao){
        this.clariDao = clariDao;
    }

    /**
     * This method is used to search tender clarifications.
     *
     * @param input data table search criteria input
     * @return data table output
     */
    @JsonView(DataTablesOutput.View.class)
    @RequestMapping(value = "/admin/data/tenderClarification", method = RequestMethod.GET)
    public DataTablesOutput<Clarification> getTenders(@Valid DataTablesInput input) {

        CurrentUser usr = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int companyId = usr.getSelectedCompany().getId();

        return clariDao.findAll(input, null, (root, criteriaQuery, cb) -> {
            criteriaQuery.distinct(true);
            return cb.and(
                    cb.equal(root.join("tender").join("company").get("id"), companyId)
            );
        });
    }
}
