package com.chlorocode.tendertracker.dao.specs;

import com.chlorocode.tendertracker.dao.entity.*;
import com.chlorocode.tendertracker.utils.DateUtility;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class TenderSpecs {

    private TenderSpecs() {}

    public static Specification<Tender> getAllOpenTender(int companyId, List<Integer> tenderIds) {
        if (companyId == 0) {
            return getDefaultCriteriaSpec();
        } else {
            return Specifications.where(getLoginUserCriteria(companyId, tenderIds)).or(getDefaultCriteriaSpec());
        }
    }

    public static Specification<Tender> getDefaultCriteriaSpec() {
        return Specifications.where(getOpenDateCriteria()).and(getOpenTenderTypeCriteria());
    }

    public static Specification<Tender> byTenderSearchString(String searchString, int companyId, List<Integer> tenderIds) {
        return Specifications.where(getAllOpenTender(companyId, tenderIds)).and(byTenderSearchString(searchString));
    }

    public static Specification<Tender> byTenderSearchString(String searchString) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (searchString != null && !searchString.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.<String>get(Tender_.title)), getContainsLikePattern(searchString)));
                predicates.add(cb.like(cb.lower(root.get(Tender_.company).get(Company_.name)), getContainsLikePattern(searchString)));
                predicates.add(cb.like(cb.lower(root.<String>get(Tender_.description)), getContainsLikePattern(searchString)));
            }

            return cb.or(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    public static Specification<Tender> byTenderSearchCriteria(String title, String companyName, int tcId, int status
            , String refNo, int companyId, List<Integer> tenderIds) {
        return Specifications.where(getAllOpenTender(companyId, tenderIds))
                .and(byTenderSearchCriteria(title, companyName, tcId, status, refNo));
    }

    public static Specification<Tender> byTenderSearchCriteria(String title, String companyName, int tcId, int status, String refNo) {
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

            // Added by Andy.
            if(refNo != null && !refNo.isEmpty()){
                predicates.add(cb.like(cb.lower(root.<String>get(Tender_.refNo)), getContainsLikePattern(refNo)));
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    private static Specification<Tender> getLoginUserCriteria(int companyId, List<Integer> tenderIds) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Add condition to show all of his company tenders.
            if (companyId > 0) {
                predicates.add(cb.equal(root.get(Tender_.company).<Integer>get(Company_.id), companyId));
            }

            // 2. Add condition to show all of his bid tenders.
            if (tenderIds != null && tenderIds.size() > 0) {
                CriteriaBuilder.In<Integer> inTenderIds = cb.in(root.get(Tender_.id));
                for (Integer id : tenderIds) {
                    inTenderIds.value(id);
                }
                predicates.add(inTenderIds);
            }
            // or(1,2)
            return cb.or(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

//    private static Specification<Tender> getDefaultCriteriaQuery() {
//        return (root, query, cb) -> {
//            List<Predicate> predicates = new ArrayList<>();
//            // show openDate<current time tenders.
//            predicates.add(cb.lessThanOrEqualTo(root.get(Tender_.openDate), DateUtility.getCurrentDate()));
//            // show only open tender type.
//            predicates.add(cb.equal(root.<Integer>get(Tender_.tenderType), 1));
//            // or(1,2)
//            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
//        };
//    }

    private static Specification<Tender> getOpenDateCriteria() {
        return (root, query, cb) -> {
            // show openDate<current time tenders.
            return cb.lessThanOrEqualTo(root.get(Tender_.openDate), DateUtility.getCurrentDate());
        };
    }

    private static Specification<Tender> getOpenTenderTypeCriteria() {
        return (root, query, cb) -> {
            // show only open tender type.
            return cb.equal(root.<Integer>get(Tender_.tenderType), 1);
        };
    }

//    private static List<Predicate> getDefaultCriteriaQuery(Root<Tender> root, CriteriaBuilder cb, int companyId, List<Integer> tenderIds) {
//        List<Predicate> predicates = new ArrayList<>();
//        List<Predicate> innerPredicates = new ArrayList<>();
//
//        // 1. Add condition to show all of his company tenders.
//        if (companyId > 0) {
//            innerPredicates.add(cb.equal(root.get(Tender_.company).<Integer>get(Company_.id), companyId));
//        }
//
//        // 2. Add condition to show all of his bid tenders.
//        if (tenderIds != null && tenderIds.size() > 0) {
//            CriteriaBuilder.In<Integer> inTenderIds = cb.in(root.get(Tender_.id));
//            for (Integer id : tenderIds) {
//                inTenderIds.value(id);
//            }
//            innerPredicates.add(inTenderIds);
//        }
//
//        // 3. Add condition to show all open tenders.
//        List<Predicate> innerPredicates2 = new ArrayList<>();
//        innerPredicates2.add(cb.lessThanOrEqualTo(root.get(Tender_.openDate), DateUtility.getCurrentDate()));
//        innerPredicates2.add(cb.equal(root.<Integer>get(Tender_.tenderType), 1));
//
//        innerPredicates.add(cb.and(innerPredicates2.toArray(new Predicate[innerPredicates2.size()])));
//
//
//        // or(1,2,3)
//        predicates.add(cb.or(innerPredicates.toArray(new Predicate[innerPredicates.size()])));
//
//        return predicates;
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
