package com.wedding.model;

public class RegularCustomer extends Customer {
    public RegularCustomer(String id, String name, String email, String phone, String weddingDate, String address) {
        super(id, name, email, phone, weddingDate, address);
    }

    public String bookingPrivilege() {
        return "Regular customer can create standard wedding bookings.";
    }
}
