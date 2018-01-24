package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.TenderAppealDAO;
import com.chlorocode.tendertracker.dao.entity.Company;
import com.chlorocode.tendertracker.dao.entity.Tender;
import com.chlorocode.tendertracker.dao.entity.TenderAppeal;
import com.chlorocode.tendertracker.exception.ApplicationException;
import com.chlorocode.tendertracker.service.notification.NotificationService;
import com.chlorocode.tendertracker.service.notification.NotificationServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TenderAppealServiceImplTest {

    @Mock
    private TenderAppealDAO tenderAppealDAO;

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserRoleService userRoleService;

    @InjectMocks
    private TenderAppealServiceImpl tenderAppealService;

    @Test
    public void testSubmitAppeal() {
        Tender tender = new Tender();
        tender.setId(1);

        Company company = new Company();
        company.setId(20);

        TenderAppeal appeal = new TenderAppeal();
        appeal.setTender(tender);
        appeal.setCompany(company);

        when(tenderAppealDAO.findTenderAppealsBy(1, 20)).thenReturn(null);

        tenderAppealService.create(appeal);
        verify(tenderAppealDAO, times(1)).saveAndFlush(appeal);
    }

    @Test
    public void testDuplicateSubmitAppeal() {
        Tender tender = new Tender();
        tender.setId(1);

        Company company = new Company();
        company.setId(20);

        TenderAppeal appeal = new TenderAppeal();
        appeal.setTender(tender);
        appeal.setCompany(company);

        List<TenderAppeal> duplicateAppeals = new LinkedList<>();
        duplicateAppeals.add(appeal);

        when(tenderAppealDAO.findTenderAppealsBy(1, 20)).thenReturn(duplicateAppeals);

        try {
            tenderAppealService.create(appeal);
            fail("Duplicate tender appeal is allowed");
        } catch (ApplicationException ex) {

        }
        verify(tenderAppealDAO, times(0)).saveAndFlush(appeal);
    }

    @Test
    public void testProcessTenderAppeal() {
        Tender tender = new Tender();

        Company company = new Company();
        company.setId(20);

        TenderAppeal appeal = new TenderAppeal();
        appeal.setCompany(company);
        appeal.setTender(tender);

        Set<String> emails = new HashSet<>();
        emails.add("test@gmail.com");

        when(tenderAppealDAO.findOne(1)).thenReturn(appeal);
        when(userRoleService.findCompanyAdminEmails(20)).thenReturn(emails);

        tenderAppealService.processTenderAppeal(1, 1, 2);

        verify(tenderAppealDAO, times(1)).saveAndFlush(appeal);
        verify(notificationService, times(1)).sendNotification(eq(NotificationServiceImpl.NOTI_MODE.appeal_update_noti), any());
    }
}
