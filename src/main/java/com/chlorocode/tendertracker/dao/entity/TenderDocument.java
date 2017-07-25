package com.chlorocode.tendertracker.dao.entity;

import javax.persistence.*;

@Entity
@Table(name = "tender_document")
public class TenderDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne
    private Tender tender;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="document_id", unique= true, nullable=false)
    private Document document;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Tender getTender() {
        return tender;
    }

    public void setTender(Tender tender) {
        this.tender = tender;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }
}
