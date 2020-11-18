package com.oufar.ems.Model;

public class Info {

    private String plateId;
    private String storeId;
    private String storeName;
    private String price;
    private String number;

    public Info(String plateId, String storeId, String storeName, String price, String number) {
        this.plateId = plateId;
        this.storeId = storeId;
        this.storeName = storeName;
        this.price = price;
        this.number = number;
    }

    public Info() {
    }

    public String getPlateId() {
        return plateId;
    }

    public void setPlateId(String plateId) {
        this.plateId = plateId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
