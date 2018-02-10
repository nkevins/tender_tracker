package com.chlorocode.tendertracker.dao.dto;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;

/**
 * Data transfer object for product update.
 */
public class ProductUpdateDTO {
    private int productCode;

    @NotBlank(message = "Title is required")
    private String title;

    @Valid
    private double price;

    private String description;

    private int type;

    private int category;

    private String response;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    private int companyId;

    private String CategoryDescription;

    public String getCategoryDescription() {
        return CategoryDescription;
    }

    public void setCategoryDescription(String categoryDescription) {
        CategoryDescription = categoryDescription;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }


    private boolean published;

    public int getProductCode() {
        return productCode;
    }

    public void setProductCode(int productCode) {
        this.productCode = productCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }
}
