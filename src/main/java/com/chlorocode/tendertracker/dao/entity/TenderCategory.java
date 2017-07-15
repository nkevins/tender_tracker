package com.chlorocode.tendertracker.dao.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "tender_category")
public class TenderCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String name;

    @OneToMany(mappedBy = "tenderCategory")
    private List<Tender> tenders;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
