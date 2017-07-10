package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.UserDAO;
import com.chlorocode.tendertracker.dao.UserRoleDAO;
import com.chlorocode.tendertracker.dao.entity.Company;
import com.chlorocode.tendertracker.dao.entity.CurrentUser;
import com.chlorocode.tendertracker.dao.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

    private UserDAO userDAO;
    private UserRoleDAO userRoleDAO;

    @Autowired
    public AuthServiceImpl(UserDAO userDAO, UserRoleDAO userRoleDAO) {
        this.userDAO = userDAO;
        this.userRoleDAO = userRoleDAO;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userDAO.findOneByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not Found"));

        String role = "ROLE_USER";

        List<String> userRoles = userRoleDAO.findUniqueUserRole(user.getId());
        for (String ur : userRoles) {
            role += ",ROLE_" + ur;
        }

        List<Company> companyAdministered = userRoleDAO.findUserAdministeredCompany(user.getId());

        return new CurrentUser(user, AuthorityUtils.commaSeparatedStringToAuthorityList(role), companyAdministered);
    }
}
