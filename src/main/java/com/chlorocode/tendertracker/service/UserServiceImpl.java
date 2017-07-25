package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.RoleUserDAO;
import com.chlorocode.tendertracker.dao.UserDAO;
import com.chlorocode.tendertracker.dao.UserRoleDAO;
import com.chlorocode.tendertracker.dao.entity.RoleUser;
import com.chlorocode.tendertracker.dao.entity.User;
import com.chlorocode.tendertracker.exception.ApplicationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private PasswordEncoder passwordEncoder;
    private UserDAO userDAO;
    private RoleUserDAO userRoleDAO;
    private UserRoleDAO usrRoleDao;

    @Autowired
    public UserServiceImpl(UserDAO userDAO, PasswordEncoder passwordEncoder,RoleUserDAO userRoleDAO) {
        this.userDAO = userDAO;
        this.passwordEncoder = passwordEncoder;
        this.userRoleDAO = userRoleDAO;
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
    public RoleUser addUserRole(RoleUser role) {
        return userRoleDAO.save(role);
    }

    @Override
    public Integer findUserRoleId(int userId, int companyId, int roleId) {
        return userRoleDAO.findUserRoleId(userId, companyId,roleId);
    }

    @Override
    public User getUserRoleByUserIdRoleId(int userId, int roleId){
        return userDAO.getUserRoleByUserIdRoleId(userId, roleId);
    }
}
