package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.entity.CodeValue;
import com.chlorocode.tendertracker.dao.entity.Country;
import com.chlorocode.tendertracker.dao.entity.TenderCategory;

import java.util.List;
import java.util.Map;

/**
 * Service interface for code value.
 */
public interface CodeValueService {

    /**
     * This method is used to get all CodeValue for a particular type.
     *
     * @param type code value type
     * @return list of CodeValue
     */
    List<CodeValue> getByType(String type);

    /**
     * This method is used to get a code value description based on code value type and code.
     *
     * @param type code value type
     * @param code code value code
     * @return String
     */
    String getDescription(String type, int code);

    /**
     * This method is used to get all tender categories.
     *
     * @return list of TenderCategory
     */
    List<TenderCategory> getAllTenderCategories();

    /**
     * This method is used to get a tender category by tender category id.
     *
     * @param id unique identifier of the tender category
     * @return TenderCategory
     */
    TenderCategory getTenderCategoryById(int id);

    /**
     * This method is used to get all tender order by search criteria.
     * e.g. order by title, opening date.
     *
     * @return Map of criteria name and description to be displayed
     */
    Map<String, String> getTenderOrderBy();

    /**
     * This method is used to get all tender order mode search criteria.
     * e.g. order ascending / descending.
     *
     * @return Map of order mode and description to be displayed
     */
    Map<String, String> getTenderOrderMode();

    /**
     * This method is used to get all product order by search criteria.
     * e.g. order by title, price.
     *
     * @return Map of criteria name and description to be displayed
     */
    Map<String, String> getProductOrderBy();

    /**
     * This method is used to get all tender order mode search criteria.
     * e.g. order ascending / descending.
     *
     * @return Map of order mode and description to be displayed
     */
    Map<String, String> getProductOrderMode();

    /**
     * This method is used to retrieve all countries from database.
     *
     * @return List of Country
     */
    List<Country> getAllCountries();
}
