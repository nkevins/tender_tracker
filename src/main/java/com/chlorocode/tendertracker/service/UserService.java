package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.entity.User;

public interface UserService {

    User create(User user);

    User findById(int id);

}
