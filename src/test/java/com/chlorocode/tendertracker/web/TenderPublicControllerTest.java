package com.chlorocode.tendertracker.web;

import com.chlorocode.tendertracker.dao.entity.*;
import com.chlorocode.tendertracker.service.BidService;
import com.chlorocode.tendertracker.service.TenderService;
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

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class TenderPublicControllerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mvc;
    private CurrentUser currentUser;

    @MockBean
    TenderService tenderService;

    @MockBean
    BidService bidService;

    @Before
    public void setUp(){
        mvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        User u = new User("Name", "test@gmail.com", "12345", "password");
        List<Company> managedCompany = new LinkedList<Company>();
        Company c = new Company();
        c.setName("Abc Pte. Ltd");
        managedCompany.add(c);
        currentUser = new CurrentUser(u, AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER,ROLE_ADMIN"), managedCompany);
    }

    @Test
    public void testDisplayTenderDetailsPage() throws Exception {
        Tender t = new Tender();
        Company c = new Company();
        TenderCategory cat = new TenderCategory();
        t.setTenderCategory(cat);
        t.setOpenDate(new Date());
        t.setClosedDate(new Date());
        t.setCompany(c);
        when(tenderService.findById(anyInt())).thenReturn(t);

        this.mvc.perform(get("/tender/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("tenderDetails"));
    }

    @Test
    public void testDisplayTenderNotExist() throws Exception {
        when(tenderService.findById(anyInt())).thenReturn(null);

        this.mvc.perform(get("/tender/111"))
                .andExpect(view().name("redirect:/"));
    }

    @Test
    public void testAccessTenderResponsePage() throws Exception {
        Company company = new Company();
        Tender t = new Tender();
        t.setCompany(company);

        when(tenderService.findById(1)).thenReturn(t);

        // Test using ADMIN role
        this.mvc.perform(
                get("/tender/1/respond").with(user(currentUser))
        ).andExpect(status().isOk())
                .andExpect(view().name("tenderResponse"));

        // Test using PREPARER role
        User u = new User("Name", "test@gmail.com", "123456", "password");
        List<Company> managedCompany = new LinkedList<Company>();
        Company c = new Company();
        c.setName("Abc Pte. Ltd");
        managedCompany.add(c);
        currentUser = new CurrentUser(u, AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER,ROLE_SUBMITTER"), managedCompany);
        this.mvc.perform(
                get("/tender/1/respond").with(user(currentUser))
        ).andExpect(status().isOk())
                .andExpect(view().name("tenderResponse"));
    }

    @Test
    public void testUnauthorizedAccessToRespondTenderPage() throws Exception {
        Company company = new Company();
        Tender t = new Tender();
        t.setCompany(company);

        when(tenderService.findById(1)).thenReturn(t);

        this.mvc.perform(
                get("/tender/1/respond")
        ).andExpect(status().isForbidden());
    }

    @Test
    public void testRespondTenderPageNotExist() throws Exception {
        when(tenderService.findById(1)).thenReturn(null);

        this.mvc.perform(
                get("/tender/1/respond").with(user(currentUser))
        ).andExpect(status().isNotFound());
    }

    @Test
    public void testSubmitTenderItem() throws Exception {
        this.mvc.perform(
                post("/tender/respond")
                        .param("items[0].quotedPrice", "15")
                        .param("items[1].quotedPrice", "10")
                        .with(user(currentUser))
                        .with(csrf())
        ).andExpect(view().name("redirect:/"));

        verify(bidService, times(1)).saveBid(any(Bid.class), any());
    }

    @Test
    public void testBookmarkTender() throws Exception {
        Tender tender = new Tender();

        when(tenderService.findById(15)).thenReturn(tender);

        this.mvc.perform(
                post("/tender/bookmark")
                        .param("tenderId", "15")
                        .with(user(currentUser))
                        .with(csrf())
        ).andExpect(view().name("redirect:/tender/15"));

        verify(tenderService, times(1)).bookmarkTender(tender, currentUser.getUser());
    }

    @Test
    public void testRemoveTenderBookmark() throws Exception {
        this.mvc.perform(
                post("/tender/removeBookmark")
                        .param("tenderId", "15")
                        .with(user(currentUser))
                        .with(csrf())
        ).andExpect(view().name("redirect:/tender/15"));

        verify(tenderService, times(1)).removeTenderBookmark(15, currentUser.getId());
    }
}
