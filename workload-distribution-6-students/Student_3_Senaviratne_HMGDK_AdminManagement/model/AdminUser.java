package com.wedding.model;

public class AdminUser extends User {
    private String role;
    private String permissionLevel;

    public AdminUser(String id, String name, String email, String phone, String role, String permissionLevel) {
        super(id, name, email, phone);
        this.role = role;
        this.permissionLevel = permissionLevel;
    }

    public String getRole() {
        return role;
    }

    public String getPermissionLevel() {
        return permissionLevel;
    }

    @Override
    public String displayDetails() {
        return "Admin User: " + getName() + " | Role: " + role + " | Permission: " + permissionLevel;
    }
}
