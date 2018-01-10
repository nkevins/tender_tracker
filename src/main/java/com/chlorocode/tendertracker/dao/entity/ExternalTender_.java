package com.chlorocode.tendertracker.dao.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.util.Date;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(ExternalTender.class)
public abstract class ExternalTender_ {
    public static volatile SingularAttribute<ExternalTender, String> title;
    public static volatile SingularAttribute<ExternalTender, String> referenceNo;
    public static volatile SingularAttribute<ExternalTender, String> status;
    public static volatile SingularAttribute<ExternalTender, Integer> tenderSource;
    public static volatile SingularAttribute<ExternalTender, String> companyName;
    public static volatile SingularAttribute<ExternalTender, String> description;
//    public static volatile SingularAttribute<Tender, TenderCategory> tenderCategory;
    public static volatile SingularAttribute<ExternalTender, Date> publishedDate;
}
