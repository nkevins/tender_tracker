package com.chlorocode.tendertracker.service;

import com.chlorocode.tendertracker.constants.TTConstants;
import com.chlorocode.tendertracker.dao.*;
import com.chlorocode.tendertracker.dao.entity.*;
import com.chlorocode.tendertracker.exception.ApplicationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * Service implementation of TenderItemService.
 */
@Service
public class TenderItemServiceImpl implements TenderItemService {

    private TenderItemDAO tenderItemDAO;
    private TenderSubscriptionService tenderSubscriptionService;

    /**
     * Constructor.
     *
     * @param tenderItemDAO TenderItemDAO
     */
    @Autowired
    public TenderItemServiceImpl(TenderItemDAO tenderItemDAO, TenderSubscriptionService tenderSubscriptionService) {
        this.tenderItemDAO = tenderItemDAO;
        this.tenderSubscriptionService=tenderSubscriptionService;
    }

    @Override
    public TenderItem findTenderItemById(int id) {
        return tenderItemDAO.findOne(id);
    }

    @Override
    public TenderItem addTenderItem(TenderItem tenderItem) {
        if (tenderItem.getQuantity() < 0) {
            throw new ApplicationException("Tender Item Quantity must be greater than 0");
        }

        return tenderItemDAO.save(tenderItem);
    }

    @Override
    public TenderItem updateTenderItem(TenderItem tenderItem) {
        if (tenderItem.getQuantity() < 0) {
            throw new ApplicationException("Tender Item Quantity must be greater than 0");
        }

        tenderItem = tenderItemDAO.save(tenderItem);
        tenderSubscriptionService.sendBookmarkNoti(tenderItem.getTender(), TTConstants.UPDATE_TENDER);

        return tenderItem;
    }

    @Override
    public void removeTenderItem(int tenderItemId) {
        tenderItemDAO.delete(tenderItemId);
    }

    @Override
    @Transactional
    public void moveUpTenderItem(int tenderItemId, int tenderId) {
        TenderItem tenderItem = tenderItemDAO.findOne(tenderItemId);
        int currentOrder = tenderItem.getSort();

        TenderItem tenderItemReplaced = tenderItemDAO.getTenderItemBySort(tenderId, currentOrder - 1);

        tenderItem.setSort(currentOrder - 1);
        tenderItemReplaced.setSort(currentOrder);

        tenderItemDAO.save(tenderItem);
        tenderItemDAO.save(tenderItemReplaced);
    }

    @Override
    @Transactional
    public void moveDownTenderItem(int tenderItemId, int tenderId) {
        TenderItem tenderItem = tenderItemDAO.findOne(tenderItemId);
        int currentOrder = tenderItem.getSort();

        TenderItem tenderItemReplaced = tenderItemDAO.getTenderItemBySort(tenderId, currentOrder + 1);

        tenderItem.setSort(currentOrder + 1);
        tenderItemReplaced.setSort(currentOrder);

        tenderItemDAO.save(tenderItem);
        tenderItemDAO.save(tenderItemReplaced);
    }
}
