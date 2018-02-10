package com.chlorocode.tendertracker.dao.dto;

import org.hibernate.validator.constraints.NotBlank;

import java.util.LinkedList;
import java.util.List;

/**
 * Data transfer object for company user add.
 */
public class CompanyUserAddDTO {
    @NotBlank(message = "Email is required")
    private String email;

    private List<Integer> roles;

    public CompanyUserAddDTO() {
        roles = new LinkedList<>();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Integer> getRoles() {
        return roles;
    }

    public void setRoles(List<Integer> roles) {
        this.roles = roles;
    }
}
