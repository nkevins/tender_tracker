package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.RoleUserDAO;
import com.chlorocode.tendertracker.dao.UserDAO;
import com.chlorocode.tendertracker.dao.UserRoleDAO;
import com.chlorocode.tendertracker.dao.entity.RoleUser;
import com.chlorocode.tendertracker.dao.entity.User;
import com.chlorocode.tendertracker.exception.ApplicationException;
import com.chlorocode.tendertracker.logging.TTLogger;
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
    private String className;
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

    public boolean updateUserRole(int id, int newRoleId, int modifiedBy){
         RoleUser user = userRoleDAO.findUserRoleById(id);

         if(user == null){
             TTLogger.warn(className,"User role not found.");
             throw new ApplicationException("User role not found");
         }

         try{
             user.setRoleId(newRoleId);
             user.setLastUpdatedBy(modifiedBy);
             user.setLastUpdatedDate(new Date());
             userRoleDAO.save(user);
             return true;
         }catch(Exception ex){
             TTLogger.warn(className,"User role not found. User Id " + user.getUserId() + " ,User Role Id " + user.getRoleId(), ex);
         }
         return false;
    }

    public boolean deleteUserRole(int id){
        try{
            userRoleDAO.delete(id);
            return true;
        }catch(Exception ex){
            TTLogger.warn(className,"failed to delete user. user id " + id, ex);
        }
        return false;
    }
}
