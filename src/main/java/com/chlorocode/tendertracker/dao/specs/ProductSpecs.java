package com.chlorocode.tendertracker.dao.specs;

import com.chlorocode.tendertracker.dao.entity.Company_;
import com.chlorocode.tendertracker.dao.entity.Product;
import com.chlorocode.tendertracker.dao.entity.Product_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class ProductSpecs {

    public static Specification<Product> getAll() {
        return ((Root<Product> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get(Product_.publish), true);
        });
    }

    public static Specification<Product> byProductSearchString(String searchString) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (searchString != null && !searchString.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get(Product_.title)), getContainsLikePattern(searchString)));
                predicates.add(cb.like(cb.lower(root.get(Product_.description)), getContainsLikePattern(searchString)));
                predicates.add(cb.like(cb.lower(root.get(Product_.company).get(Company_.name)), getContainsLikePattern(searchString)));
            }

            return  cb.or(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    public static Specification<Product> byProductSearchCriteria(String title, String companyName) {
        return Specifications.where(getAll()).and(searchProductByTitleAndCompanyName(title, companyName));
    }

    public static Specification<Product> searchProductByTitleAndCompanyName(String title, String companyName) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (title != null && !title.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get(Product_.title)), getContainsLikePattern(title)));
            }

            if (companyName != null && !companyName.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get(Product_.company).get(Company_.name)), getContainsLikePattern(companyName)));
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    public static Specification<Product> isTitleLike(String title) {
        return (root, query, cb) -> {
            String containsLikePattern = getContainsLikePattern(title);
            return cb.like(cb.lower(root.<String>get(Product_.title)), containsLikePattern);
        };
    }

    private static String getContainsLikePattern(String searchTerm) {
        if (searchTerm == null || searchTerm.isEmpty()) {
            return "%";
        } else {
            return "%" + searchTerm.toLowerCase() + "%";
        }
    }
}
