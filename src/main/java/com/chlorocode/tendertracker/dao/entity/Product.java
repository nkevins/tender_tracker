package com.chlorocode.tendertracker.dao.entity;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import javax.persistence.*;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView(DataTablesOutput.View.class)
    private long productCode;

    @JsonView(DataTablesOutput.View.class)
    private String title;

    @JsonView(DataTablesOutput.View.class)
    private double price;

    @JsonView(DataTablesOutput.View.class)
    private String description;

    @JsonView(DataTablesOutput.View.class)
    private boolean publish;

    private int type;

    private int category;

    @ManyToOne
    private Company company;

    public long getProductCode() {
        return productCode;
    }

    public void setProductCode(long productCode) {
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

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public boolean isPublish() {
        return publish;
    }

    public void setPublish(boolean publish) {
        this.publish = publish;
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
