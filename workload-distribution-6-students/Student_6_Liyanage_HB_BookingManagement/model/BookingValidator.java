package com.wedding.model;

public abstract class BookingValidator {
    public boolean hasRequiredIds(Booking booking) {
        return booking != null
            && !booking.getCustomerId().isBlank()
            && !booking.getVendorId().isBlank()
            && !booking.getPackageId().isBlank();
    }

    public abstract boolean isValidBookingDate(String bookingDate);
}
