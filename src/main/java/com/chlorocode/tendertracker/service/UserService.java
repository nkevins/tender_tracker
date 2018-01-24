package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.entity.User;

import java.util.Optional;

/**
 * Service interface of user.
 */
public interface UserService {

    /**
     * This method is used to create a new user.
     *
     * @param user user object
     * @return User
     */
    User create(User user);

    /**
     * This method is used to find a user by id.
     *
     * @param id unique identifier of the user
     * @return User
     */
    User findById(int id);

    /**
     * This method is used to send password reset pin to user via email.
     *
     * @param email user email address
     * @return String
     */
    String sendPasswordResetPIN(String email);

    /**
     * This method is used to find user by email.
     *
     * @param email user email address
     * @return User
     */
    Optional<User> findByEmail(String email);

    /**
     * This method is used to validate PIN sent to user.
     *
     * @param email user email address
     * @param pin pin number
     * @return boolean
     */
    boolean isPinValid(String email, String pin);

    /**
     * This method is used to update user password.
     *
     * @param email user email address
     * @param newPassword new password
     * @return User
     */
    User updatePassword(String email, String newPassword);

    /**
     * This method is used to update user profile.
     *
     * @param user user object
     * @return User
     */
    User updateUserProfile(User user);

    /**
     * This method is used to validate NRIC number.
     *
     * @param nric NRIC number
     * @return boolean
     */
    boolean isNRICValid(String nric);

    /**
     * This method is used to validate FIN number.
     * @param fin FIN number
     * @return boolean
     */
    boolean isFINValid(String fin);

}
