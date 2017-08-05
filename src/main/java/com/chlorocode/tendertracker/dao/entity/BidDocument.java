package com.chlorocode.tendertracker.dao.entity;

import javax.persistence.*;

@Entity
@Table(name = "bid_document")
public class BidDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne
    private Bid bid;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="document_id", unique= true, nullable=false)
    private Document document;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Bid getBid() {
        return bid;
    }

    public void setBid(Bid bid) {
        this.bid = bid;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }
}
