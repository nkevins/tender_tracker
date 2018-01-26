package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.CompanyDAO;
import com.chlorocode.tendertracker.dao.entity.Company;
import com.chlorocode.tendertracker.dao.entity.Country;
import com.chlorocode.tendertracker.dao.entity.Role;
import com.chlorocode.tendertracker.dao.entity.User;
import com.chlorocode.tendertracker.exception.ApplicationException;
import com.chlorocode.tendertracker.service.notification.NotificationServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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
    private UserRoleServiceImpl userRoleServiceImpl;

    @Mock
    private UserService userService;

    @Mock
    private NotificationServiceImpl notificationService;

    @Mock
    private UenEntityServiceImpl uenEntityService;

    @InjectMocks
    private CompanyServiceImpl companyServiceImpl;

    @Test
    public void testRegisterCompany() {
        Country country = new Country();
        country.setId("SG");

        Company reg = new Company("Abc123 Pte. Ltd", "123456K", "123456", 1, "12 Middle Rd", "",
                "123456", "Singapore", "Singapore", "Singapore", country, 1, 1, new Date(), 1, new Date());
        when(companyDAO.save(any(Company.class))).thenReturn(reg);
        when(userService.findById(anyInt())).thenReturn(new User());

        companyServiceImpl.registerCompany(reg);
        verify(companyDAO, times(1)).save(any(Company.class));
    }

    @Test
    public void testApproveCompanyRegistration() {
        Company c = new Company();
        c.setCreatedBy(1);
        User u = new User();
        Role r = new Role();
        List<Role> roles = new LinkedList<>();
        roles.add(r);

        when(companyDAO.findOne(anyInt())).thenReturn(c);
        when(userService.findById(1)).thenReturn(u);
        when(userRoleServiceImpl.findRoleById(2)).thenReturn(r);

        companyServiceImpl.approveCompanyRegistration(1, 1);

        ArgumentCaptor<Company> argument = ArgumentCaptor.forClass(Company.class);
        verify(companyDAO, times(1)).save(any(Company.class));
        verify(userRoleServiceImpl, times(1)).addUserRole(u, roles, c, 1);
        verify(companyDAO).save(argument.capture());
        assertEquals(1, argument.getValue().getStatus());
    }

    @Test
    public void testApproveInvalidCompanyRegistration() {
        when(companyDAO.findOne(anyInt())).thenReturn(null);

        try {
            companyServiceImpl.approveCompanyRegistration(1, 1);
            fail("Record not found but able to proceed");
        } catch (ApplicationException ex) {

        }

        verify(companyDAO, never()).save(any(Company.class));
    }

    @Test
    public void testRejectCompanyRegistration() {
        when(companyDAO.findOne(anyInt())).thenReturn(new Company());

        companyServiceImpl.rejectCompanyRegistration(1, 1);

        ArgumentCaptor<Company> argument = ArgumentCaptor.forClass(Company.class);
        verify(companyDAO, times(1)).save(any(Company.class));
        verify(companyDAO).save(argument.capture());
        assertEquals(2, argument.getValue().getStatus());
    }

    @Test
    public void testRejectInvalidCompanyRegistration() {
        when(companyDAO.findOne(anyInt())).thenReturn(null);

        try {
            companyServiceImpl.rejectCompanyRegistration(1, 1);
            fail("Record not found but able to proceed");
        } catch (ApplicationException ex) {

        }

        verify(companyDAO, never()).save(any(Company.class));
    }
}