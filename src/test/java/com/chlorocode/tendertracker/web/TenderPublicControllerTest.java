package com.chlorocode.tendertracker.web;

import com.chlorocode.tendertracker.dao.entity.Company;
import com.chlorocode.tendertracker.dao.entity.Tender;
import com.chlorocode.tendertracker.dao.entity.TenderCategory;
import com.chlorocode.tendertracker.service.TenderService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TenderPublicControllerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mvc;

    @MockBean
    TenderService tenderService;

    @Before
    public void setUp(){
        mvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
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
}
