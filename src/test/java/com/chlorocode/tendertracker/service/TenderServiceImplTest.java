package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.TenderDAO;
import com.chlorocode.tendertracker.dao.entity.Tender;
import com.chlorocode.tendertracker.exception.ApplicationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TenderServiceImplTest {

    @Mock
    private TenderDAO tenderDAO;

    @InjectMocks
    private TenderServiceImpl tenderServiceImpl;

    @Test
    public void testCreateTender() {
        Tender t = new Tender();
        t.setOpenDate(new Date());

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, 1);
        t.setClosedDate(c.getTime());

        when(tenderServiceImpl.createTender(t)).thenReturn(t);

        tenderServiceImpl.createTender(t);
        verify(tenderDAO, times(1)).save(any(Tender.class));
    }

    @Test
    public void testCreateTenderInvalidOpeningClosingDate() {
        Tender t = new Tender();

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, 1);

        t.setOpenDate(c.getTime());
        t.setClosedDate(new Date());

        try {
            tenderServiceImpl.createTender(t);
            fail("Tender Closing Date later than Opening Date allowed");
        } catch(ApplicationException ex) {

        }

        verify(tenderDAO, never()).save(any(Tender.class));
    }
}
