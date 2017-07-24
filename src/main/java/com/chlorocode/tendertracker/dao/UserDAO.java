package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDAO extends JpaRepository<User, Integer> {

    Optional<User> findOneByEmail(String email);
    @Query("select u from User u join u.userRoles ur join ur.role r where u.id=?1 and r.id=?2")
    User getUserRoleByUserIdRoleId(int userId, int roleId);

}
