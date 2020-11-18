package com.oufar.ems.Model;

public class Notification {

    private String id;
    private String clientEmail;
    private String storeEmail;
    private String deliveryGuyEmail;
    private String title;
    private String body;

    public Notification(String id, String clientEmail, String storeEmail, String deliveryGuyEmail, String title, String body) {
        this.id = id;
        this.clientEmail = clientEmail;
        this.storeEmail = storeEmail;
        this.deliveryGuyEmail = deliveryGuyEmail;
        this.title = title;
        this.body = body;
    }

    public Notification() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getDeliveryGuyEmail() {
        return deliveryGuyEmail;
    }

    public void setDeliveryGuyEmail(String deliveryGuyEmail) {
        this.deliveryGuyEmail = deliveryGuyEmail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
