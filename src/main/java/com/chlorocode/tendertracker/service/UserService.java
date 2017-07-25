package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.entity.RoleUser;
import com.chlorocode.tendertracker.dao.entity.User;


import java.util.List;

public interface UserService {

    User create(User user);

    User findById(int id);

    RoleUser addUserRole(RoleUser role);

    Integer findUserRoleId(int userId,int companyId,int roleId);

    User getUserRoleByUserIdRoleId(int userId, int roleId);
}
