package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.BidDAO;
import com.chlorocode.tendertracker.dao.DocumentDAO;
import com.chlorocode.tendertracker.dao.entity.Bid;
import com.chlorocode.tendertracker.dao.entity.Company;
import com.chlorocode.tendertracker.dao.entity.Document;
import com.chlorocode.tendertracker.dao.entity.Tender;
import com.chlorocode.tendertracker.exception.ApplicationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BidServiceImplTest {

    @Mock
    private BidDAO bidDAO;

    @Mock
    private DocumentDAO documentDAO;

    @Mock
    private S3Wrapper s3Wrapper;

    @InjectMocks
    private BidServiceImpl bidServiceImpl;

    @Test
    public void testSave() throws IOException {
        List<MultipartFile> files = new LinkedList<>();
        MockMultipartFile firstFile = new MockMultipartFile("data", "filename.txt", "text/plain", "some content".getBytes());
        files.add(firstFile);

        Company company = new Company();
        company.setId(1);
        Bid bid = new Bid();
        bid.setCompany(company);

        Tender tender = new Tender();
        tender.setId(1);
        bid.setTender(tender);

        when(bidDAO.findBidByCompanyAndTender(1,1)).thenReturn(null);
        bid.setId(2);
        when(bidDAO.save(bid)).thenReturn(bid);

        Bid result = bidServiceImpl.saveBid(bid, files);

        verify(bidDAO, times(1)).save(bid);
        verify(s3Wrapper, times(1)).upload(any(), eq("bid_documents/2/filename.txt"));
        verify(documentDAO, times(1)).save(any(Document.class));
        assertEquals(1, result.getDocuments().size());

        // Verify document type is bid document
        assertEquals(2, result.getDocuments().get(0).getDocument().getType());
    }

    @Test
    public void testDupicateTender() {
        List<MultipartFile> files = new LinkedList<>();
        Company company = new Company();
        company.setId(1);
        Bid bid = new Bid();
        bid.setCompany(company);

        Tender tender = new Tender();
        tender.setId(1);
        bid.setTender(tender);

        when(bidDAO.findBidByCompanyAndTender(1,1)).thenReturn(bid);

        try {
            Bid result = bidServiceImpl.saveBid(bid, files);
            fail("Duplicate bid is allowed");
        } catch(ApplicationException ex) {

        }
    }
}
