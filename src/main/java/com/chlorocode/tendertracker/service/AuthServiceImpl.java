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

/**
 * Service implementation of AuthService.
 */
@Service
public class AuthServiceImpl implements AuthService {

    private String className;
    private UserDAO userDAO;
    private UserRoleDAO userRoleDAO;

    /**
     * Constructor.
     *
     * @param userDAO UserDAO
     * @param userRoleDAO UserRoleDAO
     */
    @Autowired
    public AuthServiceImpl(UserDAO userDAO, UserRoleDAO userRoleDAO) {
        this.className = this.getClass().getName();
        this.userDAO = userDAO;
        this.userRoleDAO = userRoleDAO;
    }

    /**
     * This method is used to override the default authentication method and implement custom authentication and authorization.
     *
     * @param email user email
     * @return UserDetails
     * @throws UsernameNotFoundException UsernameNotFoundException
     */
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
        List<String> allRoles = userRoleDAO.findUniqueUserRole(user.getId());
        for (String r : allRoles) {
            if (r.equals("SYS_ADMIN")) {
                role += ",ROLE_SYS_ADMIN";
            }
        }

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
        } else if (companyAdministered.size() == 0) {
            // For normal user not belongs to any company
            return new CurrentUser(user, AuthorityUtils.commaSeparatedStringToAuthorityList(role), companyAdministered);
        }

        // For user that belongs to mulltiple company
        return new CurrentUser(user, AuthorityUtils.commaSeparatedStringToAuthorityList(role), companyAdministered, true);
    }
}
