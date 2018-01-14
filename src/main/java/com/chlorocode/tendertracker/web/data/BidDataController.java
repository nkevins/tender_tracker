package com.chlorocode.tendertracker.web.data;

import com.chlorocode.tendertracker.dao.BidDAO;
import com.chlorocode.tendertracker.dao.entity.Bid;
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

@RestController
public class BidDataController {

    private BidDAO bidDAO;

    @Autowired
    public BidDataController(BidDAO bidDAO) {
        this.bidDAO = bidDAO;
    }

    @JsonView(DataTablesOutput.View.class)
    @RequestMapping(value = "/admin/data/bids", method = RequestMethod.GET)
    public DataTablesOutput<Bid> getTenders(@Valid DataTablesInput input) {
        CurrentUser usr = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int companyId = usr.getSelectedCompany().getId();

        DataTablesOutput<Bid> bids = bidDAO.findAll(input, null, (root, criteriaQuery, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.join("company").get("id"), companyId);
        });

        return bids;
    }
}
