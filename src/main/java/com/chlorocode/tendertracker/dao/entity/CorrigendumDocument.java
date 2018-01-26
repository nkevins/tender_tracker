package com.chlorocode.tendertracker.dao.entity;

import javax.persistence.*;

/**
 * CorrigendumDocument entity.
 */
@Entity
@Table(name = "corrigendum_document")
@PrimaryKeyJoinColumn(name = "document_id")
@DiscriminatorValue("2")
public class CorrigendumDocument extends Document {

    @ManyToOne
    private Corrigendum corrigendum;

    public Corrigendum getCorrigendum() {
        return corrigendum;
    }

    public void setCorrigendum(Corrigendum corrigendum) {
        this.corrigendum = corrigendum;
    }

}
