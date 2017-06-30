package com.chlorocode.tendertracker.dao.entity;

import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public class CurrentUser extends org.springframework.security.core.userdetails.User {

    private User user;

    public CurrentUser(User user, List<GrantedAuthority> roles) {
        super(user.getEmail(), user.getPassword(), roles);
        this.user = user;
    }

    public String getName() {
        return user.getName();
    }
}
