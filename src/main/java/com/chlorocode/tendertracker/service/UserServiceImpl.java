package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.constants.TTConstants;
import com.chlorocode.tendertracker.dao.RoleUserDAO;
import com.chlorocode.tendertracker.dao.UserDAO;
import com.chlorocode.tendertracker.dao.UserRoleDAO;
import com.chlorocode.tendertracker.dao.entity.User;
import com.chlorocode.tendertracker.exception.ApplicationException;

import com.chlorocode.tendertracker.service.notification.NotificationService;
import com.chlorocode.tendertracker.service.notification.NotificationServiceImpl;

import com.chlorocode.tendertracker.utils.DateUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    private PasswordEncoder passwordEncoder;
    private UserDAO userDAO;
    private RoleUserDAO userRoleDAO;
    private UserRoleDAO usrRoleDao;
    private String className;
    private NotificationService notificationService;

    @Autowired
    public UserServiceImpl(UserDAO userDAO, PasswordEncoder passwordEncoder,RoleUserDAO userRoleDAO, NotificationService notificationService) {
        this.userDAO = userDAO;
        this.passwordEncoder = passwordEncoder;
        this.userRoleDAO = userRoleDAO;
        this.className = this.getClass().getName();
        this.notificationService = notificationService;
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
        user = userDAO.saveAndFlush(savedUser);
        if (user != null) {
            Map<String, Object> params = new HashMap<>();
            params.put(TTConstants.PARAM_NAME, user.getName());
            params.put(TTConstants.PARAM_EMAIL, user.getEmail());
            notificationService.sendNotification(NotificationServiceImpl.NOTI_MODE.welcome_message, params);
        }

        return user;
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
    public boolean isPinValid(String email, String pin) {
        Optional<User> u = userDAO.findOneByEmail(email);
        if (u.isPresent()) {
            User user = u.get();
            if (user.getConfirmationToken() != null && user.getConfirmationToken().equals(pin)
                    && user.getTokenExpireDate().after(DateUtility.getCurrentDateTime())
                    ) {
                return true;
            }
        }
        return false;
    }

    @Override
    public User updatePassword(String email, String newPassword) {
        Optional<User> u = userDAO.findOneByEmail(email);
        if (u.isPresent()) {
            User user = u.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setConfirmationToken(null);
            user.setTokenExpireDate(null);
            user.setStatus(TTConstants.ACCOUNT_STATIC_ACTIVE);
            user.setFailCount(0);
            user.setLastUpdatedDate(new Date());
            user.setLastUpdatedBy(user.getId());

            return userDAO.saveAndFlush(user);
        }
        return null;
    }

    @Override
    public User updateUserProfile(User user) {
        return userDAO.save(user);
    }

    @Override
    public String sendPasswordResetPIN(String email) {
        Optional<User> u = userDAO.findOneByEmail(email);
        if (u.isPresent()) {
            User user = u.get();
            String otp = generatePIN(TTConstants.OTP_LENGTH);
            user.setConfirmationToken(otp);
            user.setTokenExpireDate(DateUtility.getFutureDateTime(0, 0, TTConstants.OTP_VALID_HOURS, 0));

            Map<String, Object> params = new HashMap<>();
            params.put(TTConstants.PARAM_NAME, user.getName());
            params.put(TTConstants.PARAM_EMAIL, user.getEmail());
            params.put(TTConstants.PARAM_TOKEN, user.getConfirmationToken());
            notificationService.sendNotification(NotificationServiceImpl.NOTI_MODE.reset_otp, params);
            userDAO.save(user);
            return null;
        }
        return "No account found for that email address.";
    }

    private static String generatePIN(int length) {
        // do not use I, O as looks like 1 and 0
        String pin = TTConstants.EMPTY;
        Random rand = new Random();
        for (int i = 0; i < length; ++i) {
            int r = rand.nextInt(TTConstants.OTP_CHARS.length());
            pin += TTConstants.OTP_CHARS.substring(r, r + 1);
        }
        return pin;
    }

    @Override
    public boolean isNRICValid(String nric) {
        if (nric.trim().length() != 9)
            return false;

        if (!nric.substring(0, 1).equals("S") && !nric.substring(0, 1).equals("T"))
            return false;

        String numberPart = nric.substring(1, 8);
        int num = Integer.parseInt(numberPart);

        // Extract the digits
        int digit7 = num%10; num /= 10;
        int digit6 = num%10; num /= 10;
        int digit5 = num%10; num /= 10;
        int digit4 = num%10; num /= 10;
        int digit3 = num%10; num /= 10;
        int digit2 = num%10; num /= 10;
        int digit1 = num%10;

        int step1 = digit1*2 + digit2*7 + digit3*6 + digit4*5 +
                digit5*4 + digit6*3 + digit7*2;
        int step2 = step1 % 11;
        if (nric.substring(0, 1).equals("T"))
            step2 += 4;

        char code = ' ';
        switch (step2) {
            case 0: code = 'J'; break;
            case 1: code = 'Z'; break;
            case 2: code = 'I'; break;
            case 3: code = 'H'; break;
            case 4: code = 'G'; break;
            case 5: code = 'F'; break;
            case 6: code = 'E'; break;
            case 7: code = 'D'; break;
            case 8: code = 'C'; break;
            case 9: code = 'B'; break;
            case 10: code = 'A';
        }

        if (nric.charAt(8) == code)
            return true;
        else
            return false;
    }

    @Override
    public boolean isFINValid(String fin) {
        if (fin.trim().length() != 9)
            return false;

        if (!fin.substring(0, 1).equals("F") && !fin.substring(0, 1).equals("G"))
            return false;

        String numberPart = fin.substring(1, 8);
        int num = Integer.parseInt(numberPart);

        // Extract the digits
        int digit7 = num%10; num /= 10;
        int digit6 = num%10; num /= 10;
        int digit5 = num%10; num /= 10;
        int digit4 = num%10; num /= 10;
        int digit3 = num%10; num /= 10;
        int digit2 = num%10; num /= 10;
        int digit1 = num%10;

        int step1 = digit1*2 + digit2*7 + digit3*6 + digit4*5 +
                digit5*4 + digit6*3 + digit7*2;
        int step2 = step1 % 11;
        if (fin.substring(0, 1).equals("G"))
            step2 += 4;

        char code = ' ';
        switch (step2) {
            case 0: code = 'X'; break;
            case 1: code = 'W'; break;
            case 2: code = 'U'; break;
            case 3: code = 'T'; break;
            case 4: code = 'R'; break;
            case 5: code = 'Q'; break;
            case 6: code = 'P'; break;
            case 7: code = 'N'; break;
            case 8: code = 'M'; break;
            case 9: code = 'L'; break;
            case 10: code = 'K';
        }

        if (fin.charAt(8) == code)
            return true;
        else
            return false;
    }
}
