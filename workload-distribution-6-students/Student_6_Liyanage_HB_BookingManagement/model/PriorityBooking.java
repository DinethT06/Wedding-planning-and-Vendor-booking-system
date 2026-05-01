package com.wedding.model;

public class PriorityBooking extends Booking {
    private String priorityNote;

    public PriorityBooking(String id, String customerId, String vendorId, String packageId, String bookingDate, String status, String priorityNote) {
        super(id, customerId, vendorId, packageId, bookingDate, status);
        this.priorityNote = priorityNote;
    }

    public String processBooking() {
        return "Priority booking processed with note: " + priorityNote;
    }
}
