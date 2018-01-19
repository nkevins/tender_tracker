package com.chlorocode.tendertracker.dao.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Product.class)
public abstract class Product_ {
    public static volatile SingularAttribute<Product, String> title;
    public static volatile SingularAttribute<Product, String> description;
    public static volatile SingularAttribute<Product, Company> company;
}
