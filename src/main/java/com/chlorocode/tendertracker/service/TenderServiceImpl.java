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
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;

@Service
public class TenderServiceImpl implements TenderService {

    private TenderDAO tenderDAO;
    private TenderItemDAO tenderItemDAO;
    private TenderDocumentDAO tenderDocumentDAO;
    private TenderCategorySubscriptionDAO tenderCategorySubscriptionDAO;
    private DocumentDAO documentDAO;
    private S3Wrapper s3Wrapper;
    private TenderBookmarkDAO tenderBookmarkDAO;
    private TenderPagingDAO tenderPagingDAO;
    private NotificationService notificationService;

    @Autowired
    public TenderServiceImpl(TenderDAO tenderDAO, DocumentDAO documentDAO, S3Wrapper s3Wrapper, TenderBookmarkDAO tenderBookmarkDAO
                            , TenderItemDAO tenderItemDAO, TenderDocumentDAO tenderDocumentDAO
                            , TenderCategorySubscriptionDAO tenderCategorySubscriptionDAO, TenderPagingDAO tenderPagingDAO
                            , NotificationService notificationService) {
        this.tenderDAO = tenderDAO;
        this.tenderItemDAO = tenderItemDAO;
        this.tenderDocumentDAO = tenderDocumentDAO;
        this.tenderCategorySubscriptionDAO = tenderCategorySubscriptionDAO;
        this.documentDAO = documentDAO;
        this.s3Wrapper = s3Wrapper;
        this.tenderBookmarkDAO = tenderBookmarkDAO;
        this.tenderPagingDAO = tenderPagingDAO;
        this.notificationService = notificationService;
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

        if (t.getItems() == null || t.getItems().size() == 0) {
            throw new ApplicationException("At least one Tender Item must be provided");
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
            Document doc = new Document();
            doc.setName(f.getOriginalFilename());
            doc.setLocation(bucketPath);
            doc.setType(1);
            doc.setCreatedBy(t.getCreatedBy());
            doc.setCreatedDate(new Date());
            doc.setLastUpdatedBy(t.getLastUpdatedBy());
            doc.setLastUpdatedDate(new Date());
            documentDAO.save(doc);

            TenderDocument tenderDocument = new TenderDocument();
            tenderDocument.setDocument(doc);
            t.addTenderDocument(tenderDocument);
        }

        return result;
    }

    @Override
    public Tender updateTender(Tender tender) {
        tender = tenderDAO.save(tender);
        sendEmails(tender);
        return tender;
    }

    @Override
    public TenderItem findTenderItemById(int id) {
        return tenderItemDAO.findOne(id);
    }

    @Override
    public TenderItem addTenderItem(TenderItem tenderItem) {
        return tenderItemDAO.save(tenderItem);
    }

    @Override
    public TenderItem updateTenderItem(TenderItem tenderItem) {
        tenderItem = tenderItemDAO.save(tenderItem);
        sendEmails(tenderItem.getTender());

        return tenderItem;
    }

    @Override
    public void removeTenderItem(int tenderItemId) {
        tenderItemDAO.delete(tenderItemId);
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
        Document doc = new Document();
        doc.setName(attachment.getOriginalFilename());
        doc.setLocation(bucketPath);
        doc.setType(1);
        doc.setCreatedBy(createdBy);
        doc.setCreatedDate(new Date());
        doc.setLastUpdatedBy(createdBy);
        doc.setLastUpdatedDate(new Date());
        documentDAO.save(doc);

        TenderDocument tenderDocument = new TenderDocument();
        tenderDocument.setDocument(doc);
        tenderDocument.setTender(tender);

        return tenderDocumentDAO.save(tenderDocument);
    }

