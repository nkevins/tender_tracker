package com.chlorocode.tendertracker.web.data;

import com.chlorocode.tendertracker.dao.CompanyDAO;
import com.chlorocode.tendertracker.dao.entity.Company;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class CompanyDataController {

    private CompanyDAO companyDAO;

    @Autowired
    public CompanyDataController(CompanyDAO companyDAO) {
        this.companyDAO = companyDAO;
    }

    @JsonView(DataTablesOutput.View.class)
    @RequestMapping(value = "/sysadm/data/companyregistrations", method = RequestMethod.GET)
    public DataTablesOutput<Company> getUsers(@Valid DataTablesInput input) {
        return companyDAO.findAll(input, null, (root, criteriaQuery, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("status"), 0);
        });
    }
}
