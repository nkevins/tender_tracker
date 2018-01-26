package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * This DAO is used for User.
 */
@Repository
public interface UserDAO extends JpaRepository<User, Integer> {

    /**
     * This method is used to find user by email.
     *
     * @param email email address of user
     * @return Optional
     * @see User
     */
    Optional<User> findOneByEmail(String email);

}
