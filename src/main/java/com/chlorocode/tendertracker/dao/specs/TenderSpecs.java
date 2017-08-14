package com.chlorocode.tendertracker.dao.specs;

import com.chlorocode.tendertracker.dao.entity.Company_;
import com.chlorocode.tendertracker.dao.entity.Tender;
import com.chlorocode.tendertracker.dao.entity.TenderCategory_;
import com.chlorocode.tendertracker.dao.entity.Tender_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class TenderSpecs {

    private TenderSpecs() {}

    public static Specification<Tender> byTenderSearchCriteria(String title, String companyName, int tcId, int status) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
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
