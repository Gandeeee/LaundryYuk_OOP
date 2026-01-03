/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laundryyuk.model;

/**
 *
 * @author gandisuastika
 */

public class User {
    private String userId;
    private String email;
    private String passwordHash;
    private String phoneNumber;
    private String role; 

    // 1. Constructor Kosong (Ini yang terbaca oleh error Anda saat ini)
    public User() {
    }
    
    public User(String userId, String email, String passwordHash, String phoneNumber, String role) {
        this.userId = userId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }

    // Getter dan Setter
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
