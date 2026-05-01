package com.wedding.model;

public class Customer extends User {
    private String weddingDate;
    private String address;

    public Customer() {
    }

    public Customer(String id, String name, String email, String phone, String weddingDate, String address) {
        super(id, name, email, phone);
        this.weddingDate = weddingDate;
        this.address = address;
    }

    public String getWeddingDate() {
        return weddingDate;
    }

    public void setWeddingDate(String weddingDate) {
        this.weddingDate = weddingDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String displayDetails() {
        return "Customer: " + getName() + " | Wedding Date: " + weddingDate + " | Address: " + address;
    }
}
