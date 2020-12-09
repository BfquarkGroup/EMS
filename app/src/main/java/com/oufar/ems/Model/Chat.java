package com.oufar.ems.Model;

public class Chat {

    private String id, clientId, storeId, clientEmail, storeEmail, clientImage, storeImage,
            message, clientStatus, storeStatus, clientRate, storeRate;

    public Chat(String id, String clientId, String storeId, String clientEmail, String storeEmail, String clientImage, String storeImage, String message,
                String clientStatus, String storeStatus, String clientRate, String storeRate) {
        this.id = id;
        this.clientId = clientId;
        this.storeId = storeId;
        this.clientEmail = clientEmail;
        this.storeEmail = storeEmail;
        this.clientImage = clientImage;
        this.storeImage = storeImage;
        this.message = message;
        this.clientStatus = clientStatus;
        this.storeStatus = storeStatus;
        this.clientRate = clientRate;
        this.storeRate = storeRate;
    }

    public Chat() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public String getStoreEmail() {
        return storeEmail;
    }

    public void setStoreEmail(String storeEmail) {
        this.storeEmail = storeEmail;
    }

    public String getClientImage() {
        return clientImage;
    }

    public void setClientImage(String clientImage) {
        this.clientImage = clientImage;
    }

    public String getStoreImage() {
        return storeImage;
    }

    public void setStoreImage(String storeImage) {
        this.storeImage = storeImage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getClientStatus() {
        return clientStatus;
    }

    public void setClientStatus(String clientStatus) {
        this.clientStatus = clientStatus;
    }

    public String getStoreStatus() {
        return storeStatus;
    }

    public void setStoreStatus(String storeStatus) {
        this.storeStatus = storeStatus;
    }

    public String getClientRate() {
        return clientRate;
    }

    public void setClientRate(String clientRate) {
        this.clientRate = clientRate;
    }

    public String getStoreRate() {
        return storeRate;
    }

    public void setStoreRate(String storeRate) {
        this.storeRate = storeRate;
    }
}
