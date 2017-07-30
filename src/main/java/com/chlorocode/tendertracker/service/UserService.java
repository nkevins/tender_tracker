package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.entity.User;

import java.util.Optional;

public interface UserService {

    User create(User user);

    User findById(int id);

    Optional<User> findByEmail(String email);
}
