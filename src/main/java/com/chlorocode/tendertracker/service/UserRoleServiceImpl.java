package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.UserRoleDAO;
import com.chlorocode.tendertracker.dao.entity.RoleUser;
import com.chlorocode.tendertracker.dao.entity.User;
import com.chlorocode.tendertracker.dao.entity.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by andy on 27/7/2017.
 */
@Service
public class UserRoleServiceImpl implements UserRoleService {

    private UserRoleDAO usrRoleDao;

    @Autowired
    public UserRoleServiceImpl(UserRoleDAO usrRoleDao){
        this.usrRoleDao = usrRoleDao;
    }

    @Override
    public UserRole findUserRoleById(int id) {
        return usrRoleDao.findOne(id);
    }
}
