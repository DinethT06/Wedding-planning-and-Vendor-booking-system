package com.wedding.model;

public class ProductVendor extends Vendor {
    private String productCategory;

    public ProductVendor(String id, String name, String serviceType, String phone, String email, String priceRange, String availability, String status, String productCategory) {
        super(id, name, serviceType, phone, email, priceRange, availability, status);
        this.productCategory = productCategory;
    }

    public String displayVendorType() {
        return "Product Vendor: " + getName() + " | Category: " + productCategory;
    }
}
