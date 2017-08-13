package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.BidDAO;
import com.chlorocode.tendertracker.dao.DocumentDAO;
import com.chlorocode.tendertracker.dao.entity.Bid;
import com.chlorocode.tendertracker.dao.entity.BidDocument;
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
public class BidServiceImpl implements BidService {

    private BidDAO bidDAO;
    private DocumentDAO documentDAO;
    private S3Wrapper s3Wrapper;

    @Autowired
    public BidServiceImpl(BidDAO bidDAO, DocumentDAO documentDAO, S3Wrapper s3Wrapper) {
        this.bidDAO = bidDAO;
        this.documentDAO = documentDAO;
        this.s3Wrapper = s3Wrapper;
    }

    @Override
    @Transactional
    public Bid saveBid(Bid bid, List<MultipartFile> attachments) {
        // Check duplicate bid
        if (bidDAO.findBidByCompanyAndTender(bid.getCompany().getId(), bid.getTender().getId()) != null) {
            throw new ApplicationException("Same bid already exist");
        }

        Bid result = bidDAO.save(bid);

        for (MultipartFile f : attachments) {
            // Upload to AWS S3
            String bucketPath = "bid_documents/" + bid.getId() + "/" + f.getOriginalFilename();
            try {
                s3Wrapper.upload(f.getInputStream(), bucketPath);
            } catch (IOException ex) {
                throw new ApplicationException("Upload failed " + ex.getMessage());
            }

            // Save to DB
            Document doc = new Document();
            doc.setName(f.getOriginalFilename());
            doc.setLocation(bucketPath);
            doc.setType(2);
            doc.setCreatedBy(bid.getCreatedBy());
            doc.setCreatedDate(new Date());
            doc.setLastUpdatedBy(bid.getLastUpdatedBy());
            doc.setLastUpdatedDate(new Date());
            documentDAO.save(doc);

            BidDocument bidDocument = new BidDocument();
            bidDocument.setDocument(doc);
            bid.addBidDocument(bidDocument);
        }

        return result;
    }

    @Override
    public Bid findBidByCompanyAndTender(int companyId, int tenderId) {
        return bidDAO.findBidByCompanyAndTender(companyId,tenderId);
    }
}
