package com.wedding.model;

public class Review {
    private String id;
    private String customerId;
    private String vendorId;
    private String rating;
    private String comment;

    public Review() {
    }

    public Review(String id, String customerId, String vendorId, String rating, String comment) {
        this.id = id;
        this.customerId = customerId;
        this.vendorId = vendorId;
        this.rating = rating;
        this.comment = comment;
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

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
