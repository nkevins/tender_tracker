package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.constants.TTConstants;
import com.chlorocode.tendertracker.dao.*;
import com.chlorocode.tendertracker.dao.dto.TenderSearchDTO;
import com.chlorocode.tendertracker.dao.entity.*;
import com.chlorocode.tendertracker.dao.specs.TenderSpecs;
import com.chlorocode.tendertracker.exception.ApplicationException;
import com.chlorocode.tendertracker.service.notification.NotificationService;
import com.chlorocode.tendertracker.service.notification.NotificationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;

/**
 * Service implementation of TenderService.
 */
@Service
public class TenderServiceImpl implements TenderService {

    private TenderDAO tenderDAO;
    private TenderDocumentDAO tenderDocumentDAO;
    private TenderCategorySubscriptionDAO tenderCategorySubscriptionDAO;
    private S3Wrapper s3Wrapper;
    private TenderBookmarkDAO tenderBookmarkDAO;
    private TenderPagingDAO tenderPagingDAO;
    private NotificationService notificationService;
    private IPGeoLocationService ipGeoLocationService;
    private TenderVisitDAO tenderVisitDAO;
    private TenderAwardDAO tenderAwardDAO;
    private UserService userService;
    private BidService bidService;
    private UserRoleService userRoleService;
    private TenderSubscriptionService tenderSubscriptionService;

    /**
     * Constructor.
     *
     * @param tenderDAO TenderDAO
     * @param s3Wrapper S3Wrapper
     * @param tenderBookmarkDAO TenderBookmarkDAO
     * @param tenderDocumentDAO TenderDocumentDAO
     * @param tenderCategorySubscriptionDAO TenderCategorySubscriptionDAO
     * @param tenderPagingDAO TenderPagingDAO
     * @param notificationService NotificationService
     * @param ipGeoLocationService IPGeoLocationService
     * @param tenderVisitDAO TenderVisitDAO
     * @param tenderAwardDAO TenderAwardDAO
     * @param userService UserService
     * @param bidService BidService
     * @param userRoleService UserRoleService
     * @param tenderSubscriptionService TenderSubscriptionService
     */
    @Autowired
    public TenderServiceImpl(TenderDAO tenderDAO, S3Wrapper s3Wrapper, TenderBookmarkDAO tenderBookmarkDAO
                            , TenderDocumentDAO tenderDocumentDAO, TenderSubscriptionService tenderSubscriptionService
                            , TenderCategorySubscriptionDAO tenderCategorySubscriptionDAO, TenderPagingDAO tenderPagingDAO
                            , NotificationService notificationService, IPGeoLocationService ipGeoLocationService
                            , TenderVisitDAO tenderVisitDAO, TenderAwardDAO tenderAwardDAO, UserService userService
                            , BidService bidService, UserRoleService userRoleService) {
        this.tenderDAO = tenderDAO;
        this.tenderDocumentDAO = tenderDocumentDAO;
        this.tenderSubscriptionService=tenderSubscriptionService;
        this.tenderCategorySubscriptionDAO = tenderCategorySubscriptionDAO;
        this.s3Wrapper = s3Wrapper;
        this.tenderBookmarkDAO = tenderBookmarkDAO;
        this.tenderPagingDAO = tenderPagingDAO;
        this.notificationService = notificationService;
        this.ipGeoLocationService = ipGeoLocationService;
        this.tenderVisitDAO = tenderVisitDAO;
        this.tenderAwardDAO = tenderAwardDAO;
        this.userService = userService;
        this.bidService = bidService;
        this.userRoleService=userRoleService;
    }

    @Override
    public Tender findById(int id) {
        return tenderDAO.findOne(id);
    }

    @Override
    public List<Tender> findTender() {
        List<Tender> tenders = new LinkedList<>();
        Iterable<Tender> iterable = tenderDAO.findAll();
        iterable.forEach(tenders::add);

        return tenders;
    }

