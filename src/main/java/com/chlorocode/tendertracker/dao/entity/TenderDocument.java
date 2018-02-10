package com.chlorocode.tendertracker.dao.entity;

import javax.persistence.*;

/**
 * TenderDocument entity.
 */
@Entity
@Table(name = "tender_document")
@PrimaryKeyJoinColumn(name = "document_id")
@DiscriminatorValue("1")
public class TenderDocument extends Document {

    @ManyToOne
    private Tender tender;

    public Tender getTender() {
        return tender;
    }

    public void setTender(Tender tender) {
        this.tender = tender;
    }
}
