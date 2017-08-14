package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.UserDAO;
import com.chlorocode.tendertracker.dao.entity.User;
import com.chlorocode.tendertracker.exception.ApplicationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    @Spy
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Mock
    private UserDAO userDAO;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @Test
    public void testCreateUser() {
        User u = new User("Name", "test@gmail.com", "123456", "password");
        when(userDAO.findOneByEmail(anyString())).thenReturn(Optional.empty());
        when(userDAO.save(any(User.class))).thenReturn(u);

        try {
            userServiceImpl.create(u);
        } catch (Exception e) {
            fail("Exception thrown when creating user: " + e.getMessage());
        }

        ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);
        verify(userDAO, times(1)).save(any(User.class));
        verify(userDAO).save(argument.capture());
        assertTrue(passwordEncoder.matches("password", argument.getValue().getPassword()));
    }

    @Test
    public void testCreateUserWithDuplicateEmail() {
        User u = new User("Name", "test@gmail.com", "123456", "password");
        when(userDAO.findOneByEmail("test@gmail.com")).thenReturn(Optional.of(new User()));

        try {
            userServiceImpl.create(u);
            fail("Allowed to create user with same email");
        } catch (ApplicationException ex) {
            assert(ex.getMessage().equals("User already exist"));
        }

        verify(userDAO, never()).save(any(User.class));
    }

    @Test
    public void testNRICValidation() {
        String nric = "S2683266G";
        boolean result = userServiceImpl.isNRICValid(nric);
        assertTrue(result);

        nric = "T6758107A";
        result = userServiceImpl.isNRICValid(nric);
        assertTrue(result);

        nric = "S2623266G";
        result = userServiceImpl.isNRICValid(nric);
        assertFalse(result);

        nric = "A1234567A";
        result = userServiceImpl.isNRICValid(nric);
        assertFalse(result);

        nric = "12345";
        result = userServiceImpl.isNRICValid(nric);
        assertFalse(result);

        nric = "G2683266G";
        result = userServiceImpl.isNRICValid(nric);
        assertFalse(result);

        nric = "F3274338L";
        result = userServiceImpl.isNRICValid(nric);
        assertFalse(result);

        nric = "G4463484N";
        result = userServiceImpl.isNRICValid(nric);
        assertFalse(result);
    }

    @Test
    public void testFINValidation() {
        String fin = "G4630909Q";
        boolean result = userServiceImpl.isFINValid(fin);
        assertTrue(result);

        fin = "F5161580T";
        result = userServiceImpl.isFINValid(fin);
        assertTrue(result);

        fin = "G4630909A";
        result = userServiceImpl.isFINValid(fin);
        assertFalse(result);

        fin = "S6925939A";
        result = userServiceImpl.isFINValid(fin);
        assertFalse(result);

        fin = "T2075671Z";
        result = userServiceImpl.isFINValid(fin);
        assertFalse(result);

        fin = "A1234567A";
        result = userServiceImpl.isFINValid(fin);
        assertFalse(result);

        fin = "12345";
        result = userServiceImpl.isFINValid(fin);
        assertFalse(result);
    }
}
