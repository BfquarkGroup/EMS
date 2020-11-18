package com.oufar.ems.Model;

public class Favorite {

    private String id;
    private String storeId;
    private String phone;
    private String name;
    private String address;
    private String description;
    private String imageURL;
    private String email;
    private String status;

    public Favorite(String id, String storeId, String phone, String name, String address, String description, String imageURL, String status, String email) {
        this.id = id;
        this.storeId = storeId;
        this.phone = phone;
        this.name = name;
        this.address = address;
        this.description = description;
        this.imageURL = imageURL;
        this.status = status;
        this.email = email;
    }

    public Favorite() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

