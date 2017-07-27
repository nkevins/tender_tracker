package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.RoleUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by andy on 22/7/2017.
 */
@Repository
public interface RoleUserDAO extends JpaRepository<RoleUser, Integer> {

    @Query("select distinct r.roleId from RoleUser r where r.userId =?1 and r.companyId =?2 and r.roleId =?3")
    Integer findUserRoleId(int userId, int companyId, int roleId);

    @Query("select r from RoleUser r where r.id=?1")
    RoleUser findUserRoleById(int id);
}