    @Override
    @Transactional
    public Tender createTender(Tender t, List<MultipartFile> attachments) {
        if (t.getOpenDate().after(t.getClosedDate())) {
            throw new ApplicationException("Tender Closing Date must be later than Tender Opening Date");
        }

        if (t.getDeliveryDate() != null && t.getDeliveryDate().before(t.getClosedDate())) {
            throw new ApplicationException("Tender Delivery Date must be before Tender Closing Date");
        }

        if (t.getItems() == null || t.getItems().size() == 0) {
            throw new ApplicationException("At least one Tender Item must be provided");
        }

        if (t.getTenderType() == 2 && t.getInvitedCompanies().size() == 0) {
            throw new ApplicationException("For Closed Tender, please provide at least one company to be invited");
        }

        for (TenderItem ti : t.getItems()) {
            if (ti.getQuantity() < 0) {
                throw new ApplicationException("Tender Item Quantity must be greater than 0");
            }
        }

        // Set tender status to OPEN
        t.setStatus(1);

        Tender result = tenderDAO.save(t);

        for (MultipartFile f : attachments) {
            // Upload to AWS S3
            String bucketPath = "tender_documents/" + t.getId() + "/" + f.getOriginalFilename();
            try {
                s3Wrapper.upload(f.getInputStream(), bucketPath);
            } catch (IOException ex) {
                throw new ApplicationException("Upload failed " + ex.getMessage());
            }

            // Save to DB
            TenderDocument doc = new TenderDocument();
            doc.setName(f.getOriginalFilename());
            doc.setLocation(bucketPath);
            doc.setCreatedBy(t.getCreatedBy());
            doc.setCreatedDate(new Date());
            doc.setLastUpdatedBy(t.getLastUpdatedBy());
            doc.setLastUpdatedDate(new Date());
            t.addTenderDocument(doc);
            tenderDocumentDAO.save(doc);
        }

        if (result != null) {
            String[] emails = getSubscriptionEmails(result.getTenderCategory().getId());
            if (emails != null && emails.length > 0) {
                Map<String, Object> params = new HashMap<>();
                params.put(TTConstants.PARAM_APPROVAL_ACTION, TTConstants.REJECTED);
                params.put(TTConstants.PARAM_TENDER_ID, result.getId());
                params.put(TTConstants.PARAM_TENDER_TITLE, result.getTitle());
                params.put(TTConstants.PARAM_EMAILS, emails);
                notificationService.sendNotification(NotificationServiceImpl.NOTI_MODE.tender_create_noti, params);
            }
        }

        return result;
    }

    private String[] getSubscriptionEmails(int tenderCategoryId) {
        List<TenderCategorySubscription> subscriptions = tenderCategorySubscriptionDAO.findUserSubscriptionByTenderCategory(tenderCategoryId);
        if (subscriptions != null) {
            Set<String> emails = new HashSet<>();
            for (TenderCategorySubscription subscription : subscriptions) {
                if (subscription.getUser() != null) {
                    emails.add(subscription.getUser().getEmail());
                }
            }
            return emails.toArray(new String[subscriptions.size()]);
        } else {
            return null;
        }
    }

    @Override
    public Tender updateTender(Tender tender) {
        if (tender.getOpenDate().after(tender.getClosedDate())) {
            throw new ApplicationException("Tender Closing Date must be later than Tender Opening Date");
        }

        if (tender.getDeliveryDate() != null && tender.getDeliveryDate().before(tender.getClosedDate())) {
            throw new ApplicationException("Tender Delivery Date must be before Tender Closing Date");
        }

        tender = tenderDAO.save(tender);
        tenderSubscriptionService.sendBookmarkNoti(tender, TTConstants.UPDATE_TENDER);
        return tender;
    }

    @Override
    @Transactional
    public TenderDocument addTenderDocument(MultipartFile attachment, Tender tender, int createdBy) {
        // Upload to AWS S3
        String bucketPath = "tender_documents/" + tender.getId() + "/" + attachment.getOriginalFilename();
        try {
            s3Wrapper.upload(attachment.getInputStream(), bucketPath);
        } catch (IOException ex) {
            throw new ApplicationException("Upload failed " + ex.getMessage());
        }

        // Save to DB
        TenderDocument doc = new TenderDocument();
        doc.setName(attachment.getOriginalFilename());
        doc.setLocation(bucketPath);
        doc.setCreatedBy(createdBy);
        doc.setCreatedDate(new Date());
        doc.setLastUpdatedBy(createdBy);
        doc.setLastUpdatedDate(new Date());
        doc.setTender(tender);
        return tenderDocumentDAO.save(doc);
    }

    @Override
    @Transactional
    public void removeTenderDocument(int id) {
        TenderDocument tenderDocument = tenderDocumentDAO.findOne(id);

        // Remove from S3
        String bucketPath = "tender_documents/" + tenderDocument.getTender().getId() + "/" + tenderDocument.getName();
        s3Wrapper.deleteObject(bucketPath);

        tenderDocument.setTender(null);
        tenderDocumentDAO.delete(tenderDocument);
    }

