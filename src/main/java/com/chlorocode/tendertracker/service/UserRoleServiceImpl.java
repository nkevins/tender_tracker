package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.UserDAO;
import com.chlorocode.tendertracker.dao.UserRoleDAO;
import com.chlorocode.tendertracker.dao.entity.Company;
import com.chlorocode.tendertracker.dao.entity.Role;
import com.chlorocode.tendertracker.dao.entity.User;
import com.chlorocode.tendertracker.dao.entity.UserRole;
import com.chlorocode.tendertracker.exception.ApplicationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

/**
 * Created by andy on 27/7/2017.
 */
@Service
public class UserRoleServiceImpl implements UserRoleService {

    private UserRoleDAO userRoleDao;
    private UserDAO userDAO;

    @Autowired
    public UserRoleServiceImpl(UserRoleDAO userRoleDao, UserDAO userDAO){
        this.userRoleDao = userRoleDao;
        this.userDAO = userDAO;
    }

    @Override
    public Role findRoleById(int id) {
        return userRoleDao.findRoleById(id);
    }

    @Override
    public UserRole findUserRoleById(int id) {
        return userRoleDao.findOne(id);
    }

    @Override
    public List<Role> findCompanyUserRole(int userId, int companyId) {
        return userRoleDao.findRoleForCompanyUser(userId, companyId);
    }

    @Override
    public void addUserRole(User user, List<Role> roles, Company company, int createdBy) throws ApplicationException {
        // Check if user already belong to company
        List<Role> existingRoles = userRoleDao.findRoleForCompanyUser(user.getId(), company.getId());
        if (existingRoles != null && existingRoles.size() > 0) {
            throw new ApplicationException("User already a company member");
        }

        List<UserRole> newRoles = new LinkedList<>();
        for (Role r : roles) {
            UserRole userRole = new UserRole();
            userRole.setCompany(company);
            userRole.setUser(user);
            userRole.setRole(r);
            userRole.setCreatedBy(createdBy);
            userRole.setCreatedDate(new Date());
            userRole.setLastUpdatedBy(createdBy);
            userRole.setLastUpdatedDate(new Date());

            newRoles.add(userRole);
        }

        userRoleDao.save(newRoles);
    }

    @Override
    @Transactional
    public void updateUserRole(User user, List<Role> roles, Company company, int updatedBy) {
        removeUserFromCompany(user, company);
        addUserRole(user, roles, company, updatedBy);
    }

    @Override
    @Transactional
    public void removeUserFromCompany(User user, Company company) {
        List<UserRole> userRoles = userRoleDao.findUserRoleByUserAndCompany(user.getId(), company.getId());
        userRoleDao.delete(userRoles);
    }

    @Override
    public Set<String> findCompanyAdminEmails(int companyId) {
        List<UserRole> adminUserRoles = userRoleDao.findCompanyAdminUserRole(companyId);
        if (adminUserRoles != null) {
            Set<String> adminEmails = new HashSet<>();
            for(UserRole admin : adminUserRoles) {
                if (admin.getUser() != null && admin.getUser().getEmail() != null) {
                    adminEmails.add(admin.getUser().getEmail());
                }
            }
            if (adminEmails.size() > 0) {
                return adminEmails;
            }
        }
        return null;
    }

    @Override
    public Set<String> findCompanyUserEmails(int companyId) {
        List<UserRole> userRoles = userRoleDao.findUserRoleByCompany(companyId);
        if (userRoles != null) {
            Set<String> emails = new HashSet<>();
            for(UserRole admin : userRoles) {
                if (admin.getUser() != null && admin.getUser().getEmail() != null) {
                    emails.add(admin.getUser().getEmail());
                }
            }
            if (emails.size() > 0) {
                return emails;
            }
        }
        return null;
    }
}
