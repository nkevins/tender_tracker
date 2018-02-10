package com.chlorocode.tendertracker.dao.entity;

import javax.persistence.*;

/**
 * CodeValue entity.
 */
@Entity
@Table(name = "code_value")
public class CodeValue {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String type;
    private int code;
    private String description;
    private int order;

    public CodeValue() {

    }

    public CodeValue(String type, int code, String description, int order) {
        this.type = type;
        this.code = code;
        this.description = description;
        this.order = order;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
