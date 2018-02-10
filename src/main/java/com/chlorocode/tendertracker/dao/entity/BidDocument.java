package com.chlorocode.tendertracker.dao.entity;

import javax.persistence.*;

/**
 * BidDocument entity.
 */
@Entity
@Table(name = "bid_document")
@PrimaryKeyJoinColumn(name = "document_id")
@DiscriminatorValue("3")
public class BidDocument extends Document {

    @ManyToOne
    private Bid bid;

    public Bid getBid() {
        return bid;
    }

    public void setBid(Bid bid) {
        this.bid = bid;
    }

}
