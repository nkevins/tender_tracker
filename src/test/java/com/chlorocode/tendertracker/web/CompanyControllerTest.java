package com.chlorocode.tendertracker.web;

import com.chlorocode.tendertracker.dao.entity.Company;
import com.chlorocode.tendertracker.dao.entity.CurrentUser;
import com.chlorocode.tendertracker.dao.entity.User;
import com.chlorocode.tendertracker.service.CompanyService;
import com.chlorocode.tendertracker.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.LinkedList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class CompanyControllerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mvc;
    private CurrentUser currentUser;

    @MockBean
    private CompanyService companyService;

    @MockBean
    private UserService userService;

    @Before
    public void setUp(){
        mvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        User u = new User("Name", "test@gmail.com", "123456", "password");
        List<Company> managedCompany = new LinkedList<Company>();
        currentUser = new CurrentUser(u, AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER"), managedCompany);
    }

    @Test
    public void testLoadCompanyRegistrationPage() throws Exception {
        this.mvc.perform(get("/registerCompany").with(user(currentUser)))
                .andExpect(status().isOk())
                .andExpect(view().name("registerCompany"));
    }

    @Test
    public void testInvalidRegisterCompany() throws Exception {
        this.mvc.perform(
                post("/registerCompany")
                        .with(user(currentUser))
                        .with(csrf())
                )
                .andExpect(view().name("registerCompany"));
        verify(companyService, never()).registerCompany(any(Company.class));
    }

    @Test
    public void testRegisterCompany() throws Exception {
        this.mvc.perform(
                post("/registerCompany")
                        .param("name", "Company Pte. Ltd.")
                        .param("uen", "123456K")
                        .param("gstRegNo", "123456")
                        .param("companyType", "1")
                        .param("areaOfBusiness", "1")
                        .param("address1", "Address 1")
                        .param("postalCode", "Singapore")
                        .param("city", "Singapore")
                        .param("state", "Singapore")
                        .param("province", "Singapore")
                        .param("country", "Singapore")
                        .with(user(currentUser))
                        .with(csrf())
                )
                .andExpect(view().name("redirect:/"));
        verify(companyService, times(1)).registerCompany(any(Company.class));
    }

    @Test
    public void testViewCompanyDetails() throws Exception {
        Company company = new Company();
        company.setCreatedBy(1);
        company.setStatus(1);
        User user = new User();

        when(companyService.findById(1)).thenReturn(company);
        when(userService.findById(1)).thenReturn(user);

        this.mvc.perform(get("/company/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("companyDetails"));

        verify(companyService, times(1)).findById(1);
    }

    @Test
    public void testViewCompanyNotExist() throws Exception {
        when(companyService.findById(1)).thenReturn(null);

        this.mvc.perform(get("/company/1"))
                .andExpect(view().name("redirect:/"));

        verify(companyService, times(1)).findById(1);
    }

    @Test
    public void testViewCompanyStatusNotAllowed() throws Exception {
        Company company = new Company();
        company.setCreatedBy(1);
        company.setStatus(0);
        User user = new User();

        when(companyService.findById(1)).thenReturn(company);
        when(userService.findById(1)).thenReturn(user);

        this.mvc.perform(get("/company/1"))
                .andExpect(view().name("redirect:/"));

        verify(companyService, times(1)).findById(1);
    }
}
