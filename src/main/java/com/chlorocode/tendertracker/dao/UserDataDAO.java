package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.User;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Repository;

/**
 * This class extends DataTableRepository so that it can output the entity into DataTables.
 */
@Repository
public interface UserDataDAO extends DataTablesRepository<User, Integer> {
}
