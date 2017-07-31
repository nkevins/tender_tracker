package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.User;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDataDAO extends DataTablesRepository<User, Integer> {
}
