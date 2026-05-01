package com.wedding.model;

public class Vendor {
    private String id;
    private String name;
    private String serviceType;
    private String phone;
    private String email;
    private String priceRange;
    private String availability;
    private String status;

    public Vendor() {
    }

    public Vendor(String id, String name, String serviceType, String phone, String email, String priceRange, String availability, String status) {
        this.id = id;
        this.name = name;
        this.serviceType = serviceType;
        this.phone = phone;
        this.email = email;
        this.priceRange = priceRange;
        this.availability = availability;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPriceRange() {
        return priceRange;
    }

    public void setPriceRange(String priceRange) {
        this.priceRange = priceRange;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String displayDetails() {
        return "Vendor: " + name + " | Service: " + serviceType + " | Price: " + priceRange + " | Status: " + status;
    }

    public String toFileRecord() {
        return String.join("|", id, name, serviceType, phone, email, priceRange, availability, status);
    }

    public static Vendor fromFileRecord(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 8) {
            return null;
        }
        return new Vendor(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6], parts[7]);
    }
}
