package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.Company;
import com.chlorocode.tendertracker.dao.entity.RoleUser;
import com.chlorocode.tendertracker.dao.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleDAO extends JpaRepository<UserRole, Integer> {

    @Query("select distinct r.name from UserRole ur join ur.role r where ur.user.id = ?1")
    List<String> findUniqueUserRole(int userId);

    @Query("select c from UserRole ur join ur.company c where ur.user.id = ?1 and ur.role.id = 2 and c.status = 1 order by c.name")
    List<Company> findUserAdministeredCompany(int userId);

}
