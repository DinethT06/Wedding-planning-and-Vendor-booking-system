package com.wedding.model;

public class LuxuryPackage extends WeddingPackage {
    private String premiumServices;

    public LuxuryPackage(String id, String name, String category, String description, String price, String status, String premiumServices) {
        super(id, name, category, description, price, status);
        this.premiumServices = premiumServices;
    }

    public String getPremiumServices() {
        return premiumServices;
    }

    public void setPremiumServices(String premiumServices) {
        this.premiumServices = premiumServices;
    }

    public String displayPackageType() {
        return "Luxury Package: " + getName() + " - " + premiumServices;
    }
}
