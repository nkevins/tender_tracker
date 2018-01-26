package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.constants.TTConstants;
import com.chlorocode.tendertracker.dao.CodeValueDAO;
import com.chlorocode.tendertracker.dao.CompanyDAO;
import com.chlorocode.tendertracker.dao.CountryDAO;
import com.chlorocode.tendertracker.dao.TenderCategoryDAO;
import com.chlorocode.tendertracker.dao.entity.CodeValue;
import com.chlorocode.tendertracker.dao.entity.Country;
import com.chlorocode.tendertracker.dao.entity.TenderCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service implementation of CodeValueService.
 */
@Service
public class CodeValueServiceImpl implements CodeValueService {

    private CodeValueDAO codeValueDAO;
    private TenderCategoryDAO tenderCategoryDAO;
    private CountryDAO countryDAO;

    /**
     * Constructor.
     *
     * @param codeValueDAO CodeValueDAO
     */
    @Autowired
    public CodeValueServiceImpl(CodeValueDAO codeValueDAO, TenderCategoryDAO tenderCategoryDAO, CountryDAO countryDAO) {
        this.codeValueDAO = codeValueDAO;
        this.tenderCategoryDAO = tenderCategoryDAO;
        this.countryDAO = countryDAO;
    }

    @Override
    public List<CodeValue> getByType(String type) {
        return codeValueDAO.findByTypeOrderByOrderAsc(type);
    }

    @Override
    public String getDescription(String type, int code) {
        return codeValueDAO.getDescription(type, code);
    }

    @Override
    public List<TenderCategory> getAllTenderCategories() {
        return tenderCategoryDAO.getAllTenderCategories();
    }

    @Override
    public TenderCategory getTenderCategoryById(int id) {
        return tenderCategoryDAO.getTenderCategoryById(id);
    }

    @Override
    public Map<String, String> getTenderOrderBy() {
        Map<String, String> orderMode = new HashMap<>();
        orderMode.put(TTConstants.TITLE, TTConstants.LBL_TITLE);
        orderMode.put(TTConstants.OPEN_DATE, TTConstants.LBL_OPEN_DATE);
        orderMode.put(TTConstants.CLOSED_DATE, TTConstants.LBL_CLOSED_DATE);

        return orderMode;
    }

    @Override
    public Map<String, String> getTenderOrderMode() {
        Map<String, String> orderMode = new HashMap<>();
        orderMode.put(TTConstants.ASC, TTConstants.LBL_ASC);
        orderMode.put(TTConstants.DESC, TTConstants.LBL_DESC);

        return orderMode;
    }

    @Override
    public Map<String, String> getProductOrderBy() {
        Map<String, String> orderBy = new HashMap<>();
        orderBy.put(TTConstants.TITLE, TTConstants.LBL_TITLE);
        orderBy.put(TTConstants.CREATE_DATE, TTConstants.LBL_CREATE_DATE);
        orderBy.put(TTConstants.PRICE, TTConstants.LBL_PRICE);

        return orderBy;
    }

    @Override
    public Map<String, String> getProductOrderMode() {
        Map<String, String> orderMode = new HashMap<>();
        orderMode.put(TTConstants.ASC, TTConstants.LBL_ASC);
        orderMode.put(TTConstants.DESC, TTConstants.LBL_DESC);

        return orderMode;
    }

    @Override
    public List<Country> getAllCountries() {
        return countryDAO.getAllCountries();
    }
}
