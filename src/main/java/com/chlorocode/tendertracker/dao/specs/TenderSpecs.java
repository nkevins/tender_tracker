package com.chlorocode.tendertracker.dao.specs;

import com.chlorocode.tendertracker.dao.entity.Company_;
import com.chlorocode.tendertracker.dao.entity.Tender;
import com.chlorocode.tendertracker.dao.entity.TenderCategory_;
import com.chlorocode.tendertracker.dao.entity.Tender_;
import com.chlorocode.tendertracker.utils.DateUtility;
import com.chlorocode.tendertracker.utils.TTCommonUtil;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kyaw Min Thu.
 * TenderSpecs is used for create condition for tender search screen.
 */
public class TenderSpecs {

    private TenderSpecs() {}

    /**
     * This method is used to create the query for home page landing users.
     * If user is not login user, show opened and open type tenders.
     * If user is login user. Show opened and his company or invited tender or open type tenders.
     *
     * @param companyId unique identifier of the tender of login user
     * @param tenderIds tender id of all invited tenders of login user
     * @return Specification
     */
    public static Specification<Tender> getAllOpenTender(int companyId, List<Integer> tenderIds) {
        if (companyId == 0) {
            return getDefaultCriteriaSpec();
        } else {
            return Specifications.where(getOpenDateCriteria()).and(getLoginUserCriteria(companyId, tenderIds));
        }
    }

    /**
     * This method is used to create the default criteria of not login user.(Show opened and open type tenders.)
     *
     * @return Specification
     */
    public static Specification<Tender> getDefaultCriteriaSpec() {
        return Specifications.where(getOpenDateCriteria()).and(getOpenTenderTypeCriteria());
    }

    /**
     * This method used to create the query for searching tender by free text.
     * If user is not login user, companyId will be 0 and tenderIds will be null.
     *
     * @param searchString search free text
     * @param companyId unique identifier of the tender of login user
     * @param tenderIds tender id of all invited tenders of login user
     * @return Specification
     */
    public static Specification<Tender> byTenderSearchString(String searchString, int companyId, List<Integer> tenderIds) {
        return Specifications.where(getAllOpenTender(companyId, tenderIds)).and(byTenderSearchString(searchString));
    }

    /**
     * This method used to create criteria for finding tender by free text.
     * User's searchString will be find in tender title, company name and tender description.
     *
     * @param searchString search free text
     * @return Specification
     */
    public static Specification<Tender> byTenderSearchString(String searchString) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (searchString != null && !searchString.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.<String>get(Tender_.title)), TTCommonUtil.getContainsLikePattern(searchString)));
                predicates.add(cb.like(cb.lower(root.get(Tender_.company).get(Company_.name)), TTCommonUtil.getContainsLikePattern(searchString)));
                predicates.add(cb.like(cb.lower(root.<String>get(Tender_.description)), TTCommonUtil.getContainsLikePattern(searchString)));
            }

            return cb.or(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    /**
     * This method used to create the query for advanced search.
     * If user is not login user, companyId will be 0 and tenderIds will be null.
     *
     * @param title title of tender
     * @param companyName name of company
     * @param tcId category of tender
     * @param status status of tender
     * @param refNo reference number of tender
     * @param companyId unique identifier of the tender of login user
     * @param tenderIds tender id of all invited tenders of login user
     * @return Specification
     */
    public static Specification<Tender> byTenderSearchCriteria(String title, String companyName, int tcId, int status
            , String refNo, int companyId, List<Integer> tenderIds) {
        return Specifications.where(getAllOpenTender(companyId, tenderIds))
                .and(byTenderSearchCriteria(title, companyName, tcId, status, refNo));
    }

    /**
     * This method used to create criteria for advance search.
     * User's searchString will be find in tender title, company name and tender description.
     *
     * @param title title of tender
     * @param companyName name of company
     * @param tcId category of tender
     * @param status status of tender
     * @param refNo reference number of tender
     * @return Specification
     */
    public static Specification<Tender> byTenderSearchCriteria(String title, String companyName, int tcId, int status, String refNo) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (title != null && !title.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.<String>get(Tender_.title)), TTCommonUtil.getContainsLikePattern(title)));
            }
            if (companyName != null && !companyName.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get(Tender_.company).get(Company_.name)), TTCommonUtil.getContainsLikePattern(companyName)));
            }
            if (tcId > 0) {
                predicates.add(cb.equal(root.get(Tender_.tenderCategory).get(TenderCategory_.id), tcId));
            }
            if (status > 0) {
                predicates.add(cb.equal(root.<Integer>get(Tender_.status), status));
            }

            // Added by Andy.
            if(refNo != null && !refNo.isEmpty()){
                predicates.add(cb.like(cb.lower(root.<String>get(Tender_.refNo)), TTCommonUtil.getContainsLikePattern(refNo)));
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    /**
     * This method is used to create criteria for login user.
     * Login user should able to see all of his opened tender and other eligible tender(Opened and open type tenders)
     *
     * @param companyId unique identifier of the tender of login user
     * @param tenderIds tender id of all invited tenders of login user
     * @return
     */
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

            // 3. Show open tender type.
            predicates.add(cb.equal(root.<Integer>get(Tender_.tenderType), 1));
            // or(1,2,3)
            return cb.or(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    /**
     * This method is used to create criteria for getting all opened tender.
     *
     * @return Specification
     */
    private static Specification<Tender> getOpenDateCriteria() {
        return (root, query, cb) -> {
            // show openDate<current time tenders.
            return cb.lessThanOrEqualTo(root.get(Tender_.openDate), DateUtility.getCurrentDateTime());
        };
    }

    /**
     * This method is used to create for getting all open type tenders.
     *
     * @return Specification
     */
    private static Specification<Tender> getOpenTenderTypeCriteria() {
        return (root, query, cb) -> {
            // show only open tender type.
            return cb.equal(root.<Integer>get(Tender_.tenderType), 1);
        };
    }

}
