package com.chlorocode.tendertracker.dao.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by andy on 16/8/2017.
 */
@Entity
@Table(name = "uen_entity")
public class UenEntity {

    @Id
    private String uen;

    @Column(name = "issuance_agency_id")
    private String issueAgencyId;

    @Column(name = "uen_status")
    private String uenStatus;

    @Column(name = "entity_name")
    private String entityName;

    @Column(name = "entity_type")
    private String entityType;

    @Column(name = "uen_issue_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date uenIssueDate;

    @Column(name = "reg_street_name")
    private String regStreetName;

    @Column(name = "reg_postal_code")
    private String regPostalCode;

}
