package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.constants.TTConstants;
import com.chlorocode.tendertracker.dao.CodeValueDAO;
import com.chlorocode.tendertracker.dao.entity.CodeValue;
import com.chlorocode.tendertracker.dao.entity.Country;
import com.chlorocode.tendertracker.dao.entity.TenderCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CodeValueServiceImpl implements CodeValueService {

    private CodeValueDAO codeValueDAO;

    @Autowired
    public CodeValueServiceImpl(CodeValueDAO codeValueDAO) {
        this.codeValueDAO = codeValueDAO;
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
        return codeValueDAO.getAllTenderCategories();
    }

    @Override
    public TenderCategory getTenderCategoryById(int id) {
        return codeValueDAO.getTenderCategoryById(id);
    }

    @Override
    public Map<String, String> getTenderOrderModes() {
        Map<String, String> orderMode = new HashMap<>();
        orderMode.put(TTConstants.TITLE, TTConstants.LBL_TITLE);
        orderMode.put(TTConstants.OPEN_DATE, TTConstants.LBL_OPEN_DATE);
        orderMode.put(TTConstants.CLOSED_DATE, TTConstants.LBL_CLOSED_DATE);

        return orderMode;
    }

    @Override
    public List<Country> getAllCountries() {
        return codeValueDAO.getAllCountries();
    }
}
