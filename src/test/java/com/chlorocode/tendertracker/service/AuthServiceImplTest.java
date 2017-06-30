package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.UserDAO;
import com.chlorocode.tendertracker.dao.entity.CurrentUser;
import com.chlorocode.tendertracker.dao.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AuthServiceImplTest {

    @Mock
    private UserDAO userDAO;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    public void testValidLogin() {
        User u = new User("Name", "test@gmail.com", "123456", "password");
        when(userDAO.findOneByEmail(u.getEmail())).thenReturn(Optional.of(u));

        try {
            UserDetails usr = authService.loadUserByUsername(u.getEmail());
            assertNotNull(usr);
            assertTrue(usr instanceof CurrentUser);
            assertEquals(u.getEmail(), usr.getUsername());

            // Test user must have USER role
            Collection<? extends GrantedAuthority> authorities = usr.getAuthorities();
            assertTrue(authorities.contains(new SimpleGrantedAuthority("USER")));
        } catch (Exception e) {
            fail("Exception thrown when login: " + e.getMessage());
        }
    }

    @Test
    public void testInvalidLogin() {
        User u = new User("Name", "test@gmail.com", "123456", "password");
        when(userDAO.findOneByEmail(any(String.class))).thenReturn(Optional.empty());

        try {
            authService.loadUserByUsername(u.getEmail());
            fail("User not found and no exception thrown");
        } catch (Exception e) {
            assertTrue(e instanceof UsernameNotFoundException);
        }
    }
}
