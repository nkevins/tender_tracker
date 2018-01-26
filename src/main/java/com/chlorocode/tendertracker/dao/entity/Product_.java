package com.chlorocode.tendertracker.dao.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * This class is used to create specification cirteria for query.
 */
@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Product.class)
public abstract class Product_ {
    public static volatile SingularAttribute<Product, String> title;
    public static volatile SingularAttribute<Product, String> description;
    public static volatile SingularAttribute<Product, Company> company;
    public static volatile SingularAttribute<Product, Boolean> publish;
}
