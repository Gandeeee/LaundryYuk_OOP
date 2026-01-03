/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laundryyuk.model;

/**
 *
 * @author gandisuastika
 */
public class Admin extends User {
    private String adminName;
    private String employeeId;

    public Admin() {
        super();
    }

    public Admin(String userId, String email, String passwordHash, String phoneNumber, 
                 String adminName, String employeeId) {
        super(userId, email, passwordHash, phoneNumber, "ADMIN");
        this.adminName = adminName;
        this.employeeId = employeeId;
    }

    // --- Getter dan Setter ---

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }
}