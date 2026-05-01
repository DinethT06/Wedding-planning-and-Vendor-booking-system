package com.wedding.model;

public class StandardBooking extends Booking {
    public StandardBooking(String id, String customerId, String vendorId, String packageId, String bookingDate, String status) {
        super(id, customerId, vendorId, packageId, bookingDate, status);
    }

    public String processBooking() {
        return "Standard booking processed for date: " + getBookingDate();
    }
}
