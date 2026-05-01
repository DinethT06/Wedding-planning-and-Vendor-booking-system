package com.wedding.model;

public class VerifiedReview extends Review {
    private String verifiedBookingId;

    public VerifiedReview(String id, String customerId, String vendorId, String rating, String comment, String verifiedBookingId) {
        super(id, customerId, vendorId, rating, comment);
        this.verifiedBookingId = verifiedBookingId;
    }

    public String getVerifiedBookingId() {
        return verifiedBookingId;
    }

    public void setVerifiedBookingId(String verifiedBookingId) {
        this.verifiedBookingId = verifiedBookingId;
    }

    public String displayReview() {
        return "Verified Review (" + verifiedBookingId + "): " + getRating() + " stars";
    }
}
