/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laundryyuk.model;

/**
 *
 * @author gandisuastika
 */

public class Rating {
    private String ratingId;
    private String orderId; 
    private int score;      
    private String reviewText;

    // Constructor Kosong (Wajib untuk beberapa framework)
    public Rating() {
    }

    // CONSTRUCTOR 1: LENGKAP (Dipakai saat membaca dari Database)
    public Rating(String ratingId, String orderId, int score, String reviewText) {
        this.ratingId = ratingId;
        this.orderId = orderId;
        this.score = score;
        this.reviewText = reviewText;
    }

    // CONSTRUCTOR 2: KHUSUS INPUT BARU (Dipakai Controller)
    // Controller hanya mengirim 3 data, ID dibuat otomatis di sini!
    public Rating(String orderId, int score, String reviewText) {
        // Generate ID Unik (Format: RTG-AngkaWaktu)
        this.ratingId = "RTG-" + System.currentTimeMillis(); 
        
        this.orderId = orderId;
        this.score = score;
        this.reviewText = reviewText;
    }

    // --- Getter dan Setter ---
    public String getRatingId() { return ratingId; }
    public void setRatingId(String ratingId) { this.ratingId = ratingId; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public String getReviewText() { return reviewText; }
    public void setReviewText(String reviewText) { this.reviewText = reviewText; }
}