    @Override
    public Page<Tender> listAllByPage(Pageable pageable) {
        int companyId = getCompanyId();
        Specification<Tender> searchSpec = TenderSpecs.getAllOpenTender(companyId, getInviteTenderIds(companyId));
        return tenderPagingDAO.findAll(searchSpec, pageable);
    }

    @Override
    public Page<Tender> searchTender(TenderSearchDTO searchDTO, Pageable pageable) {
        int companyId = getCompanyId();
        Specification<Tender> searchSpec;
        if (searchDTO.getSearchText() != null && !searchDTO.getSearchText().trim().isEmpty()) {
            searchSpec = TenderSpecs.byTenderSearchString(searchDTO.getSearchText().trim(), companyId, getInviteTenderIds(companyId));
            searchDTO.setCompanyName(null);
            searchDTO.setTitle(null);
            searchDTO.setRefNo(null);
            searchDTO.setStatus(0);
            searchDTO.setTenderCategory(0);
        } else {
            searchSpec = TenderSpecs.byTenderSearchCriteria(
                    searchDTO.getTitle() == null ? null : searchDTO.getTitle().trim()
                    , searchDTO.getCompanyName() == null ? null : searchDTO.getCompanyName().trim()
                    , searchDTO.getTenderCategory()
                    , searchDTO.getStatus(), searchDTO.getRefNo()
                    , companyId, getInviteTenderIds(companyId));
            searchDTO.setSearchText(null);
        }
        return tenderPagingDAO.findAll(searchSpec, pageable);
    }

    @Override
    @Async
    public void logVisit(Tender tender, String ipAddress) {
        TenderVisit visit = ipGeoLocationService.getIPDetails(ipAddress);
        if (visit != null) {
            visit.setTender(tender);
            tenderVisitDAO.save(visit);
        }
    }

    @Override
    @Transactional
    public void awardTender(TenderAward tenderAward) {
        tenderAwardDAO.save(tenderAward);
        Tender tender = tenderAward.getTender();
        tender.setStatus(3);
        tenderDAO.save(tender);

        // Add awarded company user.
        Set<String> emails = userRoleService.findCompanyUserEmails(tenderAward.getCompany().getId());
        if (emails == null) {
            emails = new HashSet<>();
        }
        // Add bid user.
        List<Bid> bids = bidService.findBidByTender(tender.getId());
        if (bids != null) {
            for(Bid bid : bids) {
                User user = userService.findById(bid.getCreatedBy());
                if (user != null) {
                    emails.add(user.getEmail());
                }
            }
        }
        // Add bookmark user.
        List<TenderBookmark>  tenderBookmarks = tenderBookmarkDAO.findTenderBookmarkByTender(tender.getId());
        if (tenderBookmarks != null) {
            for (TenderBookmark bookmark : tenderBookmarks) {
                if (bookmark.getUser() != null) emails.add(bookmark.getUser().getEmail());
            }
        }

        if (emails.size() > 0) {
            Map<String, Object> params = new HashMap<>();
            params.put(TTConstants.PARAM_TENDER_ID, tenderAward.getTender().getId());
            params.put(TTConstants.PARAM_TENDER_TITLE, tenderAward.getTender().getTitle());
            params.put(TTConstants.PARAM_COMPANY_NAME, tenderAward.getCompany().getName());
            params.put(TTConstants.PARAM_EMAILS, emails.toArray(new String[emails.size()]));
            notificationService.sendNotification(NotificationServiceImpl.NOTI_MODE.tender_award_noti, params);
        }
    }

    private List<Integer> getInviteTenderIds(int companyId) {
        if (companyId > 0) {
            List<Tender> tenders = tenderDAO.findTenderByInvitedCompany(companyId);
            if (tenders != null && tenders.size() > 0) {
                List<Integer> tenderIds = new ArrayList<>();
                for (Tender tender : tenders) {
                    if (tender != null) {
                        tenderIds.add(tender.getId());
                    }
                }
                return tenderIds;
            }
        }
        return null;
    }

    private int getCompanyId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal != null && principal instanceof CurrentUser) {
            CurrentUser usr = (CurrentUser) principal;
            if (usr.getSelectedCompany() != null) {
                return usr.getSelectedCompany().getId();
            }
        }
        return 0;
    }
}
