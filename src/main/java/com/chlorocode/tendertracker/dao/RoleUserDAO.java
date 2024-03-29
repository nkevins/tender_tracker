package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.RoleUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Created by andy on 22/7/2017.
 * This DAO is used for User Role.
 */
@Repository
public interface RoleUserDAO extends JpaRepository<RoleUser, Integer> {

    /**
     * This method is used to find the user role by userId, companyId and roleId.
     *
     * @param userId unique identifier of user
     * @param companyId unique identifier of company
     * @param roleId unique identifier of role
     * @return Integer
     */
    @Query("select distinct r.roleId from RoleUser r where r.userId =?1 and r.companyId =?2 and r.roleId =?3")
    Integer findUserRoleId(int userId, int companyId, int roleId);

    /**
     * This method is used to find the user role by id.
     *
     * @param id unique identifier of user role
     * @return RoleUser
     * @see RoleUser
     */
    @Query("select r from RoleUser r where r.id=?1")
    RoleUser findUserRoleById(int id);
}
