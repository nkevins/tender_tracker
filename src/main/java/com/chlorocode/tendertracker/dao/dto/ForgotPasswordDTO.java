package com.chlorocode.tendertracker.dao.dto;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.annotation.MatchesPattern;

public class ForgotPasswordDTO {

    @NotBlank(message = "Email address must not be empty.")
    @Email(message = "Email address is invalid.")
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
