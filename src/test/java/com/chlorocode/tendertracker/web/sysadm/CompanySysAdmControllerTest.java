package com.chlorocode.tendertracker.web.sysadm;

import com.chlorocode.tendertracker.dao.dto.CompanyRegistrationDetailsDTO;
import com.chlorocode.tendertracker.dao.entity.Company;
import com.chlorocode.tendertracker.dao.entity.CurrentUser;
import com.chlorocode.tendertracker.dao.entity.User;
import com.chlorocode.tendertracker.service.CompanyService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
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

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static org.mockito.Matchers.anyInt;
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
public class CompanySysAdmControllerTest {

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
        List<Company> managedCompany = new LinkedList<Company>();
        currentUser = new CurrentUser(u, AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER,ROLE_SYS_ADMIN"), managedCompany);
    }

    @MockBean
    private CompanyService companyService;

    @Test
    public void testGetCompanyRegistrationListPage() throws Exception {
        this.mvc.perform(get("/sysadm/companyRegistration").with(user(currentUser)))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/sysadm/companyRegistrationView"));
    }

    @Test
    public void testViewCompanyRegistrationDetail() throws Exception {
        CompanyRegistrationDetailsDTO c = Mockito.mock(CompanyRegistrationDetailsDTO.class);
        User u = Mockito.mock(User.class);
        when(u.getName()).thenReturn("Name");
        when(c.getApplicationDate()).thenReturn(new Date());
        when(c.getApplicant()).thenReturn(u);
        when(companyService.findCompanyRegistrationById(anyInt())).thenReturn(c);

        this.mvc.perform(get("/sysadm/companyRegistration/1").with(user(currentUser)))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/sysadm/companyRegistrationDetail"));
    }

    @Test
    public void testViewCompanyRegistrationNotExist() throws Exception {
        when(companyService.findCompanyRegistrationById(anyInt())).thenReturn(null);

        this.mvc.perform(get("/sysadm/companyRegistration/841").with(user(currentUser)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testApproveRegistration() throws Exception {
        this.mvc.perform(
                post("/sysadm/companyRegistration").with(user(currentUser))
                    .param("id", "1")
                    .param("action", "approve")
                    .with(csrf())
        ).andExpect(view().name("redirect:/sysadm/companyRegistration"));

        verify(companyService, times(1)).approveCompanyRegistration(anyInt(), anyInt());
    }

    @Test
    public void testRejectRegistraiton() throws Exception {
        this.mvc.perform(
                post("/sysadm/companyRegistration").with(user(currentUser))
                        .param("id", "1")
                        .param("action", "reject")
                        .with(csrf())
        ).andExpect(view().name("redirect:/sysadm/companyRegistration"));

        verify(companyService, times(1)).rejectCompanyRegistration(anyInt(), anyInt());
    }
}
