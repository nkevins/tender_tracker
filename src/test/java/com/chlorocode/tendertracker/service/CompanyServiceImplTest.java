package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.CompanyDAO;
import com.chlorocode.tendertracker.dao.CompanyRegistrationDAO;
import com.chlorocode.tendertracker.dao.entity.Company;
import com.chlorocode.tendertracker.dao.entity.CompanyRegistration;
import com.chlorocode.tendertracker.dao.entity.User;
import com.chlorocode.tendertracker.exception.ApplicationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CompanyServiceImplTest {

    @Mock
    private CompanyDAO companyDAO;

    @Mock
    private CompanyRegistrationDAO companyRegistrationDAO;

    @InjectMocks
    private CompanyServiceImpl companyServiceImpl;

    @Test
    public void testRegisterCompany() {
        CompanyRegistration reg = new CompanyRegistration("Abc Pte. Ltd", "123456K", "123456", 1, "12 Middle Rd", "",
                "123456", "Singapore", "Singapore", "Singapore", "Singapore", 1, new User(), 1, new Date(), 1, new Date());
        when(companyServiceImpl.registerCompany(reg)).thenReturn(reg);

        companyServiceImpl.registerCompany(reg);
        verify(companyRegistrationDAO, times(1)).save(any(CompanyRegistration.class));
    }

    @Test
    public void testApproveCompanyRegistration() {
        when(companyDAO.findCompanyRegistrationById(anyInt())).thenReturn(new Company());

        companyServiceImpl.approveCompanyRegistration(1, 1);

        ArgumentCaptor<Company> argument = ArgumentCaptor.forClass(Company.class);
        verify(companyDAO, times(1)).save(any(Company.class));
        verify(companyDAO).save(argument.capture());
        assertEquals(1, argument.getValue().getStatus());
    }

    @Test
    public void testApproveInvalidCompanyRegistration() {
        when(companyDAO.findCompanyRegistrationById(anyInt())).thenReturn(null);

        try {
            companyServiceImpl.approveCompanyRegistration(1, 1);
            fail("Record not found but able to proceed");
        } catch (ApplicationException ex) {

        }

        verify(companyDAO, never()).save(any(Company.class));
    }

    @Test
    public void testRejectCompanyRegistration() {
        when(companyDAO.findCompanyRegistrationById(anyInt())).thenReturn(new Company());

        companyServiceImpl.rejectCompanyRegistration(1, 1);

        ArgumentCaptor<Company> argument = ArgumentCaptor.forClass(Company.class);
        verify(companyDAO, times(1)).save(any(Company.class));
        verify(companyDAO).save(argument.capture());
        assertEquals(2, argument.getValue().getStatus());
    }

    @Test
    public void testRejectInvalidCompanyRegistration() {
        when(companyDAO.findCompanyRegistrationById(anyInt())).thenReturn(null);

        try {
            companyServiceImpl.rejectCompanyRegistration(1, 1);
            fail("Record not found but able to proceed");
        } catch (ApplicationException ex) {

        }

        verify(companyDAO, never()).save(any(Company.class));
    }
}