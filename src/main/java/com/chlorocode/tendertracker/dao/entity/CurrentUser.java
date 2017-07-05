package com.chlorocode.tendertracker.dao.entity;

import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public class CurrentUser extends org.springframework.security.core.userdetails.User {

    private User user;
    private List<Company> companyAdministered;
    private Company selectedCompany;

    public CurrentUser(User user, List<GrantedAuthority> roles, List<Company> companyAdministered) {
        super(user.getEmail(), user.getPassword(), roles);
        this.user = user;

        this.companyAdministered = companyAdministered;
        if (companyAdministered.size() == 1) {
            selectedCompany = companyAdministered.get(0);
        } else {
            selectedCompany = null;
        }
    }

    public String getName() {
        return user.getName();
    }

    public User getUser() {
        return user;
    }

    public List<Company> getCompanyAdministered() {
        return companyAdministered;
    }

    public Company getSelectedCompany() {
        return selectedCompany;
    }

    public void setSelectedCompany(Company selectedCompany) {
        this.selectedCompany = selectedCompany;
    }
}
