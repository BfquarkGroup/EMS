package com.oufar.ems.Model;

public class Plate {

    private String id;
    private String plate;
    private String price;
    private String description;
    private String imageURL;
    private String storeId;
    private String localeId;
    private String number;
    private String status;
    private String storeEmail;

    public Plate(String id, String plate, String price, String description, String imageURL, String storeId, String localeId, String number, String status, String storeEmail) {
        this.id = id;
        this.plate = plate;
        this.price = price;
        this.description = description;
        this.imageURL = imageURL;
        this.storeId = storeId;
        this.localeId = localeId;
        this.number = number;
        this.status = status;
        this.storeEmail = storeEmail;
    }

    public Plate() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getLocaleId() {
        return localeId;
    }

    public void setLocaleId(String localeId) {
        this.localeId = localeId;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStoreEmail() {
        return storeEmail;
    }

    public void setStoreEmail(String storeEmail) {
        this.storeEmail = storeEmail;
    }
}
