/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laundryyuk.model;

/**
 *
 * @author gandisuastika
 */

import java.sql.Timestamp;

public class Order {
    private String orderId;
    private String customerId; // Foreign Key ke Customer
    private String driverId;   // Foreign Key ke Driver (Bisa null)
    private Timestamp orderDate;
    private OrderStatus status; // Menggunakan Enum yang baru dibuat
    private String serviceType;
    private double totalWeight;
    private double totalPrice;
    private Timestamp pickupSchedule;
    private String pickupAddress;
    private String notes;
    private boolean isPaid;
    private String paymentProof;

    public Order() {
    }

    // Constructor Lengkap
    public Order(String orderId, String customerId, String driverId, Timestamp orderDate, 
                 OrderStatus status, String serviceType, double totalWeight, double totalPrice, 
                 Timestamp pickupSchedule, String pickupAddress, String notes, boolean isPaid) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.driverId = driverId;
        this.orderDate = orderDate;
        this.status = status;
        this.serviceType = serviceType;
        this.totalWeight = totalWeight;
        this.totalPrice = totalPrice;
        this.pickupSchedule = pickupSchedule;
        this.pickupAddress = pickupAddress;
        this.notes = notes;
        this.isPaid = isPaid;
    }

    // --- Getter dan Setter ---

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getDriverId() { return driverId; }
    public void setDriverId(String driverId) { this.driverId = driverId; }

    public Timestamp getOrderDate() { return orderDate; }
    public void setOrderDate(Timestamp orderDate) { this.orderDate = orderDate; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    
    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }

    public double getTotalWeight() { return totalWeight; }
    public void setTotalWeight(double totalWeight) { this.totalWeight = totalWeight; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public Timestamp getPickupSchedule() { return pickupSchedule; }
    public void setPickupSchedule(Timestamp pickupSchedule) { this.pickupSchedule = pickupSchedule; }
    
    public String getPickupAddress() { return pickupAddress; }
    public void setPickupAddress(String pickupAddress) { this.pickupAddress = pickupAddress; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public boolean isPaid() { return isPaid; }
    public void setPaid(boolean paid) { isPaid = paid; }
    
    public String getPaymentProof() {
        return paymentProof;
    }

    public void setPaymentProof(String paymentProof) {
        this.paymentProof = paymentProof;
    }
}