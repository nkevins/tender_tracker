package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.entity.RoleUser;
import com.chlorocode.tendertracker.dao.entity.User;

import java.util.Optional;

public interface UserService {

    User create(User user);

    User findById(int id);

    String sendPasswordResetPIN(String email);

    Optional<User> findByEmail(String email);

    boolean isPinValid(String email, String pin);

    User updatePassword(String email, String newPassword);

    User updateUserProfile(User user);

}
