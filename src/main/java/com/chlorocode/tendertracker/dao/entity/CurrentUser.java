package com.chlorocode.tendertracker.dao.entity;

import org.springframework.security.core.GrantedAuthority;

import java.util.List;

/**
 * This class is used for login authentication by spring framework.
 */
public class CurrentUser extends org.springframework.security.core.userdetails.User {

    private User user;
    private List<Company> companyAdministered;
    private Company selectedCompany;
    private boolean needToSelectCompany;

    public CurrentUser(User user, List<GrantedAuthority> roles, List<Company> companyAdministered) {
        super(user.getEmail(), user.getPassword(), roles);
        this.user = user;

        this.companyAdministered = companyAdministered;
        if (companyAdministered.size() == 1) {
            selectedCompany = companyAdministered.get(0);
        } else {
            selectedCompany = null;
        }
        this.needToSelectCompany = false;
    }

    public CurrentUser(User user, List<GrantedAuthority> roles, List<Company> companyAdministered, boolean needToSelectCompany) {
        this(user, roles, companyAdministered);
        this.needToSelectCompany = needToSelectCompany;
    }

    public int getId() {
        return user.getId();
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

    public boolean isNeedToSelectCompany() {
        return needToSelectCompany;
    }

    public void setNeedToSelectCompany(boolean needToSelectCompany) {
        this.needToSelectCompany = needToSelectCompany;
    }
}
