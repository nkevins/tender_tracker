package com.chlorocode.tendertracker.dao.dto;

import com.chlorocode.tendertracker.dao.entity.CorrigendumDocument;
import com.chlorocode.tendertracker.dao.entity.Tender;

import java.util.List;

/**
 * Data transfer object for corrigendum update.
 */
public class CorrigendumUpdateDTO {

    private int id;
    private String description;
    private Tender tender;
    private List<CorrigendumDocument> documents;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Tender getTender() {
        return tender;
    }

    public void setTender(Tender tender) {
        this.tender = tender;
    }

    public List<CorrigendumDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(List<CorrigendumDocument> documents) {
        this.documents = documents;
    }
}
