package com.oufar.ems.Model;

public class UnselectedPlate {

    private String id;
    private String plate;
    private String price;
    private String description;

    public UnselectedPlate(String id, String plate, String price, String description) {
        this.id = id;
        this.plate = plate;
        this.price = price;
        this.description = description;
    }

    public UnselectedPlate() {
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
}
