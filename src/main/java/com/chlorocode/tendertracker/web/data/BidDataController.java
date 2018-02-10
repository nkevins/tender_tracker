package com.chlorocode.tendertracker.web.data;

import com.chlorocode.tendertracker.dao.BidDAO;
import com.chlorocode.tendertracker.dao.TenderDataAppealDAO;
import com.chlorocode.tendertracker.dao.entity.Bid;
import com.chlorocode.tendertracker.dao.entity.CurrentUser;
import com.chlorocode.tendertracker.dao.entity.TenderAppeal;
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
 * REST Controller for bid.
 */
@RestController
public class BidDataController {

    private BidDAO bidDAO;
    private TenderDataAppealDAO dao;

    /**
     * Constructor
     *
     * @param bidDAO BidDAO
     * @param dao TenderDataAppealDAO
     */
    @Autowired
    public BidDataController(BidDAO bidDAO, TenderDataAppealDAO dao) {
        this.bidDAO = bidDAO;
        this.dao = dao;
    }

    /**
     * This method is used to return list of bids for DataTable.
     *
     * @param input DataTable search input
     * @return DataTable output
     */
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


    /**
     * This method is used to return tender appeal list for DataTable.
     *
     * @param input DataTable search input
     * @return DataTable output
     */
    @JsonView(DataTablesOutput.View.class)
    @RequestMapping(value = "/admin/data/tenderappeal", method = RequestMethod.GET)
    public DataTablesOutput<TenderAppeal> getTendersAppeal(@Valid DataTablesInput input) {
        CurrentUser usr = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int companyId = usr.getSelectedCompany().getId();

        DataTablesOutput<TenderAppeal> bids = dao.findAll(input, null, (root, criteriaQuery, criteriaBuilder) -> {
            return
            criteriaBuilder.and(
                    criteriaBuilder.equal(root.join("tender").join("company").get("id"), companyId)
            );
        });

        return bids;
    }
}
