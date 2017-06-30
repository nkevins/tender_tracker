package com.chlorocode.tendertracker.web;

import com.chlorocode.tendertracker.dao.entity.User;
import com.chlorocode.tendertracker.exception.ApplicationException;
import com.chlorocode.tendertracker.service.UserService;
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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthControllerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mvc;

    @Before
    public void setUp(){
        mvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @MockBean
    private UserService userService;

    @Test
    public void testLoadLoginPage() throws Exception {
        this.mvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    public void testLoadRegistrationPage() throws Exception {
        this.mvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("registerUser"));
    }

    @Test
    public void testSubmitEmptyRegistrationForm() throws Exception {
        this.mvc.perform(
                    post("/register")
                    .with(csrf())
                )
                .andExpect(view().name("registerUser"));
        verify(userService, never()).create(any(User.class));
    }

    @Test
    public void testSuccessfulRegistration() throws Exception {
        this.mvc.perform(
                post("/register")
                        .param("name", "Name")
                        .param("email", "email@test.com")
                        .param("contactNo", "12345678")
                        .param("password", "password")
                        .param("confirmPassword", "password")
                        .with(csrf())
                )
                .andExpect(view().name("redirect:/login"));
        verify(userService, times(1)).create(any(User.class));
    }

    @Test
    public void testRegistrationWithWrongConfirmPassword() throws Exception {
        this.mvc.perform(
                post("/register")
                        .param("name", "Name")
                        .param("email", "email@test.com")
                        .param("contactNo", "12345678")
                        .param("password", "password")
                        .param("confirmPassword", "password1")
                        .with(csrf())
        )
                .andExpect(view().name("registerUser"));
        verify(userService, never()).create(any(User.class));
    }

    @Test
    public void testDuplicateUserRegistration() throws Exception {
        when(userService.create(any(User.class))).thenThrow(new ApplicationException("User already exist"));
        this.mvc.perform(
                post("/register")
                        .param("name", "Name")
                        .param("email", "email@test.com")
                        .param("contactNo", "12345678")
                        .param("password", "password")
                        .param("confirmPassword", "password")
                        .with(csrf())
        )
                .andExpect(view().name("registerUser"));
        verify(userService, times(1)).create(any(User.class));
    }
}
