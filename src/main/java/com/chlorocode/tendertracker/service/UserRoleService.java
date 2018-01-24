package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.entity.Company;
import com.chlorocode.tendertracker.dao.entity.Role;
import com.chlorocode.tendertracker.dao.entity.User;
import com.chlorocode.tendertracker.dao.entity.UserRole;

import java.util.List;
import java.util.Set;

/**
 * Service interface for user role.
 */
public interface UserRoleService {

    /**
     * This method is used to find role by id.
     *
     * @param id unique identifier of the role
     * @return Role
     */
    Role findRoleById(int id);

    /**
     * This method is used to find user role by id.
     *
     * @param id unique identifier of the user role
     * @return UserRole
     */
    UserRole findUserRoleById(int id);

    /**
     * This method is used to find role that a user has for a particular company.
     *
     * @param userId unique identifier of the user
     * @param companyId unique identifier of the company
     * @return list of role
     */
    List<Role> findCompanyUserRole(int userId, int companyId);

    /**
     * This method is used to add new role for a particular user for a particular company.
     *
     * @param user user object
     * @param roles list of new roles to be added
     * @param company company object
     * @param createdBy user id who add the new roles
     */
    void addUserRole(User user, List<Role> roles, Company company, int createdBy);

    /**
     * This method is used to replace user's existing role to a new list of roles.
     *
     * @param user user object
     * @param roles list of new roles to be added
     * @param company company object
     * @param updatedBy user id who update the roles
     */
    void updateUserRole(User user, List<Role> roles, Company company, int updatedBy);

    /**
     * This method is used to remove user from company.
     *
     * @param user user object
     * @param company company object
     */
    void removeUserFromCompany(User user, Company company);

    /**
     * This method is used to find list of all email addresses of company admin.
     *
     * @param companyId unique identifier of the company
     * @return emails
     */
    Set<String> findCompanyAdminEmails(int companyId);

    /**
     * This method is used to find list of all email addresses of company user.
     *
     * @param companyId unique identifier of the company
     * @return emails
     */
    Set<String> findCompanyUserEmails(int companyId);
}
