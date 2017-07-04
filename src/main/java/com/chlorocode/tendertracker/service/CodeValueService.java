package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.entity.CodeValue;

import java.util.List;

public interface CodeValueService {

    List<CodeValue> getByType(String type);
}
