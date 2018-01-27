package com.chlorocode.tendertracker.dao.specs;

import com.chlorocode.tendertracker.dao.entity.ExternalTender;
import com.chlorocode.tendertracker.dao.entity.ExternalTender_;
import com.chlorocode.tendertracker.utils.DateUtility;
import com.chlorocode.tendertracker.utils.TTCommonUtil;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kyaw Min Thu.
 * ExternalTenderSpecs is used for create condition for external tender search screen.
 */
public class ExternalTenderSpecs {

    private ExternalTenderSpecs() {}

    /**
     * This method is used to create the query for get all open tender.
     *
     * @return Specification
     */
    public static Specification<ExternalTender> getAllOpenTender() {
        return (root, query, cb) -> {
            return cb.lessThanOrEqualTo(root.get(ExternalTender_.publishedDate), DateUtility.getCurrentDateTime());
        };
    }

    /**
     * This method used to create the query for searching external tender by free text.
     *
     * @param searchString search free text
     * @return Specification
     */
    public static Specification<ExternalTender> byTenderSearchString(String searchString) {
        return (root, query, cb) -> {
            Predicate openDateFilter = cb.lessThanOrEqualTo(root.get(ExternalTender_.publishedDate), DateUtility.getCurrentDateTime());
            List<Predicate> predicates = new ArrayList<>();
            if (searchString != null && !searchString.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.<String>get(ExternalTender_.title)), TTCommonUtil.getContainsLikePattern(searchString)));
                predicates.add(cb.like(cb.lower(root.<String>get(ExternalTender_.companyName)), TTCommonUtil.getContainsLikePattern(searchString)));
            }

            return cb.and(openDateFilter, cb.or(predicates.toArray(new Predicate[predicates.size()])));
        };
    }

    /**
     * This method used to create the query for external tender advanced search.
     *
     * @param title title of tender
     * @param companyName name of company
     * @param status status of tender
     * @param tenderSource source of external tender
     * @param refNo reference number of tender
     * @return Specification
     */
    public static Specification<ExternalTender> byTenderSearchCriteria(String title, String companyName, String status
            , int tenderSource, String refNo) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.lessThanOrEqualTo(root.get(ExternalTender_.publishedDate), DateUtility.getCurrentDateTime()));
            if (title != null && !title.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.<String>get(ExternalTender_.title)), TTCommonUtil.getContainsLikePattern(title)));
            }
            if (companyName != null && !companyName.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.<String>get(ExternalTender_.companyName)), TTCommonUtil.getContainsLikePattern(companyName)));
            }
            if (status != null && !status.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.<String>get(ExternalTender_.status)), TTCommonUtil.getContainsLikePattern(status)));
            }
            if (tenderSource > 0) {
                predicates.add(cb.equal(root.<Integer>get(ExternalTender_.tenderSource), tenderSource));
            }
            if(refNo != null && !refNo.isEmpty()){
                predicates.add(cb.like(cb.lower(root.<String>get(ExternalTender_.referenceNo)), TTCommonUtil.getContainsLikePattern(refNo)));
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
