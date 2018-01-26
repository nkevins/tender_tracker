package com.chlorocode.tendertracker.dao.entity;

import com.chlorocode.tendertracker.dao.entity.Company;
import com.chlorocode.tendertracker.dao.entity.Tender;
import com.chlorocode.tendertracker.dao.entity.TenderCategory;

import javax.annotation.Generated;
import javax.management.openmbean.OpenDataException;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.util.Date;

/**
 * This class is used to create specification cirteria for query.
 */
@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Tender.class)
public abstract class Tender_ {
    public static volatile SingularAttribute<Tender, Integer> id;
    public static volatile SingularAttribute<Tender, String> title;
    public static volatile SingularAttribute<Tender, String> refNo;
    public static volatile SingularAttribute<Tender, Integer> status;
    public static volatile SingularAttribute<Tender, Company> company;
    public static volatile SingularAttribute<Tender, String> description;
    public static volatile SingularAttribute<Tender, TenderCategory> tenderCategory;
    public static volatile SingularAttribute<Tender, Date> openDate;
    public static volatile SingularAttribute<Tender, Integer> tenderType;
}
