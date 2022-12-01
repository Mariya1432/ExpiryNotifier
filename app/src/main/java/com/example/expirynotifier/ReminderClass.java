package com.example.expirynotifier;

import java.io.Serializable;

public class ReminderClass implements Serializable {

    public ReminderClass() {
    }

    String uuid = null;
    String productName = null;
    String category = null;
    String imageUrl = null;
    long expiryDate = 0;
    int notifyBefore = 1;
    int alarmId = 0;

    public ReminderClass(String uuid, String productName, String category, String imageUrl, long expiryDate, int notifyBefore, int alarmId) {
        this.uuid = uuid;
        this.productName = productName;
        this.category = category;
        this.imageUrl = imageUrl;
        this.expiryDate = expiryDate;
        this.notifyBefore = notifyBefore;
        this.alarmId = alarmId;
    }


    public String getUuid() {
        return uuid;
    }

    public String getProductName() {
        return productName;
    }

    public String getCategory() {
        return category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public long getExpiryDate() {
        return expiryDate;
    }

    public int getNotifyBefore() {
        return notifyBefore;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setExpiryDate(long expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setNotifyBefore(int notifyBefore) {
        this.notifyBefore = notifyBefore;
    }
}
