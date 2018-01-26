package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.TenderCategorySubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This DAO used for TenderCategorySubscription.
 */
@Repository
public interface TenderCategorySubscriptionDAO extends JpaRepository<TenderCategorySubscription, Integer> {

    /**
     * This method is used to delete the existing subscription by userId.
     *
     * @param userId unique identifier of user
     */
    @Modifying
    @Query("delete from TenderCategorySubscription s where s.user.id = ?1")
    void removeExistingSubscription(int userId);

    /**
     * This method is used to find the user subscription by userId.
     *
     * @param userId unique identifier of user
     * @return List
     * @see TenderCategorySubscription
     */
    @Query("select s from TenderCategorySubscription s where s.user.id = ?1")
    List<TenderCategorySubscription> findUserSubscription(int userId);

    /**
     * This method is used to find the user subscription by categoryId.
     *
     * @param categoryId unique identifier of category
     * @return List
     * @see TenderCategorySubscription
     */
    @Query("select s from TenderCategorySubscription s where s.tenderCategory.id = ?1")
    List<TenderCategorySubscription> findUserSubscriptionByTenderCategory(int categoryId);
}
