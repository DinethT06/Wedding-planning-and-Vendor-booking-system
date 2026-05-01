package com.wedding.model;

public class NormalReview extends Review {
    public NormalReview(String id, String customerId, String vendorId, String rating, String comment) {
        super(id, customerId, vendorId, rating, comment);
    }

    public String displayReview() {
        return "Review Rating: " + getRating() + " | Comment: " + getComment();
    }
}
