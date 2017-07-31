package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.entity.Company;
import com.chlorocode.tendertracker.dao.entity.Role;
import com.chlorocode.tendertracker.dao.entity.User;
import com.chlorocode.tendertracker.dao.entity.UserRole;

import java.util.List;

/**
 * Created by andy on 27/7/2017.
 */
public interface UserRoleService {

    Role findRoleById(int id);
    UserRole findUserRoleById(int id);
    List<Role> findCompanyUserRole(int userId, int companyId);

    void addUserRole(User user, List<Role> roles, Company company, int createdBy);
    void updateUserRole(User user, List<Role> roles, Company company, int updatedBy);
    void removeUserFromCompany(User user, Company company);
}
