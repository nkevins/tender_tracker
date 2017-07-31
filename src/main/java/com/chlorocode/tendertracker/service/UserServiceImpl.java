package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.RoleUserDAO;
import com.chlorocode.tendertracker.dao.UserDAO;
import com.chlorocode.tendertracker.dao.UserRoleDAO;
import com.chlorocode.tendertracker.dao.entity.User;
import com.chlorocode.tendertracker.exception.ApplicationException;

import com.chlorocode.tendertracker.logging.TTLogger;
import com.chlorocode.tendertracker.service.notification.NotificationService;
import com.chlorocode.tendertracker.service.notification.NotificationServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private PasswordEncoder passwordEncoder;
    private UserDAO userDAO;
    private RoleUserDAO userRoleDAO;
    private UserRoleDAO usrRoleDao;
    private String className;
    @Autowired
    private NotificationService notificationService;

    @Autowired
    public UserServiceImpl(UserDAO userDAO, PasswordEncoder passwordEncoder,RoleUserDAO userRoleDAO) {
        this.userDAO = userDAO;
        this.passwordEncoder = passwordEncoder;
        this.userRoleDAO = userRoleDAO;
        this.className = this.getClass().getName();
    }

    @Override
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

    @Override
    public User findById(int id) {
        return userDAO.findOne(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userDAO.findOneByEmail(email);
    }

    @Override
    public String sendPasswordResetPIN(String email) {
        Optional<User> u = userDAO.findOneByEmail(email);
        if (u.isPresent()) {
            User user = u.get();
            String otp = "TESTING"; // TODO generate OTP.
            user.setConfirmationToken(otp);
            if (notificationService.sendNotification(NotificationServiceImpl.NOTI_MODE.reset_otp, user)) {
                userDAO.save(user);
                return null;
            }
            return "Email cannot send to your email address. Please contact to administrator.";
        }
        return "No account found for that email address.";
    }
}
