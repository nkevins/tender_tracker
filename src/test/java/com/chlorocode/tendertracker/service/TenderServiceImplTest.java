package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.DocumentDAO;
import com.chlorocode.tendertracker.dao.TenderDAO;
import com.chlorocode.tendertracker.dao.entity.Document;
import com.chlorocode.tendertracker.dao.entity.Tender;
import com.chlorocode.tendertracker.dao.entity.TenderItem;
import com.chlorocode.tendertracker.exception.ApplicationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TenderServiceImplTest {

    @Mock
    private TenderDAO tenderDAO;

    @Mock
    private DocumentDAO documentDAO;

    @Mock
    private S3Wrapper s3Wrapper;

    @InjectMocks
    private TenderServiceImpl tenderServiceImpl;

    @Test
    public void testCreateTender() {
        List<MultipartFile> files = new LinkedList<>();
        MockMultipartFile firstFile = new MockMultipartFile("data", "filename.txt", "text/plain", "some content".getBytes());
        files.add(firstFile);

        Tender t = new Tender();
        t.setOpenDate(new Date());

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, 1);
        t.setClosedDate(c.getTime());

        TenderItem i1 = new TenderItem();
        i1.setUom(1);
        i1.setQuantity(1);
        i1.setDescription("Item 1");
        t.addTenderItem(i1);

        assertEquals(t, i1.getTender());

        TenderItem i2 = new TenderItem();
        i2.setUom(1);
        i2.setQuantity(1);
        i2.setDescription("Item 2");
        t.addTenderItem(i2);

        assertEquals(t, i2.getTender());

        t.setId(2);
        when(tenderDAO.save(t)).thenReturn(t);

        Tender result = tenderServiceImpl.createTender(t, files);
        verify(tenderDAO, times(1)).save(any(Tender.class));
        assertEquals(1, result.getStatus());
        verify(s3Wrapper, times(1)).upload(any(), eq("tender_documents/2/filename.txt"));
        verify(documentDAO, times(1)).save(any(Document.class));
        assertEquals(1, t.getDocuments().size());

        // Verify document type is tender document
        assertEquals(1, result.getDocuments().get(0).getDocument().getType());
    }

    @Test
    public void testCreateTenderInvalidOpeningClosingDate() {
        List<MultipartFile> files = new LinkedList<>();

        Tender t = new Tender();

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, 1);

        t.setOpenDate(c.getTime());
        t.setClosedDate(new Date());

        try {
            tenderServiceImpl.createTender(t, files);
            fail("Tender Closing Date later than Opening Date allowed");
        } catch(ApplicationException ex) {

        }

        verify(tenderDAO, never()).save(any(Tender.class));
    }

    @Test
    public void testCreateTenderWithoutItem() {
        List<MultipartFile> files = new LinkedList<>();

        Tender t = new Tender();
        t.setOpenDate(new Date());

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, 1);
        t.setClosedDate(c.getTime());

        try {
            when(tenderServiceImpl.createTender(t, files)).thenReturn(t);
            tenderServiceImpl.createTender(t, files);
            fail("Allowed to save tender without item");
        } catch(Exception ex) {

        }

        verify(tenderDAO, never()).save(any(Tender.class));
    }
}
