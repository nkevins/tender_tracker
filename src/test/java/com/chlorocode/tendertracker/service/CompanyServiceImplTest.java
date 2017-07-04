package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.CompanyRegistrationDAO;
import com.chlorocode.tendertracker.dao.entity.CompanyRegistration;
import com.chlorocode.tendertracker.dao.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CompanyServiceImplTest {

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
}
