package com.chlorocode.tendertracker.dao.dto;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;

public class ProductCreateDTO {

    @NotBlank(message = "Title is required")
    private String title;

    @Valid
    private double price;

    private String description;

    private int type;

    private int category;

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
}
