package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.DocumentDAO;
import com.chlorocode.tendertracker.dao.TenderBookmarkDAO;
import com.chlorocode.tendertracker.dao.TenderDAO;
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
    private DocumentDAO documentDAO;
    private S3Wrapper s3Wrapper;
    private TenderBookmarkDAO tenderBookmarkDAO;

    @Autowired
    public TenderServiceImpl(TenderDAO tenderDAO, DocumentDAO documentDAO, S3Wrapper s3Wrapper, TenderBookmarkDAO tenderBookmarkDAO) {
        this.tenderDAO = tenderDAO;
        this.documentDAO = documentDAO;
        this.s3Wrapper = s3Wrapper;
        this.tenderBookmarkDAO = tenderBookmarkDAO;
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
    public Tender findById(int id) {
        return tenderDAO.findOne(id);
    }

    @Override
    public TenderItem findTenderItemById(int id) {
        return tenderDAO.findTenderItemById(id);
    }

    @Override
    public List<Tender> findTender() {
        List<Tender> tenders = new LinkedList<>();
        Iterable<Tender> iterable = tenderDAO.findAll();
        iterable.forEach(tenders::add);

        return tenders;
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
