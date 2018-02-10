package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.constants.TTConstants;
import com.chlorocode.tendertracker.dao.UserDAO;
import com.chlorocode.tendertracker.dao.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

/**
 * Service class to track login attempt.
 */
@Service
public class LoginAttemptService {

    private UserDAO userDAO;

    /**
     * Constructor.
     *
     * @param userDAO UserDAO
     */
    @Autowired
    public LoginAttemptService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * This method is to reset fail count once login is successful.
     *
     * @param key user email
     */
    public void loginSucceeded(String key) {
        Optional<User> u = userDAO.findOneByEmail(key);
        if (u.isPresent()) {
            User user = u.get();
            user.setStatus(TTConstants.ACCOUNT_STATIC_ACTIVE);
            user.setFailCount(0);
            user.setLastUpdatedDate(new Date());
            user.setLastUpdatedBy(user.getId());

            userDAO.saveAndFlush(user);
        }
    }

    /**
     * This method is used to increment fail count once login is failed.
     *
     * @param key user email
     */
    public void loginFailed(String key) {
        Optional<User> u = userDAO.findOneByEmail(key);
        if (u.isPresent()) {
            User user = u.get();
            int failCount = user.getFailCount() + 1;
            if (failCount >= TTConstants.MAX_ATTEMPT) {
                user.setStatus(TTConstants.ACCOUNT_STATIC_LOCK);
            }
            user.setFailCount(failCount);
            user.setLastUpdatedDate(new Date());
            user.setLastUpdatedBy(user.getId());

            userDAO.saveAndFlush(user);
        }
    }
}