package com.java1.fullsail.dwtools.models;

import java.io.Serializable;

public class PaintInfoModel implements Serializable{
    String noOfRooms,roomSize,priceOfPaint,roomCost,amountGallons,totalCostPaint,hrsRequired,laborFee,total;

    public PaintInfoModel(String noOfRooms, String roomSize, String priceOfPaint, String roomCost, String amountGallons, String totalCostPaint, String hrsRequired, String laborFee, String total) {
        this.noOfRooms = noOfRooms;
        this.roomSize = roomSize;
        this.priceOfPaint = priceOfPaint;
        this.roomCost = roomCost;
        this.amountGallons = amountGallons;
        this.totalCostPaint = totalCostPaint;
        this.hrsRequired = hrsRequired;
        this.laborFee = laborFee;
        this.total = total;
    }

    public String getNoOfRooms() {
        return noOfRooms;
    }

    public void setNoOfRooms(String noOfRooms) {
        this.noOfRooms = noOfRooms;
    }

    public String getRoomSize() {
        return roomSize;
    }

    public void setRoomSize(String roomSize) {
        this.roomSize = roomSize;
    }

    public String getPriceOfPaint() {
        return priceOfPaint;
    }

    public void setPriceOfPaint(String priceOfPaint) {
        this.priceOfPaint = priceOfPaint;
    }

    public String getRoomCost() {
        return roomCost;
    }

    public void setRoomCost(String roomCost) {
        this.roomCost = roomCost;
    }

    public String getAmountGallons() {
        return amountGallons;
    }

    public void setAmountGallons(String amountGallons) {
        this.amountGallons = amountGallons;
    }

    public String getTotalCostPaint() {
        return totalCostPaint;
    }

    public void setTotalCostPaint(String totalCostPaint) {
        this.totalCostPaint = totalCostPaint;
    }

    public String getHrsRequired() {
        return hrsRequired;
    }

    public void setHrsRequired(String hrsRequired) {
        this.hrsRequired = hrsRequired;
    }

    public String getLaborFee() {
        return laborFee;
    }

    public void setLaborFee(String laborFee) {
        this.laborFee = laborFee;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
