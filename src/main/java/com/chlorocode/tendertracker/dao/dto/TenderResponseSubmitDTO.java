package com.chlorocode.tendertracker.dao.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedList;
import java.util.List;

public class TenderResponseSubmitDTO {
    private int tenderId;
    private List<MultipartFile> attachments;
    private List<TenderItemResponseSubmitDTO> items;

    public TenderResponseSubmitDTO() {
        attachments = new LinkedList<>();
        items = new LinkedList<>();
    }

    public int getTenderId() {
        return tenderId;
    }

    public void setTenderId(int tenderId) {
        this.tenderId = tenderId;
    }

    public List<MultipartFile> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<MultipartFile> attachments) {
        this.attachments = attachments;
    }

    public List<TenderItemResponseSubmitDTO> getItems() {
        return items;
    }

    public void setItems(List<TenderItemResponseSubmitDTO> items) {
        this.items = items;
    }

    public void addTenderItem(TenderItemResponseSubmitDTO item) {
        this.items.add(item);
    }
}
