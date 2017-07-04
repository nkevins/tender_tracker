package com.chlorocode.tendertracker.web;

import com.chlorocode.tendertracker.dao.entity.CompanyRegistration;
import com.chlorocode.tendertracker.dao.entity.CurrentUser;
import com.chlorocode.tendertracker.dao.entity.User;
import com.chlorocode.tendertracker.service.CompanyService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CompanyControllerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mvc;
    private CurrentUser currentUser;

    @Before
    public void setUp(){
        mvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        User u = new User("Name", "test@gmail.com", "123456", "password");
        currentUser = new CurrentUser(u, AuthorityUtils.commaSeparatedStringToAuthorityList("USER"));
    }

    @MockBean
    private CompanyService companyService;

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
        verify(companyService, never()).registerCompany(any(CompanyRegistration.class));
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
        verify(companyService, times(1)).registerCompany(any(CompanyRegistration.class));
    }
}
