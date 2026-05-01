package com.wedding.model;

public class Booking {
    private String id;
    private String customerId;
    private String vendorId;
    private String packageId;
    private String bookingDate;
    private String status;

    public Booking() {
    }

    public Booking(String id, String customerId, String vendorId, String packageId, String bookingDate, String status) {
        this.id = id;
        this.customerId = customerId;
        this.vendorId = vendorId;
        this.packageId = packageId;
        this.bookingDate = bookingDate;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
