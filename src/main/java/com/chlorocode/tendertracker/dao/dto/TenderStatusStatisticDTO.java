package com.chlorocode.tendertracker.dao.dto;

/**
 * Data transfer object for tender status statistic.
 */
public class TenderStatusStatisticDTO {
    private String label;
    private int value;

    public TenderStatusStatisticDTO() { }

    public TenderStatusStatisticDTO(String label, int value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
