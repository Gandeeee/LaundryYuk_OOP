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
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminDAO {

    // 1. Ambil SEMUA Order (REVISI: JOIN KE PAYMENTS)
    public List<Order> getAllOrders() {
        List<Order> list = new ArrayList<>();
        
        // REVISI: p.payment_proof AS bukti_bayar
        String sql = "SELECT o.order_id, o.customer_id, o.order_date, o.status, o.service_type, " +
                     "o.total_weight, o.total_price, o.pickup_schedule, o.pickup_address, " +
                     "o.notes, o.is_paid, p.payment_proof AS bukti_bayar " +
                     "FROM orders o " +
                     "LEFT JOIN payments p ON o.order_id = p.order_id " +
                     "ORDER BY o.order_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Order o = new Order();
                o.setOrderId(rs.getString("order_id"));
                o.setCustomerId(rs.getString("customer_id"));
                o.setOrderDate(rs.getTimestamp("order_date"));
                
                String statusDb = rs.getString("status");
                try {
                    o.setStatus(OrderStatus.valueOf(statusDb));
                } catch (Exception e) {
                    System.out.println("ADMIN ERROR STATUS: " + statusDb);
                    o.setStatus(OrderStatus.MENUNGGU_DIJEMPUT);
                }

                o.setServiceType(rs.getString("service_type"));
                o.setTotalWeight(rs.getDouble("total_weight"));
                o.setTotalPrice(rs.getDouble("total_price"));
                o.setPickupSchedule(rs.getTimestamp("pickup_schedule"));
                o.setPickupAddress(rs.getString("pickup_address"));
                o.setNotes(rs.getString("notes"));
                o.setPaid(rs.getBoolean("is_paid"));
                
                // Mapping Bukti Bayar dari ALIAS
                o.setPaymentProof(rs.getString("bukti_bayar")); 
                
                list.add(o);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2. Update Status Order (REVISI: Smart Update Data Overwriting Fix)
    public boolean updateOrder(String orderId, OrderStatus newStatus, double weight, double price) {
        String sql;
        
        // Logika: Jika weight/price 0, hanya update status (jangan timpa harga jadi 0)
        if (weight == 0 && price == 0) {
            sql = "UPDATE orders SET status = ? WHERE order_id = ?";
        } else {
            sql = "UPDATE orders SET status = ?, total_weight = ?, total_price = ? WHERE order_id = ?";
        }
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            if (weight == 0 && price == 0) {
                ps.setString(1, newStatus.name());
                ps.setString(2, orderId);
            } else {
                ps.setString(1, newStatus.name());
                ps.setDouble(2, weight);
                ps.setDouble(3, price);
                ps.setString(4, orderId);
            }
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // 3. Statistik: Total Pendapatan
    // CATATAN: Tetap ambil dari orders.is_paid karena kita sepakat mempertahankan flag ini
    public double getTotalRevenue() {
        String sql = "SELECT SUM(total_price) FROM orders WHERE is_paid = 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if(rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    
    // FUNGSI ASSIGN DRIVER  Jemput & Antar
    public boolean assignDriver(String orderId, String driverId, String targetStatus) {
        String sql = "UPDATE orders SET driver_id = ?, status = ? WHERE order_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, driverId);
            ps.setString(2, targetStatus); // Status sesuai parameter
            ps.setString(3, orderId);
            
            int result = ps.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // 5. Input Tagihan
    public boolean inputBill(String orderId, double weight, double pricePerKg) {
        double totalPrice = weight * pricePerKg;
        String sql = "UPDATE orders SET total_weight = ?, total_price = ?, status = 'MENUNGGU_PEMBAYARAN' WHERE order_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setDouble(1, weight);
            ps.setDouble(2, totalPrice);
            ps.setString(3, orderId);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // ==========================================================================
    // REVISI TOTAL: VERIFY PAYMENT (MULTI-TABLE UPDATE)
    // ==========================================================================
    public boolean verifyPayment(String orderId, boolean isValid) {
        Connection conn = null;
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Mulai Transaksi Manual
            
            if (isValid) {
                // A. JIKA SAH:
                // 1. Update tabel PAYMENTS -> is_verified = 1
                String sql1 = "UPDATE payments SET is_verified = 1 WHERE order_id = ?";
                ps1 = conn.prepareStatement(sql1);
                ps1.setString(1, orderId);
                ps1.executeUpdate();
                
                // 2. Update tabel ORDERS -> is_paid = 1, status = PROSES_PENCUCIAN
                String sql2 = "UPDATE orders SET status = 'PROSES_PENCUCIAN', is_paid = 1 WHERE order_id = ?";
                ps2 = conn.prepareStatement(sql2);
                ps2.setString(1, orderId);
                ps2.executeUpdate();
                
            } else {
                // B. JIKA TIDAK SAH / TOLAK:
                // Hapus data dari payments agar user bisa upload ulang
                String sql1 = "DELETE FROM payments WHERE order_id = ?";
                ps1 = conn.prepareStatement(sql1);
                ps1.setString(1, orderId);
                ps1.executeUpdate();
                
                // (Opsional) Pastikan status order tetap MENUNGGU_PEMBAYARAN (biasanya tidak perlu diubah)
            }
            
            conn.commit(); // Simpan Perubahan Permanen
            return true;
            
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return false;
        } finally {
            // Tutup resource manual karena tidak pakai try-with-resources untuk transaction block
            try { if (ps1 != null) ps1.close(); } catch (SQLException e) {}
            try { if (ps2 != null) ps2.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
    }
    
    // Method Delete Order
    public boolean deleteOrder(String orderId) {
        String sql = "DELETE FROM orders WHERE order_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // 4. CHART DATA: (Fixed ONLY_FULL_GROUP_BY)
    public List<double[]> getChartData() {
        List<double[]> stats = new ArrayList<>();
        
        String sql = "SELECT MONTH(order_date) as bulan, " +
                     "COUNT(*) as total_order, " +
                     "COALESCE(SUM(CASE WHEN is_paid = 1 THEN total_price ELSE 0 END), 0) as total_pendapatan " +
                     "FROM orders " +
                     "WHERE order_date >= DATE_SUB(CURRENT_DATE, INTERVAL 5 MONTH) " +
                     "GROUP BY MONTH(order_date) " +
                     "ORDER BY MIN(order_date) ASC";
                      
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                double[] row = new double[3];
                row[0] = rs.getInt("bulan");
                row[1] = rs.getInt("total_order");
                row[2] = rs.getDouble("total_pendapatan");
                stats.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }
}