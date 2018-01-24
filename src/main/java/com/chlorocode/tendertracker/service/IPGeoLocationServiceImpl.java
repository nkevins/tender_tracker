package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.entity.TenderVisit;
import com.chlorocode.tendertracker.logging.TTLogger;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Date;

/**
 * Service implementation of IPGeoLocationService using ip-api.com service.
 */
@Service
public class IPGeoLocationServiceImpl implements IPGeoLocationService {

    public static final String SERVICE_URL = "http://ip-api.com/json/";
    private String className;

    /**
     * Constructor.
     */
    public IPGeoLocationServiceImpl() {
        this.className = this.getClass().getName();
    }

    @Override
    public TenderVisit getIPDetails(String ip) {
        try {
            InputStream is = new URL(SERVICE_URL + ip.trim() + "?fields=126975").openStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);

            if (json.getString("status").equals("success")) {
                TenderVisit visit = new TenderVisit();
                visit.setIpAddress(ip);
                visit.setVisitDate(new Date());
                visit.setCountry(json.getString("country"));
                visit.setCountryCode(json.getString("countryCode"));
                visit.setRegion(json.getString("region"));
                visit.setRegionName(json.getString("regionName"));
                visit.setCity(json.getString("city"));
                visit.setZip(json.getString("zip"));
                visit.setLat(json.getDouble("lat"));
                visit.setLon(json.getDouble("lon"));
                visit.setTimeZone(json.getString("timezone"));
                visit.setIsp(json.getString("isp"));
                visit.setOrg(json.getString("org"));
                visit.setAs(json.getString("as"));
                visit.setMobile(json.getBoolean("mobile"));

                return visit;
            } else {
                return null;
            }

        } catch (Exception e) {
            TTLogger.error(className, "Error getting IP Geo details", e);
            return null;
        }
    }

    private String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
}
