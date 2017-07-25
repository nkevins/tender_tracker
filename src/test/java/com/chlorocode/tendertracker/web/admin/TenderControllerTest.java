package com.chlorocode.tendertracker.web.admin;

import com.chlorocode.tendertracker.dao.entity.Company;
import com.chlorocode.tendertracker.dao.entity.CurrentUser;
import com.chlorocode.tendertracker.dao.entity.Tender;
import com.chlorocode.tendertracker.dao.entity.User;
import com.chlorocode.tendertracker.service.TenderService;
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

import java.util.LinkedList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TenderControllerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mvc;
    private CurrentUser currentUser;

    @MockBean
    private TenderService tenderService;

    @Before
    public void setUp() {
        mvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        User u = new User("Name", "test@gmail.com", "123456", "password");
        List<Company> managedCompany = new LinkedList<Company>();
        Company c = new Company();
        c.setName("Abc Pte. Ltd");
        managedCompany.add(c);
        currentUser = new CurrentUser(u, AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER,ROLE_ADMIN"), managedCompany);
    }

    @Test
    public void testViewSearchTenderPage() throws Exception {
        this.mvc.perform(get("/admin/tender").with(user(currentUser)))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/tender/tenderView"));
    }

    @Test
    public void testViewCreateTenderPage() throws Exception {
        this.mvc.perform(get("/admin/tender/create").with(user(currentUser)))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/tender/tenderCreate"));
    }

    @Test
    public void testCreateTender() throws Exception {
        this.mvc.perform(
                post("/admin/tender/create")
                .param("title", "title")
                .param("openDate", "18/07/2017")
                .param("closedDate", "01/09/2017")
                .param("tenderCategory", "1")
                .param("tenderType", "1")
                .param("estimatePurchaseValue", "2000")
                .param("contactPersonName", "Contact Person")
                .param("contactPersonEmail", "test@gmail.com")
                .param("contactPersonPhone", "82751123")
                .param("items[0].uom", "1")
                .param("items[0].quantity", "1")
                .param("items[0].description", "Item 1")
                .param("items[1].uom", "1")
                .param("items[1].quantity", "1")
                .param("items[1].description", "Item 2")
                .with(user(currentUser))
                .with(csrf())
        ).andExpect(view().name("redirect:/admin/tender"));

        verify(tenderService, times(1)).createTender(any(Tender.class), any());
    }

    @Test
    public void testCreateTenderFail() throws Exception {
        this.mvc.perform(
                post("/admin/tender/create")
                        .with(user(currentUser))
                        .with(csrf())
        ).andExpect(view().name("admin/tender/tenderCreate"));

        verify(tenderService, never()).createTender(any(Tender.class), any());
    }
}
