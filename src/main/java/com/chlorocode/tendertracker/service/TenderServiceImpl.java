package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.*;
import com.chlorocode.tendertracker.dao.entity.*;
import com.chlorocode.tendertracker.exception.ApplicationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Service
public class TenderServiceImpl implements TenderService {

    private TenderDAO tenderDAO;
    private TenderItemDAO tenderItemDAO;
    private TenderDocumentDAO tenderDocumentDAO;
    private DocumentDAO documentDAO;
    private S3Wrapper s3Wrapper;
    private TenderBookmarkDAO tenderBookmarkDAO;

    @Autowired
    public TenderServiceImpl(TenderDAO tenderDAO, DocumentDAO documentDAO, S3Wrapper s3Wrapper, TenderBookmarkDAO tenderBookmarkDAO,
                             TenderItemDAO tenderItemDAO, TenderDocumentDAO tenderDocumentDAO) {
        this.tenderDAO = tenderDAO;
        this.tenderItemDAO = tenderItemDAO;
        this.tenderDocumentDAO = tenderDocumentDAO;
        this.documentDAO = documentDAO;
        this.s3Wrapper = s3Wrapper;
        this.tenderBookmarkDAO = tenderBookmarkDAO;
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
        return tenderDAO.save(tender);
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
        return tenderItemDAO.save(tenderItem);
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
}
