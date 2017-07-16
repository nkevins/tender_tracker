package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.Company;
import com.chlorocode.tendertracker.dao.entity.User;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;

/**
 * Created by andy on 16/7/2017.
 */
public interface UserDataDAO extends DataTablesRepository<User, Integer> {
}
