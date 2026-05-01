package com.wedding.model;

public class BasicPackage extends WeddingPackage {
    public BasicPackage(String id, String name, String category, String description, String price, String status) {
        super(id, name, category, description, price, status);
    }

    public String displayPackageType() {
        return "Basic Package: " + getName() + " - " + getPrice();
    }
}
