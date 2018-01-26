package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.Company;
import com.chlorocode.tendertracker.dao.entity.Role;
import com.chlorocode.tendertracker.dao.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This DAO is used for UserRole.
 */
@Repository
public interface UserRoleDAO extends JpaRepository<UserRole, Integer> {

    /**
     * This method is used for finding role by roleId.
     *
     * @param roleId unique identifier of role
     * @return Role
     * @see Role
     */
    @Query("select r from Role r where r.id = ?1")
    Role findRoleById(int roleId);

    /**
     * This method is used to find the role by userId and companyId.
     *
     * @param userId unique identifier of user
     * @param companyId unique identifier of company
     * @return List
     * @see Role
     */
    @Query("select r from UserRole ur join ur.role r where ur.user.id = ?1 and ur.company.id = ?2")
    List<Role> findRoleForCompanyUser(int userId, int companyId);

    /**
     * This method is used to find the UserRole by userId and companyId.
     *
     * @param userId unique identifier of user
     * @param companyId unique identifier of company
     * @return List
     * @see UserRole
     */
    @Query("select ur from UserRole ur where ur.user.id = ?1 and ur.company.id = ?2")
    List<UserRole> findUserRoleByUserAndCompany(int userId, int companyId);

    /**
     * This method is used to find the name of UserRole by userId.
     *
     * @param userId unique identifier of user
     * @return List
     */
    @Query("select distinct r.name from UserRole ur join ur.role r where ur.user.id = ?1")
    List<String> findUniqueUserRole(int userId);

    /**
     * This method is used to find administrator company for user.
     *
     * @param userId unique identifier of user
     * @return List
     * @see Company
     */
    @Query("select distinct c from UserRole ur join ur.company c where ur.user.id = ?1 and c.status = 1 order by c.name")
    List<Company> findUserAdministeredCompany(int userId);

    /**
     * This method is used to find the admin of the company.
     *
     * @param companyId unique identifier of company
     * @return List
     * @see UserRole
     */
    @Query("select ur from UserRole ur where ur.company.id = ?1 and ur.role.id = 2")
    List<UserRole> findCompanyAdminUserRole(int companyId);

    /**
     * This method is used to find the all users of the company.
     *
     * @param companyId unique identifier of company
     * @return List
     * @see UserRole
     */
    @Query("select ur from UserRole ur where ur.company.id = ?1")
    List<UserRole> findUserRoleByCompany(int companyId);
}
