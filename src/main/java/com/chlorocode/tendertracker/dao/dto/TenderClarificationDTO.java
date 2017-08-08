package com.chlorocode.tendertracker.dao.dto;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by andy on 8/8/2017.
 */
public class TenderClarificationDTO {
    private int id;

    @NotBlank(message = "Please enter your response")
    private String response;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
