package com.chlorocode.tendertracker.dao.specs;

import com.chlorocode.tendertracker.dao.entity.*;
import com.chlorocode.tendertracker.utils.DateUtility;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class ExternalTenderSpecs {

    private ExternalTenderSpecs() {}

    public static Specification<ExternalTender> getAllOpenTender() {
        return (root, query, cb) -> {
            return cb.lessThanOrEqualTo(root.get(ExternalTender_.publishedDate), DateUtility.getCurrentDate());
        };
    }

    public static Specification<ExternalTender> byTenderSearchString(String searchString) {
        return (root, query, cb) -> {
            Predicate openDateFilter = cb.lessThanOrEqualTo(root.get(ExternalTender_.publishedDate), DateUtility.getCurrentDate());
            List<Predicate> predicates = new ArrayList<>();
            if (searchString != null && !searchString.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.<String>get(ExternalTender_.title)), getContainsLikePattern(searchString)));
                predicates.add(cb.like(cb.lower(root.<String>get(ExternalTender_.companyName)), getContainsLikePattern(searchString)));
            }

            return cb.and(openDateFilter, cb.or(predicates.toArray(new Predicate[predicates.size()])));
        };
    }

    public static Specification<ExternalTender> byTenderSearchCriteria(String title, String companyName, String status
            , int tenderSource, String refNo) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.lessThanOrEqualTo(root.get(ExternalTender_.publishedDate), DateUtility.getCurrentDate()));
            if (title != null && !title.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.<String>get(ExternalTender_.title)), getContainsLikePattern(title)));
            }
            if (companyName != null && !companyName.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.<String>get(ExternalTender_.companyName)), getContainsLikePattern(companyName)));
            }
            if (status != null && !status.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.<String>get(ExternalTender_.status)), getContainsLikePattern(status)));
            }
            if (tenderSource > 0) {
                predicates.add(cb.equal(root.<Integer>get(ExternalTender_.tenderSource), tenderSource));
            }
            if(refNo != null && !refNo.isEmpty()){
                predicates.add(cb.like(cb.lower(root.<String>get(ExternalTender_.referenceNo)), getContainsLikePattern(refNo)));
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    public static Specification<ExternalTender> isTitleLike(String title) {
        return (root, query, cb) -> {
            String containsLikePattern = getContainsLikePattern(title);
            return cb.like(cb.lower(root.<String>get(ExternalTender_.title)), containsLikePattern);
        };
    }

    public static Specification<ExternalTender> isCompanyLike(String companyName) {
        return (root, query, cb) -> {
            String containsLikePattern = getContainsLikePattern(companyName);
            return cb.like(cb.lower(root.<String>get(ExternalTender_.companyName)), containsLikePattern);
        };
    }

    public static Specification<ExternalTender> isEqualStatus(String status) {
        return (root, query, cb) -> {
            return cb.like(cb.lower(root.<String>get(ExternalTender_.status)), status);
        };
    }

    public static Specification<ExternalTender> isEqualTenderSource(int tenderSource) {
        return (root, query, cb) -> {
            return cb.equal(root.<Integer>get(ExternalTender_.tenderSource), tenderSource);
        };
    }

    private static String getContainsLikePattern(String searchTerm) {
        if (searchTerm == null || searchTerm.isEmpty()) {
            return "%";
        }
        else {
            return "%" + searchTerm.toLowerCase() + "%";
        }
    }
}
