package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.*;
import com.chlorocode.tendertracker.dao.entity.*;
import com.chlorocode.tendertracker.exception.ApplicationException;
import com.chlorocode.tendertracker.service.notification.NotificationServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
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
    private TenderCategorySubscriptionDAO tenderCategorySubscriptionDAO;

    @Mock
    private TenderDocumentDAO tenderDocumentDAO;

    @Mock
    private S3Wrapper s3Wrapper;

    @Mock
    private TenderBookmarkDAO tenderBookmarkDAO;

    @Mock
    private NotificationServiceImpl notificationService;

    @InjectMocks
    private TenderServiceImpl tenderServiceImpl;

    @Test
    public void testCreateTender() {
        List<MultipartFile> files = new LinkedList<>();
        MockMultipartFile firstFile = new MockMultipartFile("data", "filename.txt", "text/plain", "some content".getBytes());
        files.add(firstFile);

        TenderCategory cat = new TenderCategory();
        cat.setId(1);

        Tender t = new Tender();
        t.setOpenDate(new Date());
        t.setTenderCategory(cat);

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
        verify(tenderDocumentDAO, times(1)).save(any(TenderDocument.class));
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

    @Test
    public void testCreateTenderBookmark() {
        Tender tender = new Tender();
        User user = new User();

        tenderServiceImpl.bookmarkTender(tender, user);

        ArgumentCaptor<TenderBookmark> argument = ArgumentCaptor.forClass(TenderBookmark.class);
        verify(tenderBookmarkDAO, times(1)).save(any(TenderBookmark.class));
        verify(tenderBookmarkDAO).save(argument.capture());
        assertEquals(tender, argument.getValue().getTender());
        assertEquals(user, argument.getValue().getUser());
    }

    @Test
    public void testRemoveTenderBookmark() {
        TenderBookmark tenderBookmark = new TenderBookmark();

        when(tenderBookmarkDAO.findTenderBookmarkByUserAndTender(1, 1)).thenReturn(tenderBookmark);

        tenderServiceImpl.removeTenderBookmark(1, 1);

        verify(tenderBookmarkDAO, times(1)).delete(tenderBookmark);
    }
}
