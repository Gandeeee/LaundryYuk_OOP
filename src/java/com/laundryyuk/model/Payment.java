/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laundryyuk.model;

/**
 *
 * @author gandisuastika
 */

public class Payment {
    private String paymentId;
    private String orderId; // Foreign Key ke Order
    private double amount;
    private String paymentProofUrl;
    private boolean isVerified;

    public Payment() {
    }

    public Payment(String paymentId, String orderId, double amount, String paymentProofUrl, boolean isVerified) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.amount = amount;
        this.paymentProofUrl = paymentProofUrl;
        this.isVerified = isVerified;
    }

    // --- Getter dan Setter ---

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getPaymentProofUrl() { return paymentProofUrl; }
    public void setPaymentProofUrl(String paymentProofUrl) { this.paymentProofUrl = paymentProofUrl; }

    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { isVerified = verified; }
}