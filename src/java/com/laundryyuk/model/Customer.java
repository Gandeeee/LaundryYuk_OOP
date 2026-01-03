/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laundryyuk.model;

/**
 *
 * @author gandisuastika
 */

public class Customer extends User {
    private String customerName;
    private String address;

    public Customer() {
        super(); // Memanggil constructor User
    }

    // Constructor lengkap (Data User + Data Customer)
    public Customer(String userId, String email, String passwordHash, String phoneNumber, 
                    String customerName, String address) {
        // Set data ke Parent (User)
        super(userId, email, passwordHash, phoneNumber, "CUSTOMER");
        // Set data ke diri sendiri (Customer)
        this.customerName = customerName;
        this.address = address;
    }

    // --- Getter dan Setter ---

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}