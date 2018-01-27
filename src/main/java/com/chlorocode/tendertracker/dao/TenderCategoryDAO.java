package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.TenderCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This DAO is used for TenderCategory.
 */
@Repository
public interface TenderCategoryDAO extends JpaRepository<TenderCategory, Integer> {

    /**
     * This method is used to get all tender categories.
     *
     * @return List
     * @see TenderCategory
     */
    @Query("select c from TenderCategory c order by c.name")
    List<TenderCategory> getAllTenderCategories();

    /**
     * This method is used to get tender category by id.
     *
     * @param id unique identifier of TenderCategory
     * @return TenderCategory
     * @see TenderCategory
     */
    @Query("select c from TenderCategory c where c.id = ?1")
    TenderCategory getTenderCategoryById(int id);

}
