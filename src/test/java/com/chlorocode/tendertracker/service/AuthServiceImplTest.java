package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.UserDAO;
import com.chlorocode.tendertracker.dao.UserRoleDAO;
import com.chlorocode.tendertracker.dao.entity.Company;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AuthServiceImplTest {

    @Mock
    private UserDAO userDAO;

    @Mock
    private UserRoleDAO userRoleDAO;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    public void testValidLogin() {
        User u = new User("Name", "test@gmail.com", "123456", "password");
        when(userDAO.findOneByEmail(u.getEmail())).thenReturn(Optional.of(u));
        when(userRoleDAO.findUniqueUserRole(anyInt())).thenReturn(new LinkedList<>());
        when(userRoleDAO.findUserAdministeredCompany(anyInt())).thenReturn(new LinkedList<>());

        try {
            UserDetails usr = authService.loadUserByUsername(u.getEmail());
            assertNotNull(usr);
            assertTrue(usr instanceof CurrentUser);
            assertEquals(u.getEmail(), usr.getUsername());

            // Test user must have USER role
            Collection<? extends GrantedAuthority> authorities = usr.getAuthorities();
            assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")));
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

    @Test
    public void testUserAdministerOneCompany() {
        User u = new User("Name", "test@gmail.com", "123456", "password");
        List<String> userRoles = new LinkedList<>();
        userRoles.add("ADMIN");

        List<Company> companies = new LinkedList<>();
        companies.add(new Company());

        when(userDAO.findOneByEmail(u.getEmail())).thenReturn(Optional.of(u));
        when(userRoleDAO.findUniqueUserRole(anyInt())).thenReturn(userRoles);
        when(userRoleDAO.findUserAdministeredCompany(anyInt())).thenReturn(companies);

        try {
            UserDetails usr = authService.loadUserByUsername(u.getEmail());
            assertNotNull(usr);
            assertTrue(usr instanceof CurrentUser);
            assertEquals(u.getEmail(), usr.getUsername());
            CurrentUser currentUser = (CurrentUser) usr;

            assertEquals(companies, currentUser.getCompanyAdministered());
            assertEquals(companies.get(0), currentUser.getSelectedCompany());
        } catch (Exception e) {
            fail("Exception thrown when login: " + e.getMessage());
        }
    }

    @Test
    public void testUserAdministerMultipleCompany() {
        User u = new User("Name", "test@gmail.com", "123456", "password");
        List<String> userRoles = new LinkedList<>();
        userRoles.add("ADMIN");

        List<Company> companies = new LinkedList<>();
        Company c1 = new Company();
        c1.setName("Company1");
        Company c2 = new Company();
        c2.setName("Company2");
        companies.add(c1);
        companies.add(c2);

        when(userDAO.findOneByEmail(u.getEmail())).thenReturn(Optional.of(u));
        when(userRoleDAO.findUniqueUserRole(anyInt())).thenReturn(userRoles);
        when(userRoleDAO.findUserAdministeredCompany(anyInt())).thenReturn(companies);

        try {
            UserDetails usr = authService.loadUserByUsername(u.getEmail());
            assertNotNull(usr);
            assertTrue(usr instanceof CurrentUser);
            assertEquals(u.getEmail(), usr.getUsername());
            CurrentUser currentUser = (CurrentUser) usr;

            assertEquals(companies, currentUser.getCompanyAdministered());
            assertNull(currentUser.getSelectedCompany());
        } catch (Exception e) {
            fail("Exception thrown when login: " + e.getMessage());
        }
    }
}
