package com.chlorocode.tendertracker.web.data;

import com.chlorocode.tendertracker.dao.TenderDAO;
import com.chlorocode.tendertracker.dao.entity.Tender;
import com.chlorocode.tendertracker.dao.entity.TenderVisit;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for report.
 */
@RestController
public class TenderReportsDataController {

    private TenderDAO tenderDAO;

    /**
     * Constructor.
     *
     * @param tenderDAO TenderDAO
     */
    @Autowired
    public TenderReportsDataController(TenderDAO tenderDAO){
        this.tenderDAO = tenderDAO;
    }

    /**
     * This method is used to provide tender visitor country data.
     *
     * @param id unique identifier of the data
     * @return String
     * @throws JSONException if error when forming the JSON response
     */
    @RequestMapping(value = "/admin/data/report/countryVisitorList/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    String getTenderCountryVisitorData(@PathVariable(value="id") Integer id) throws JSONException {
        JSONArray data = new JSONArray();

        Tender tender = tenderDAO.findOne(id);
        for (TenderVisit v : tender.getTenderVisits()) {
            JSONObject coordinate = new JSONObject();
            coordinate.put("lat", v.getLat());
            coordinate.put("lon", v.getLon());
            data.put(coordinate);
        }

        return data.toString();
    }
}
