package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.constants.TTConstants;
import com.chlorocode.tendertracker.dao.UserDAO;
import com.chlorocode.tendertracker.dao.UserRoleDAO;
import com.chlorocode.tendertracker.dao.entity.Company;
import com.chlorocode.tendertracker.dao.entity.CurrentUser;
import com.chlorocode.tendertracker.dao.entity.User;
import com.chlorocode.tendertracker.dao.entity.UserRole;
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
        if (user.getStatus() != TTConstants.ACCOUNT_STATIC_ACTIVE) {
            throw new RuntimeException("locked");
        }

        String role = "ROLE_USER";

        List<Company> companyAdministered = userRoleDAO.findUserAdministeredCompany(user.getId());

        TTLogger.debug(className, email + " managed companies: " + companyAdministered.stream().map(Company::getName)
                .collect(Collectors.joining(",")));

        // For user that belong only to one company, assign the role for that particular company
        if (companyAdministered.size() == 1) {
            List<UserRole> companyRoles = userRoleDAO.findUserRoleByUserAndCompany(user.getId(), companyAdministered.get(0).getId());
            for (UserRole r : companyRoles) {
                role += ",ROLE_" + r.getRole().getName();
            }
            return new CurrentUser(user, AuthorityUtils.commaSeparatedStringToAuthorityList(role), companyAdministered);
        }

        return new CurrentUser(user, AuthorityUtils.commaSeparatedStringToAuthorityList(role), companyAdministered, true);
    }
}
