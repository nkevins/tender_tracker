package com.chlorocode.tendertracker.dao.dto;

import com.chlorocode.tendertracker.constants.TTConstants;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;

public class UserRegistrationDTO {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    private int IdType;

    @NotBlank(message = "Id No is required")
    private String idNo;

    @NotBlank(message = "Contact No is required")
    private String contactNo;

    @NotBlank(message = "Password is required")
    @Pattern(regexp = TTConstants.PASSWORD_COMPLEXITY, message = "Password must be non space characters and at least 6 characters.")
    private String password;

    @NotBlank(message = "Confirm Password is required")
    private String confirmPassword;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
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

    public int getIdType() {
        return IdType;
    }

    public void setIdType(int idType) {
        IdType = idType;
    }

    public String getIdNo() {
        return idNo;
    }

    public void setIdNo(String idNo) {
        this.idNo = idNo;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
