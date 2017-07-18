package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.entity.CodeValue;
import com.chlorocode.tendertracker.dao.entity.TenderCategory;

import java.util.List;

public interface CodeValueService {

    List<CodeValue> getByType(String type);

    String getDescription(String type, int code);

    List<TenderCategory> getAllTenderCategories();

    TenderCategory getTenderCategoryById(int id);
}
