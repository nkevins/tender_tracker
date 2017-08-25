package com.chlorocode.tendertracker.dao.specs;

import com.chlorocode.tendertracker.dao.entity.Company_;
import com.chlorocode.tendertracker.dao.entity.Tender;
import com.chlorocode.tendertracker.dao.entity.TenderCategory_;
import com.chlorocode.tendertracker.dao.entity.Tender_;
import com.chlorocode.tendertracker.utils.DateUtility;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class TenderSpecs {

    private TenderSpecs() {}

    public static Specification<Tender> getAllOpenTender() {
        return (root, query, cb) -> {
            return cb.lessThanOrEqualTo(root.get(Tender_.openDate), DateUtility.getCurrentDate());
        };
    }

    public static Specification<Tender> byTenderSearchString(String searchString) {
        return (root, query, cb) -> {
            Predicate openDateFilter = cb.lessThanOrEqualTo(root.get(Tender_.openDate), DateUtility.getCurrentDate());
            List<Predicate> predicates = new ArrayList<>();
            if (searchString != null && !searchString.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.<String>get(Tender_.title)), getContainsLikePattern(searchString)));
                predicates.add(cb.like(cb.lower(root.get(Tender_.company).get(Company_.name)), getContainsLikePattern(searchString)));
                predicates.add(cb.like(cb.lower(root.<String>get(Tender_.description)), getContainsLikePattern(searchString)));
            }

            return cb.and(openDateFilter, cb.or(predicates.toArray(new Predicate[predicates.size()])));
        };
    }

    public static Specification<Tender> byTenderSearchCriteria(String title, String companyName, int tcId, int status, String refNo) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.lessThanOrEqualTo(root.get(Tender_.openDate), DateUtility.getCurrentDate()));
            if (title != null && !title.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.<String>get(Tender_.title)), getContainsLikePattern(title)));
            }
            if (companyName != null && !companyName.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get(Tender_.company).get(Company_.name)), getContainsLikePattern(companyName)));
            }
            if (tcId > 0) {
                predicates.add(cb.equal(root.get(Tender_.tenderCategory).get(TenderCategory_.id), tcId));
            }
            if (status > 0) {
                predicates.add(cb.equal(root.<Integer>get(Tender_.status), status));
            }

            // Added by Andy.
            if(refNo != null && !refNo.isEmpty()){
                predicates.add(cb.like(cb.lower(root.<String>get(Tender_.refNo)), getContainsLikePattern(refNo)));
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    public static Specification<Tender> isTitleLike(String title) {
        return (root, query, cb) -> {
            String containsLikePattern = getContainsLikePattern(title);
            return cb.like(cb.lower(root.<String>get(Tender_.title)), containsLikePattern);
        };
    }

    public static Specification<Tender> isCompanyLike(String company) {
        return (root, query, cb) -> {
            String containsLikePattern = getContainsLikePattern(company);
            return cb.like(cb.lower(root.get(Tender_.company).get(Company_.name)), containsLikePattern);
        };
    }

    public static Specification<Tender> isEqualTenderCategory(int tcId) {
        return (root, query, cb) -> {
            return cb.equal(root.get(Tender_.tenderCategory).get(TenderCategory_.id), tcId);
        };
    }

    public static Specification<Tender> isEqualStatus(int status) {
        return (root, query, cb) -> {
            return cb.equal(root.<Integer>get(Tender_.status), status);
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
