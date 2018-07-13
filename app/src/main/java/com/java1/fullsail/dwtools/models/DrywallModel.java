package com.java1.fullsail.dwtools.models;

import java.io.Serializable;

public class DrywallModel implements Serializable{

    String areaTotal,excludedArea,wallTotal,ceilTotal,slopingTotal;

    public DrywallModel(String areaTotal, String excludedArea, String wallTotal, String ceilTotal, String slopingTotal) {
        this.areaTotal = areaTotal;
        this.excludedArea = excludedArea;
        this.wallTotal = wallTotal;
        this.ceilTotal = ceilTotal;
        this.slopingTotal = slopingTotal;
    }

    public String getAreaTotal() {
        return areaTotal;
    }

    public void setAreaTotal(String areaTotal) {
        this.areaTotal = areaTotal;
    }

    public String getExcludedArea() {
        return excludedArea;
    }

    public void setExcludedArea(String excludedArea) {
        this.excludedArea = excludedArea;
    }

    public String getWallTotal() {
        return wallTotal;
    }

    public void setWallTotal(String wallTotal) {
        this.wallTotal = wallTotal;
    }

    public String getCeilTotal() {
        return ceilTotal;
    }

    public void setCeilTotal(String ceilTotal) {
        this.ceilTotal = ceilTotal;
    }

    public String getSlopingTotal() {
        return slopingTotal;
    }

    public void setSlopingTotal(String slopingTotal) {
        this.slopingTotal = slopingTotal;
    }
}
