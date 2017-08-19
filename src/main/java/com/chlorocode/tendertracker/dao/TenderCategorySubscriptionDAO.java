package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.TenderCategorySubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TenderCategorySubscriptionDAO extends JpaRepository<TenderCategorySubscription, Integer> {

    @Modifying
    @Query("delete from TenderCategorySubscription s where s.user.id = ?1")
    void removeExistingSubscription(int userId);

    @Query("select s from TenderCategorySubscription s where s.user.id = ?1")
    List<TenderCategorySubscription> findUserSubscription(int userId);

    @Query("select s from TenderCategorySubscription s where s.tenderCategory.id = ?1")
    List<TenderCategorySubscription> findUserSubscriptionByTenderCategory(int categoryId);
}
