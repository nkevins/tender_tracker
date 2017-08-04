package com.chlorocode.tendertracker.dao.dto;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by andy on 2/8/2017.
 */
public class EvaluateCriteriaDTO {
    @NotBlank(message = "Please set the criteria")
    private String criteria1;
    private int type1;
    private int type2;
    private int type3;
    private int type4;
    private int type5;


    private String criteria2;

    private String criteria3;

    private String criteria4;

    private String criteria5;

    public int getType2() {
        return type2;
    }

    public void setType2(int type2) {
        this.type2 = type2;
    }

    public int getType3() {
        return type3;
    }

    public void setType3(int type3) {
        this.type3 = type3;
    }

    public int getType4() {
        return type4;
    }

    public void setType4(int type4) {
        this.type4 = type4;
    }

    public int getType5() {
        return type5;
    }

    public void setType5(int type5) {
        this.type5 = type5;
    }

    public String getCriteria2() {
        return criteria2;
    }

    public void setCriteria2(String criteria2) {
        this.criteria2 = criteria2;
    }

    public String getCriteria3() {
        return criteria3;
    }

    public void setCriteria3(String criteria3) {
        this.criteria3 = criteria3;
    }

    public String getCriteria4() {
        return criteria4;
    }

    public void setCriteria4(String criteria4) {
        this.criteria4 = criteria4;
    }

    public String getCriteria5() {
        return criteria5;
    }

    public void setCriteria5(String criteria5) {
        this.criteria5 = criteria5;
    }

    public String getCriteria1() {
        return criteria1;
    }

    public void setCriteria1(String criteria1) {
        this.criteria1 = criteria1;
    }

    public int getType1() {
        return type1;
    }

    public void setType1(int type1) {
        this.type1 = type1;
    }
}
