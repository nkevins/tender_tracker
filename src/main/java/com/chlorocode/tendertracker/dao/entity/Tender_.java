package com.chlorocode.tendertracker.dao.entity;

import com.chlorocode.tendertracker.dao.entity.Company;
import com.chlorocode.tendertracker.dao.entity.Tender;
import com.chlorocode.tendertracker.dao.entity.TenderCategory;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Tender.class)
public abstract class Tender_ {
    public static volatile SingularAttribute<Tender, String> title;
    public static volatile SingularAttribute<Tender, String> refNo;
    public static volatile SingularAttribute<Tender, Integer> status;
    public static volatile SingularAttribute<Tender, Company> company;
    public static volatile SingularAttribute<Tender, TenderCategory> tenderCategory;
}
