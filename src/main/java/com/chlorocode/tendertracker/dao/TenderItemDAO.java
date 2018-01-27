package com.chlorocode.tendertracker.dao;

import com.chlorocode.tendertracker.dao.entity.TenderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * This DAO is used for Tender Item.
 */
@Repository
public interface TenderItemDAO extends JpaRepository<TenderItem, Integer> {

    /**
     * This method is used to find the TenderItem by tenderId and sort.
     *
     * @param tenderId unique identifier of tender
     * @param sort sort pattern code
     * @return TenderItem
     * @see TenderItem
     */
    @Query(value = "SELECT ti FROM TenderItem ti JOIN ti.tender t WHERE t.id = ?1 AND ti.sort = ?2")
    TenderItem getTenderItemBySort(int tenderId, int sort);
}
