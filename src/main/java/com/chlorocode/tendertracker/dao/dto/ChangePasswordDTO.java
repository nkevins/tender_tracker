package com.chlorocode.tendertracker.dao.dto;

import com.chlorocode.tendertracker.constants.TTConstants;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;

/**
 * Kyaw Min Thu
 */
public class ChangePasswordDTO {

    private String email;

    @NotBlank
    @Pattern(regexp = TTConstants.PASSWORD_COMPLEXITY, message = "Password must be non space characters and at least 6 characters.")
    private String password;

    @NotBlank
    private String confirmPassword;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
