package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.UserDAO;
import com.chlorocode.tendertracker.dao.UserRoleDAO;
import com.chlorocode.tendertracker.dao.entity.Company;
import com.chlorocode.tendertracker.dao.entity.CurrentUser;
import com.chlorocode.tendertracker.dao.entity.User;
import com.chlorocode.tendertracker.logging.TTLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private String className;
    private UserDAO userDAO;
    private UserRoleDAO userRoleDAO;

    @Autowired
    public AuthServiceImpl(UserDAO userDAO, UserRoleDAO userRoleDAO) {
        this.className = this.getClass().getName();
        this.userDAO = userDAO;
        this.userRoleDAO = userRoleDAO;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        TTLogger.info(className, email + " attempt login");

        User user;
        try {
            user = userDAO.findOneByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not Found"));
        } catch (UsernameNotFoundException ex) {
            TTLogger.info(className, email + " login attempt failed, user not found");
            throw ex;
        }

        TTLogger.info(className, email + " user found");

        String role = "ROLE_USER";

        List<String> userRoles = userRoleDAO.findUniqueUserRole(user.getId());
        for (String ur : userRoles) {
            role += ",ROLE_" + ur;
        }

        TTLogger.debug(className, email + " user roles authorized: " + role);

        List<Company> companyAdministered = userRoleDAO.findUserAdministeredCompany(user.getId());

        TTLogger.debug(className, email + " managed companies: " + companyAdministered.stream().map(Company::getName)
                .collect(Collectors.joining(",")));

        return new CurrentUser(user, AuthorityUtils.commaSeparatedStringToAuthorityList(role), companyAdministered);
    }
}
