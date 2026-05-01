package com.wedding.model;

public class ServiceVendor extends Vendor {
    private String serviceArea;

    public ServiceVendor(String id, String name, String serviceType, String phone, String email, String priceRange, String availability, String status, String serviceArea) {
        super(id, name, serviceType, phone, email, priceRange, availability, status);
        this.serviceArea = serviceArea;
    }

    public String displayVendorType() {
        return "Service Vendor: " + getName() + " | Area: " + serviceArea;
    }
}
