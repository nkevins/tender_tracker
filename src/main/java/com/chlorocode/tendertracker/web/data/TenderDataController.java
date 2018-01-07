package com.chlorocode.tendertracker.web.data;

import com.chlorocode.tendertracker.dao.TenderDAO;
import com.chlorocode.tendertracker.dao.entity.*;
import com.fasterxml.jackson.annotation.JsonView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;

@RestController
public class TenderDataController {

    private TenderDAO tenderDAO;

    @Autowired
    public TenderDataController(TenderDAO tenderDAO) {
        this.tenderDAO = tenderDAO;
    }

    @JsonView(DataTablesOutput.View.class)
    @RequestMapping(value = "/admin/data/tenders", method = RequestMethod.GET)
    public DataTablesOutput<Tender> getTenders(@Valid DataTablesInput input) {
        CurrentUser usr = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int companyId = usr.getSelectedCompany().getId();

        DataTablesOutput<Tender> tenders = tenderDAO.findAll(input, null, (root, criteriaQuery, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.join("company").get("id"), companyId);
        });

        return tenders;
    }

    @JsonView(DataTablesOutput.View.class)
    @RequestMapping(value = "/admin/data/evaluatetenders", method = RequestMethod.GET)
    public DataTablesOutput<Tender> getEvaluateTenderList(@Valid DataTablesInput input) {
        CurrentUser usr = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int companyId = usr.getSelectedCompany().getId();

        DataTablesOutput<Tender> tenders = tenderDAO.findAll(input, null, (root, criteriaQuery, criteriaBuilder) -> {
            return criteriaBuilder.and(
                    criteriaBuilder.equal(root.join("company").get("id"), companyId),
                    criteriaBuilder.equal(root.get("status"), 2)
            );
        });

        return tenders;
    }

    @JsonView(DataTablesOutput.View.class)
    @RequestMapping(value = "/admin/data/awardtenders", method = RequestMethod.GET)
    public DataTablesOutput<Tender> getAwardTenderList(@Valid DataTablesInput input) {
        CurrentUser usr = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int companyId = usr.getSelectedCompany().getId();

        DataTablesOutput<Tender> tenders = tenderDAO.findAll(input, null, (root, criteriaQuery, criteriaBuilder) -> {
            return criteriaBuilder.and(
                    criteriaBuilder.equal(root.join("company").get("id"), companyId),
                    criteriaBuilder.or(
                            criteriaBuilder.equal(root.get("status"), 2),
                            criteriaBuilder.equal(root.get("status"), 3)
                    )
            );
        });

        return tenders;
    }

    @RequestMapping(value = "/admin/data/awardtenders/overallprice/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String getTenderOverallPrice(@PathVariable(value="id") Integer id) throws JSONException {
        JSONArray data = new JSONArray();

        Tender tender = tenderDAO.findOne(id);
        for (Bid b : tender.getBids()) {
            JSONObject bidInfo = new JSONObject();
            bidInfo.put("y", b.getCompany().getName());
            bidInfo.put("a", b.getTotalAmount());
            data.put(bidInfo);
        }

        return data.toString();
    }

    @RequestMapping(value = "/admin/data/awardtenders/itemPriceComparission/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String getTenderItemPriceComparisson(@PathVariable(value="id") Integer id) throws JSONException {
        JSONObject info = new JSONObject();
        JSONArray data = new JSONArray();
        JSONArray yAxis = new JSONArray();
        JSONArray label = new JSONArray();

        HashSet<String> yAxisSet = new HashSet();
        HashSet<String> labelSet = new HashSet();

        Tender tender = tenderDAO.findOne(id);
        for (TenderItem i : tender.getItems()) {
            JSONObject tenderItem = new JSONObject();
            tenderItem.put("y", i.getDescription());

            for (Bid b : tender.getBids()) {
                for (BidItem bi : b.getBidItems()) {
                    if (bi.getTenderItem().equals(i)) {
                        tenderItem.put(Integer.toString(b.getCompany().getId()), bi.getAmount());

                        if (!yAxisSet.contains(Integer.toString(b.getCompany().getId()))) {
                            yAxisSet.add(Integer.toString(b.getCompany().getId()));
                            yAxis.put(Integer.toString(b.getCompany().getId()));
                        }

                        if (!labelSet.contains(b.getCompany().getName())) {
                            labelSet.add(b.getCompany().getName());
                            label.put(b.getCompany().getName());
                        }
                    }
                }
            }

            data.put(tenderItem);
        }

        info.put("data", data);
        info.put("yAxis", yAxis);
        info.put("label", label);

        return info.toString();
    }
}
