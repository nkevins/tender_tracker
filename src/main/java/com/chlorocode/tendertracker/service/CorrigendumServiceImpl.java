package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.constants.TTConstants;
import com.chlorocode.tendertracker.dao.CorrigendumDAO;
import com.chlorocode.tendertracker.dao.CorrigendumDocumentDAO;
import com.chlorocode.tendertracker.dao.DocumentDAO;
import com.chlorocode.tendertracker.dao.entity.Corrigendum;
import com.chlorocode.tendertracker.dao.entity.CorrigendumDocument;
import com.chlorocode.tendertracker.dao.entity.Document;
import com.chlorocode.tendertracker.exception.ApplicationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
public class CorrigendumServiceImpl implements CorrigendumService {

    private CorrigendumDAO corrigendumDAO;
    private CorrigendumDocumentDAO corrigendumDocumentDAO;
    private DocumentDAO documentDAO;
    private S3Wrapper s3Wrapper;
    private TenderService tenderService;

    @Autowired
    public CorrigendumServiceImpl(CorrigendumDAO corrigendumDAO, CorrigendumDocumentDAO corrigendumDocumentDAO,
                                  DocumentDAO documentDAO, S3Wrapper s3Wrapper, TenderService tenderService) {
        this.corrigendumDAO = corrigendumDAO;
        this.corrigendumDocumentDAO = corrigendumDocumentDAO;
        this.documentDAO = documentDAO;
        this.s3Wrapper = s3Wrapper;
        this.tenderService = tenderService;
    }

    @Override
    public Corrigendum findCorrigendumById(int corrigendumId) {
        return corrigendumDAO.findOne(corrigendumId);
    }

    @Override
    public List<Corrigendum> findTenderCorrigendum(int tenderId) {
        return corrigendumDAO.findCorrigendumByTender(tenderId);
    }

    @Override
    @Transactional
    public Corrigendum addCorrigendum(Corrigendum corrigendum, List<MultipartFile> attachments) {
        Corrigendum result = corrigendumDAO.save(corrigendum);

        for (MultipartFile f : attachments) {
            // Upload to AWS S3
            String bucketPath = "tender_corrigendums/" + corrigendum.getId() + "/" + f.getOriginalFilename();
            try {
                s3Wrapper.upload(f.getInputStream(), bucketPath);
            } catch (IOException ex) {
                throw new ApplicationException("Upload failed " + ex.getMessage());
            }

            // Save to DB
            Document doc = new Document();
            doc.setName(f.getOriginalFilename());
            doc.setLocation(bucketPath);
            doc.setType(3);
            doc.setCreatedBy(corrigendum.getCreatedBy());
            doc.setCreatedDate(new Date());
            doc.setLastUpdatedBy(corrigendum.getLastUpdatedBy());
            doc.setLastUpdatedDate(new Date());
            documentDAO.save(doc);

            CorrigendumDocument corrigendumDocument = new CorrigendumDocument();
            corrigendumDocument.setDocument(doc);
            corrigendum.addDocument(corrigendumDocument);
        }

        if (result != null) {
            tenderService.sendBookmarkNoti(result.getTender(), TTConstants.ADD_CORRIGENDUM);
        }

        return result;
    }

    @Override
    public Corrigendum updateCorrigendum(Corrigendum corrigendum) {
        return corrigendumDAO.saveAndFlush(corrigendum);
    }

    @Override
    @Transactional
    public void removeCorrigendum(int corrigendumId) {
        Corrigendum corrigendum = corrigendumDAO.findOne(corrigendumId);

        for (CorrigendumDocument corrigendumDocument : corrigendum.getDocuments()) {
            Document doc = corrigendumDocument.getDocument();
            // Remove from S3
            String bucketPath = "tender_corrigendums/" + corrigendum.getId() + "/" + doc.getName();
            s3Wrapper.deleteObject(bucketPath);

            corrigendumDocument.setCorrigendum(null);
            corrigendumDocument.setDocument(null);
            corrigendumDocumentDAO.delete(corrigendumDocument);
            documentDAO.delete(doc);
        }

        corrigendumDAO.delete(corrigendum);
    }

    @Override
    @Transactional
    public CorrigendumDocument addCorrigendumDocument(MultipartFile attachment, Corrigendum corrigendum, int createdBy) {
        // Upload to AWS S3
        String bucketPath = "tender_corrigendums/" + corrigendum.getId() + "/" + attachment.getOriginalFilename();
        try {
            s3Wrapper.upload(attachment.getInputStream(), bucketPath);
        } catch (IOException ex) {
            throw new ApplicationException("Upload failed " + ex.getMessage());
        }

        // Save to DB
        Document doc = new Document();
        doc.setName(attachment.getOriginalFilename());
        doc.setLocation(bucketPath);
        doc.setType(3);
        doc.setCreatedBy(createdBy);
        doc.setCreatedDate(new Date());
        doc.setLastUpdatedBy(createdBy);
        doc.setLastUpdatedDate(new Date());
        documentDAO.save(doc);

        CorrigendumDocument corrigendumDocument = new CorrigendumDocument();
        corrigendumDocument.setDocument(doc);
        corrigendumDocument.setCorrigendum(corrigendum);

        return corrigendumDocumentDAO.save(corrigendumDocument);
    }

    @Override
    @Transactional
    public void removeCorrigendumDocument(int id) {
        CorrigendumDocument corrigendumDocument = corrigendumDocumentDAO.findOne(id);
        Document document = corrigendumDocument.getDocument();

        // Remove from S3
        String bucketPath = "tender_corrigendums/" + corrigendumDocument.getCorrigendum().getId() + "/" + document.getName();
        s3Wrapper.deleteObject(bucketPath);

        corrigendumDocument.setCorrigendum(null);
        corrigendumDocument.setDocument(null);
        corrigendumDocumentDAO.delete(corrigendumDocument);
        documentDAO.delete(document);
    }
}
