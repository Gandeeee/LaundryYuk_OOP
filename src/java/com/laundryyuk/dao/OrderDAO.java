/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laundryyuk.dao;

/**
 *
 * @author gandisuastika
 */

import com.laundryyuk.config.DatabaseConnection;
import com.laundryyuk.model.Order;
import com.laundryyuk.model.OrderStatus;
import com.laundryyuk.model.Rating;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    // Simpan Pesanan Baru (TIDAK BERUBAH)
    public boolean createOrder(Order order) {
        String sql = "INSERT INTO orders (order_id, customer_id, order_date, status, service_type, total_weight, total_price, pickup_schedule, pickup_address, notes, is_paid) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, order.getOrderId());
            ps.setString(2, order.getCustomerId());
            ps.setTimestamp(3, order.getOrderDate());
            ps.setString(4, order.getStatus().name());
            ps.setString(5, order.getServiceType());
            ps.setDouble(6, order.getTotalWeight());
            ps.setDouble(7, order.getTotalPrice());
            ps.setTimestamp(8, order.getPickupSchedule());
            ps.setString(9, order.getPickupAddress());
            ps.setString(10, order.getNotes());
            ps.setBoolean(11, order.isPaid());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==========================================================================
    // REVISI TOTAL: LOGIKA UPLOAD BUKTI KE TABEL PAYMENTS
    // ==========================================================================
    public boolean uploadPaymentProof(String orderId, String proofFileName) {
        double amount = getOrderAmount(orderId);
        
        if (isPaymentExists(orderId)) {
            // Update jika sudah ada (Re-upload)
            // REVISI: Nama kolom jadi 'payment_proof'
            String sql = "UPDATE payments SET payment_proof = ?, is_verified = 0 WHERE order_id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, proofFileName);
                ps.setString(2, orderId);
                return ps.executeUpdate() > 0;
            } catch (SQLException e) { e.printStackTrace(); return false; }
        } else {
            // Insert Baru
            // REVISI: Nama kolom jadi 'payment_proof'
            String sql = "INSERT INTO payments (payment_id, order_id, amount, payment_proof, is_verified) VALUES (?, ?, ?, ?, 0)";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                String paymentId = "PAY-" + System.currentTimeMillis();
                ps.setString(1, paymentId);
                ps.setString(2, orderId);
                ps.setDouble(3, amount);
                ps.setString(4, proofFileName);
                return ps.executeUpdate() > 0;
            } catch (SQLException e) { e.printStackTrace(); return false; }
        }
    }
    
    // Helper: Cek apakah payment sudah ada
    private boolean isPaymentExists(String orderId) {
        String sql = "SELECT payment_id FROM payments WHERE order_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, orderId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Helper: Ambil harga order
    private double getOrderAmount(String orderId) {
        String sql = "SELECT total_price FROM orders WHERE order_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, orderId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble("total_price");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    // REVISI TOTAL: SESUAIKAN NAMA KOLOM DENGAN DATABASE (payment_proof)
    public List<Order> getOrdersByCustomer(String customerId) {
        List<Order> list = new ArrayList<>();
        
        // REVISI PENTING:
        // 1. Menggunakan p.payment_proof (Sesuai Screenshot Anda image_cfe2e8)
        // 2. Menggunakan ALIAS 'bukti_bayar' untuk menghindari bentrok dengan o.payment_proof
        String sql = "SELECT o.order_id, o.customer_id, o.order_date, o.status, o.service_type, " +
                     "o.total_weight, o.total_price, o.pickup_schedule, o.pickup_address, " +
                     "o.notes, o.is_paid, p.payment_proof AS bukti_bayar " +
                     "FROM orders o " +
                     "LEFT JOIN payments p ON o.order_id = p.order_id " +
                     "WHERE o.customer_id = ? " +
                     "ORDER BY o.order_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, customerId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Order o = new Order();
                o.setOrderId(rs.getString("order_id"));
                o.setCustomerId(rs.getString("customer_id"));
                o.setOrderDate(rs.getTimestamp("order_date"));
                
                // SAFE ENUM MAPPING (Anti Crash)
                String statusDb = rs.getString("status");
                try {
                    o.setStatus(OrderStatus.valueOf(statusDb));
                } catch (Exception e) {
                    System.out.println("ERROR MAPPING STATUS: " + statusDb);
                    o.setStatus(OrderStatus.MENUNGGU_DIJEMPUT); // Default
                }

                o.setServiceType(rs.getString("service_type"));
                o.setTotalWeight(rs.getDouble("total_weight"));
                o.setTotalPrice(rs.getDouble("total_price"));
                o.setPickupSchedule(rs.getTimestamp("pickup_schedule"));
                o.setPickupAddress(rs.getString("pickup_address"));
                o.setNotes(rs.getString("notes"));
                o.setPaid(rs.getBoolean("is_paid"));
                
                // PENTING: Ambil dari ALIAS 'bukti_bayar'
                o.setPaymentProof(rs.getString("bukti_bayar")); 
                
                list.add(o);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Cek log jika list masih kosong!
        }
        return list;
    }
    
    // --- METHOD STATISTIK (Tidak Berubah) ---
    
    public int countCompletedOrders(String customerId) {
        String sql = "SELECT COUNT(*) FROM orders WHERE customer_id = ? AND status = 'SELESAI_DICUCI'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerId);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public int countActiveOrders(String customerId) {
        String sql = "SELECT COUNT(*) FROM orders WHERE customer_id = ? AND status NOT IN ('SELESAI_DICUCI', 'DIBATALKAN')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerId);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    // Mengambil Info Driver
    public String[] getAssignedDriverInfo(String orderId) {
        String[] driverInfo = new String[2];
        String sql = "SELECT d.driver_name, d.driver_phone " +
                     "FROM orders o " +
                     "JOIN drivers d ON o.driver_id = d.driver_id " +
                     "WHERE o.order_id = ?";
                      
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    driverInfo[0] = rs.getString("driver_name");
                    driverInfo[1] = rs.getString("driver_phone");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return driverInfo;
    }
    
    // Method Hapus Bukti (Jika ditolak Admin / Batal)
    public boolean deletePaymentProof(String orderId) {
        // REVISI: Hapus row di tabel payments
        String sql = "DELETE FROM payments WHERE order_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Method Rating (Tidak Berubah)
    public boolean saveRating(Rating rating) {
        String sql = "INSERT INTO ratings (rating_id, order_id, score, review_text) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, rating.getRatingId()); 
            ps.setString(2, rating.getOrderId());
            ps.setInt(3, rating.getScore());
            ps.setString(4, rating.getReviewText());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isOrderRated(String orderId) {
        String sql = "SELECT rating_id FROM ratings WHERE order_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, orderId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}