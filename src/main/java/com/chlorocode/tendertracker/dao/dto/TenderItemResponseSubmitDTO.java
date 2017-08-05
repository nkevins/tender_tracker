package com.chlorocode.tendertracker.dao.dto;

import com.chlorocode.tendertracker.dao.entity.TenderItem;

public class TenderItemResponseSubmitDTO {

    private TenderItem item;
    private Double quotedPrice;
    private int itemId;

    public TenderItem getItem() {
        return item;
    }

    public void setItem(TenderItem item) {
        this.item = item;
    }

    public Double getQuotedPrice() {
        return quotedPrice;
    }

    public void setQuotedPrice(Double quotedPrice) {
        this.quotedPrice = quotedPrice;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }
}
