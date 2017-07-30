package com.chlorocode.tendertracker.dao.dto;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class TenderSearchDTO {

    private String title;

    private String companyName;

//    private int tenderCategory;

    private String status;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

//    public int getTenderCategory() {
//        return tenderCategory;
//    }
//
//    public void setTenderCategory(int tenderCategory) {
//        this.tenderCategory = tenderCategory;
//    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
