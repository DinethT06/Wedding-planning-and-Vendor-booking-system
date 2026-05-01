package com.wedding.model;

public class Admin extends User {
    private String role;
    private String username;

    public Admin() {
    }

    public Admin(String id, String name, String email, String phone, String role, String username) {
        super(id, name, email, phone);
        this.role = role;
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String displayDetails() {
        return "Admin: " + getName() + " | Role: " + role + " | Username: " + username;
    }
}
