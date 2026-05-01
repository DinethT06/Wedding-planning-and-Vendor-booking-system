package com.wedding.model;

public class PremiumCustomer extends Customer {
    private String membershipLevel;

    public PremiumCustomer(String id, String name, String email, String phone, String weddingDate, String address, String membershipLevel) {
        super(id, name, email, phone, weddingDate, address);
        this.membershipLevel = membershipLevel;
    }

    public String bookingPrivilege() {
        return "Premium customer can access priority booking support.";
    }

    public String getMembershipLevel() {
        return membershipLevel;
    }
}
