package com.chlorocode.tendertracker.dao.specs;

import com.chlorocode.tendertracker.dao.entity.Company_;
import com.chlorocode.tendertracker.dao.entity.Tender;
import com.chlorocode.tendertracker.dao.entity.TenderCategory_;
import com.chlorocode.tendertracker.dao.entity.Tender_;
import com.chlorocode.tendertracker.utils.DateUtility;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class TenderSpecs {

    private TenderSpecs() {}

    public static Specification<Tender> getAllOpenTender(int companyId, List<Integer> tenderIds) {
        return (root, query, cb) -> {
//            return cb.greaterThanOrEqualTo(root.get(Tender_.openDate), DateUtility.getCurrentDate());
            List<Predicate> predicates = getDefaultCriteriaQuery(root, cb, companyId, tenderIds);
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    public static Specification<Tender> byTenderSearchString(String searchString, int companyId, List<Integer> tenderIds) {
        return (root, query, cb) -> {
//            Predicate openDateFilter = cb.greaterThanOrEqualTo(root.get(Tender_.openDate), DateUtility.getCurrentDate());
            List<Predicate> predicates = getDefaultCriteriaQuery(root, cb, companyId, tenderIds);
            if (searchString != null && !searchString.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.<String>get(Tender_.title)), getContainsLikePattern(searchString)));
                predicates.add(cb.like(cb.lower(root.get(Tender_.company).get(Company_.name)), getContainsLikePattern(searchString)));
                predicates.add(cb.like(cb.lower(root.<String>get(Tender_.description)), getContainsLikePattern(searchString)));
            }

//            return cb.and(openDateFilter, cb.or(predicates.toArray(new Predicate[predicates.size()])));
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    public static Specification<Tender> byTenderSearchCriteria(String title, String companyName, int tcId, int status
            , String refNo, int companyId, List<Integer> tenderIds) {
        return (root, query, cb) -> {
            List<Predicate> predicates = getDefaultCriteriaQuery(root, cb, companyId, tenderIds);

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

    private static List<Predicate> getDefaultCriteriaQuery(Root<Tender> root, CriteriaBuilder cb, int companyId, List<Integer> tenderIds) {
        List<Predicate> predicates = new ArrayList<>();
        List<Predicate> innerPredicates = new ArrayList<>();

        // 1. Add condition to show all of his company tenders.
        if (companyId > 0) {
            innerPredicates.add(cb.equal(root.get(Tender_.company).<Integer>get(Company_.id), companyId));
        }

        // 2. Add condition to show all of his bid tenders.
        if (tenderIds != null && tenderIds.size() > 0) {
            CriteriaBuilder.In<Integer> inTenderIds = cb.in(root.get(Tender_.id));
            for (Integer id : tenderIds) {
                inTenderIds.value(id);
            }
            innerPredicates.add(inTenderIds);
        }

        // 3. Add condition to show all open tenders.
        innerPredicates.add(cb.greaterThanOrEqualTo(root.get(Tender_.openDate), DateUtility.getCurrentDate()));

        // or(1,2,3)
        predicates.add(cb.or(innerPredicates.toArray(new Predicate[innerPredicates.size()])));

        return predicates;
    }

//    private static List<Predicate> createQuery(Root<Tender> root, CriteriaBuilder cb, int companyId, List<Integer> tenderIds, List<Predicate> subPredicates) {
//        List<Predicate> mainPredicates = new ArrayList<>();
//
//        Predicate predicatesOpenDate = (cb.greaterThanOrEqualTo(root.get(Tender_.openDate), DateUtility.getCurrentDate()));
//
//        if (companyId > 0) {
//            innerPredicates.add(cb.equal(root.get(Tender_.company).<Integer>get(Company_.id), companyId));
//        }
//        if (tenderIds != null && tenderIds.size() > 0) {
//            CriteriaBuilder.In<Integer> inTenderIds = cb.in(root.get(Tender_.id));
//            for (Integer id : tenderIds) {
//                inTenderIds.value(id);
//            }
//            innerPredicates.add(inTenderIds);
//        }
//        predicates.add(cb.or(innerPredicates.toArray(new Predicate[innerPredicates.size()])));
//
//        return predicates;
//    }

//    public static Specification<Tender> isTitleLike(String title) {
//        return (root, query, cb) -> {
//            String containsLikePattern = getContainsLikePattern(title);
//            return cb.like(cb.lower(root.<String>get(Tender_.title)), containsLikePattern);
//        };
//    }
//
//    public static Specification<Tender> isCompanyLike(String company) {
//        return (root, query, cb) -> {
//            String containsLikePattern = getContainsLikePattern(company);
//            return cb.like(cb.lower(root.get(Tender_.company).get(Company_.name)), containsLikePattern);
//        };
//    }
//
//    public static Specification<Tender> isEqualTenderCategory(int tcId) {
//        return (root, query, cb) -> {
//            return cb.equal(root.get(Tender_.tenderCategory).get(TenderCategory_.id), tcId);
//        };
//    }
//
//    public static Specification<Tender> isEqualStatus(int status) {
//        return (root, query, cb) -> {
//            return cb.equal(root.<Integer>get(Tender_.status), status);
//        };
//    }

    private static String getContainsLikePattern(String searchTerm) {
        if (searchTerm == null || searchTerm.isEmpty()) {
            return "%";
        }
        else {
            return "%" + searchTerm.toLowerCase() + "%";
        }
    }
}