    @Override
    @Transactional
    public void removeTenderDocument(int id) {
        TenderDocument tenderDocument = tenderDocumentDAO.findOne(id);
        Document document = tenderDocument.getDocument();

        // Remove from S3
        String bucketPath = "tender_documents/" + tenderDocument.getTender().getId() + "/" + document.getName();
        s3Wrapper.deleteObject(bucketPath);

        tenderDocument.setTender(null);
        tenderDocument.setDocument(null);
        tenderDocumentDAO.delete(tenderDocument);
        documentDAO.delete(document);
    }

    @Override
    public TenderBookmark findTenderBookmark(int tenderId, int userId) {
        return tenderBookmarkDAO.findTenderBookmarkByUserAndTender(tenderId, userId);
    }

    @Override
    public TenderBookmark bookmarkTender(Tender tender, User user) {
        TenderBookmark tenderBookmark = new TenderBookmark();
        tenderBookmark.setUser(user);
        tenderBookmark.setTender(tender);
        tenderBookmark.setCreatedBy(user.getId());
        tenderBookmark.setCreatedDate(new Date());
        tenderBookmark.setLastUpdatedBy(user.getId());
        tenderBookmark.setLastUpdatedDate(new Date());

        return tenderBookmarkDAO.save(tenderBookmark);
    }

    @Override
    public void removeTenderBookmark(int tenderId, int userId) {
        TenderBookmark tenderBookmark = findTenderBookmark(tenderId, userId);
        tenderBookmarkDAO.delete(tenderBookmark);
    }

    @Override
    public List<TenderCategorySubscription> findUserSubscription(int userId) {
        return tenderCategorySubscriptionDAO.findUserSubscription(userId);
    }

    @Override
    @Transactional
    public void subscribeToTenderCategory(User user, List<TenderCategory> categories) {
        tenderCategorySubscriptionDAO.removeExistingSubscription(user.getId());

        List<TenderCategorySubscription> subsList = new LinkedList<>();

        for (TenderCategory i : categories) {
            TenderCategorySubscription subs = new TenderCategorySubscription();
            subs.setUser(user);
            subs.setTenderCategory(i);
            subs.setCreatedBy(user.getId());
            subs.setCreatedDate(new Date());
            subs.setLastUpdatedBy(user.getId());
            subs.setLastUpdatedDate(new Date());

            subsList.add(subs);
        }

        tenderCategorySubscriptionDAO.save(subsList);
    }

    @Override
    public List<TenderBookmark> findTenderBookmarkByUserId(int userId) {
        return tenderBookmarkDAO.findTenderBookmarkByUserId(userId);
    }

    @Override
    public Page<Tender> listAllByPage(Pageable pageable) {
        return tenderPagingDAO.findAll(pageable);
    }

    @Override
    public Page<Tender> searchTender(TenderSearchDTO searchDTO, Pageable pageable) {
        Specification<Tender> searchSpec = TenderSpecs.byTenderSearchCriteria(
                searchDTO.getTitle() == null ? null : searchDTO.getTitle().trim()
                , searchDTO.getCompanyName() == null ? null : searchDTO.getCompanyName().trim()
                , searchDTO.getTenderCategory()
                , searchDTO.getStatus());
        return tenderPagingDAO.findAll(searchSpec, pageable);
    }

    private void sendEmails(Tender tender) {
        if (tender != null) {
            // Send tender information to bookmark users.
            List<TenderBookmark>  tenderBookmarks = tenderBookmarkDAO.findTenderBookmarkByTender(tender.getId());
            if (tenderBookmarks != null) {
                Set<User> users = new HashSet<>();
                for (TenderBookmark bookmark : tenderBookmarks) {
                    if (bookmark.getUser() != null) users.add(bookmark.getUser());
                }
                if (users != null && !users.isEmpty()) {
                    Map<String, Object> params = new HashMap<>();
                    params.put(TTConstants.PARAM_TENDER, tender);
                    params.put(TTConstants.PARAM_EMAILS, users.toArray(new User[users.size()]));
                    notificationService.sendNotification(NotificationServiceImpl.NOTI_MODE.bookmark_noti, params);
                }
            }
        }
    }
}
