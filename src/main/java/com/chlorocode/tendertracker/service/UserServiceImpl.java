package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.UserDAO;
import com.chlorocode.tendertracker.dao.entity.User;
import com.chlorocode.tendertracker.exception.ApplicationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private PasswordEncoder passwordEncoder;
    private UserDAO userDAO;

    @Autowired
    public UserServiceImpl(UserDAO userDAO, PasswordEncoder passwordEncoder) {
        this.userDAO = userDAO;
        this.passwordEncoder = passwordEncoder;
    }

    public User create(User user) throws ApplicationException {
        // validate duplicate email
        Optional<User> u = userDAO.findOneByEmail(user.getEmail());
        if (u.isPresent()) {
            throw new ApplicationException("User already exist");
        }

        // Encrypt password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        user.setStatus(1);
        user.setCreatedDate(new Date());
        user.setLastUpdatedDate(new Date());

        User savedUser = userDAO.save(user);
        savedUser.setCreatedBy(savedUser.getId());
        savedUser.setLastUpdatedBy(savedUser.getId());
        return userDAO.saveAndFlush(savedUser);
    }
}
