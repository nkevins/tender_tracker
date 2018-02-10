package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.dao.entity.TenderItem;

/**
 * Service interface for tender item.
 */
public interface TenderItemService {

    /**
     * This method is used to get tender item by id.
     *
     * @param id unique identifier of the tender item
     * @return Tender Item
     */
    TenderItem findTenderItemById(int id);

    /**
     * This method is used to add tender item.
     *
     * @param tenderItem tender item to be saved
     * @return TenderItem
     */
    TenderItem addTenderItem(TenderItem tenderItem);

    /**
     * This method is used to update tender item.
     *
     * @param tenderItem tender item to be updated
     * @return TenderItem
     */
    TenderItem updateTenderItem(TenderItem tenderItem);

    /**
     * This method is used to remove tender item.
     *
     * @param tenderItemId unique identifier of the tender item
     */
    void removeTenderItem(int tenderItemId);

    /**
     * This method is used to move up tender item order.
     *
     * @param tenderItemId unique identifier of the tender item
     * @param tenderId unique identifier of the tender
     */
    void moveUpTenderItem(int tenderItemId, int tenderId);

    /**
     * This method is used to move down tender item order.
     *
     * @param tenderItemId unique identifier of the tender item
     * @param tenderId unique identifier of the tender
     */
    void moveDownTenderItem(int tenderItemId, int tenderId);

}
