package com.chlorocode.tendertracker.dao.specs;

import com.chlorocode.tendertracker.dao.entity.Company_;
import com.chlorocode.tendertracker.dao.entity.Product;
import com.chlorocode.tendertracker.dao.entity.Product_;
import com.chlorocode.tendertracker.utils.TTCommonUtil;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zaw Min Thant.
 * ProductSpecs is used for create condition for product search screen.
 */
public class ProductSpecs {

    /**
     * This method is used to create the query for get all product.
     *
     * @return Specification
     */
    public static Specification<Product> getAll() {
        return ((Root<Product> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) -> {
            return criteriaBuilder.and(
                    criteriaBuilder.equal(root.get(Product_.publish), true),
                    criteriaBuilder.equal(root.get("status"), 0)
            );
        });
    }

    /**
     * This method is used to create the query for find product by search string.
     *
     * @param searchString free search text
     * @return Specification
     */
    public static Specification<Product> byProductSearchString(String searchString) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (searchString != null && !searchString.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get(Product_.title)), TTCommonUtil.getContainsLikePattern(searchString)));
                predicates.add(cb.like(cb.lower(root.get(Product_.description)), TTCommonUtil.getContainsLikePattern(searchString)));
                predicates.add(cb.like(cb.lower(root.get(Product_.company).get(Company_.name)), TTCommonUtil.getContainsLikePattern(searchString)));
            }

            return  cb.or(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    /**
     * This method is used to create the query for search product by search criteria.
     *
     * @param title title of the product
     * @param companyName name of the company
     * @return Specification
     */
    public static Specification<Product> byProductSearchCriteria(String title, String companyName) {
        return Specifications.where(getAll()).and(searchProductByTitleAndCompanyName(title, companyName));
    }

    /**
     * This method is used to create the query for search product by title and company name.
     *
     * @param title title of the product
     * @param companyName name of the company
     * @return Specification
     */
    public static Specification<Product> searchProductByTitleAndCompanyName(String title, String companyName) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (title != null && !title.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get(Product_.title)), TTCommonUtil.getContainsLikePattern(title)));
            }

            if (companyName != null && !companyName.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get(Product_.company).get(Company_.name)), TTCommonUtil.getContainsLikePattern(companyName)));
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
