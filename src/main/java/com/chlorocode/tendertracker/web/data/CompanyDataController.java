package com.chlorocode.tendertracker.web.data;

import com.chlorocode.tendertracker.dao.CompanyDAO;
import com.chlorocode.tendertracker.dao.TenderDAO;
import com.chlorocode.tendertracker.dao.entity.Company;
import com.chlorocode.tendertracker.dao.entity.Tender;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.io.CharStreams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
public class CompanyDataController {

    private CompanyDAO companyDAO;
    private TenderDAO tenderDAO;

    @Autowired
    public CompanyDataController(CompanyDAO companyDAO, TenderDAO tenderDAO) {
        this.companyDAO = companyDAO;
        this.tenderDAO = tenderDAO;
    }

    @JsonView(DataTablesOutput.View.class)
    @RequestMapping(value = "/sysadm/data/companyregistrations", method = RequestMethod.GET)
    public DataTablesOutput<Company> getCompanyRegistrations(@Valid DataTablesInput input) {
        return companyDAO.findAll(input, null, (root, criteriaQuery, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("status"), 0);
        });
    }

    @JsonView(DataTablesOutput.View.class)
    @RequestMapping(value = "/sysadm/data/companyinfolist", method = RequestMethod.GET)
    public DataTablesOutput<Company> getCompanyInfoList(@Valid DataTablesInput input) {
        return companyDAO.findAll(input, null, (root, criteriaQuery, criteriaBuilder) -> {
            return criteriaBuilder.notEqual(root.get("status"), 0);
        });
    }

    @RequestMapping(path = "/admin/data/company", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String getCompanyName(@RequestParam(value="name", defaultValue="") String name)
            throws JSONException {
        JSONArray companies = new JSONArray();
        if (!name.trim().isEmpty()) {
            List<Company> companyData = companyDAO.findCompanyByName(name);
            for (Company c : companyData) {
                JSONObject company = new JSONObject();
                company.put("id", c.getId());
                company.put("name", c.getName());
                companies.put(company);
            }
        }
        return companies.toString();
    }

    @RequestMapping(path = "/admin/data/invitedCompany/{tenderId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getTenderInvitedCompany(@PathVariable(value="tenderId") int id)
            throws JSONException {
        JSONArray companies = new JSONArray();
        Tender tender = tenderDAO.findOne(id);

        if (tender == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        for (Company c : tender.getInvitedCompanies()) {
            JSONObject company = new JSONObject();
            company.put("id", Integer.toString(c.getId()));
            company.put("name", c.getName());
            companies.put(company);
        }
        return new ResponseEntity<>(companies.toString(), HttpStatus.OK);
    }

    @RequestMapping(path = "/admin/data/invitedCompany/{tenderId}", method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity removeTenderInvitedCompany(@PathVariable(value="tenderId") int id, HttpServletRequest request)
            throws JSONException, IOException {
        Tender tender = tenderDAO.findOne(id);
        JSONObject json = new JSONObject(CharStreams.toString(request.getReader()));

        if (tender == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        } else if (tender.getTenderType() != 2) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        for (Company c : tender.getInvitedCompanies()) {
            if (c.getId() == json.getInt("companyId")) {
                if (tender.getInvitedCompanies().size() == 1) {
                    return new ResponseEntity("Not allowed to removed all invited company for Closed Tender", HttpStatus.EXPECTATION_FAILED);
                }

                tender.getInvitedCompanies().remove(c);
                tenderDAO.save(tender);

                return new ResponseEntity(HttpStatus.OK);
            }
        }

        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(path = "/admin/data/invitedCompany/{tenderId}", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity addTenderInvitedCompany(@PathVariable(value="tenderId") int id, HttpServletRequest request)
            throws JSONException, IOException {
        Tender tender = tenderDAO.findOne(id);
        JSONObject json = new JSONObject(CharStreams.toString(request.getReader()));

        if (tender == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        } else if (tender.getTenderType() != 2) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        Company company = companyDAO.findOne(json.getInt("companyId"));
        if (company == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        for (Company c : tender.getInvitedCompanies()) {
            if (c.getId() == company.getId()) {
                // if company already exist, skip add process
                return new ResponseEntity(HttpStatus.OK);
            }
        }
        tender.addInvitedCompany(company);
        tenderDAO.save(tender);

        return new ResponseEntity(HttpStatus.OK);
    }
}
