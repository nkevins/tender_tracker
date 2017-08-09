package com.chlorocode.tendertracker.dao.entity;

import javax.persistence.*;

@Entity
public class CorrigendumDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne
    private Corrigendum corrigendum;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="document_id", unique= true, nullable=false)
    private  Document document;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Corrigendum getCorrigendum() {
        return corrigendum;
    }

    public void setCorrigendum(Corrigendum corrigendum) {
        this.corrigendum = corrigendum;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }
}
