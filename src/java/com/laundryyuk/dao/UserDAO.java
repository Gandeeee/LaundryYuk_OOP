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
import com.laundryyuk.model.Customer;
import com.laundryyuk.model.User;
import com.laundryyuk.util.PasswordUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    public boolean registerCustomer(Customer customer) {
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement psUser = null;
        PreparedStatement psCust = null;
        boolean isSuccess = false;

        String sqlUser = "INSERT INTO users (user_id, email, password_hash, phone_number, role) VALUES (?, ?, ?, ?, ?)";
        String sqlCustomer = "INSERT INTO customers (user_id, customer_name, address) VALUES (?, ?, ?)";

        try {
            // 1. Matikan Auto-Commit untuk memulai Transaction
            conn.setAutoCommit(false);

            // 2. Insert ke Tabel USERS (Parent)
            psUser = conn.prepareStatement(sqlUser);
            psUser.setString(1, customer.getUserId());
            psUser.setString(2, customer.getEmail());
            // Password di-hash sebelum disimpan
            psUser.setString(3, PasswordUtil.hashPassword(customer.getPasswordHash())); 
            psUser.setString(4, customer.getPhoneNumber());
            psUser.setString(5, "CUSTOMER"); // Hardcode role
            psUser.executeUpdate();

            // 3. Insert ke Tabel CUSTOMERS (Child)
            psCust = conn.prepareStatement(sqlCustomer);
            psCust.setString(1, customer.getUserId()); // Foreign Key sama dengan User ID
            psCust.setString(2, customer.getCustomerName());
            psCust.setString(3, customer.getAddress());
            psCust.executeUpdate();

            // 4. Commit Transaksi (Simpan Permanen)
            conn.commit();
            isSuccess = true;
            System.out.println("[UserDAO] Register Customer Berhasil: " + customer.getEmail());

        } catch (SQLException e) {
            System.err.println("[UserDAO] Register Gagal! Melakukan Rollback...");
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback(); // Batalkan semua jika error
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            // 5. Tutup resource & kembalikan Auto-Commit
            try {
                if (conn != null) conn.setAutoCommit(true);
                if (psUser != null) psUser.close();
                if (psCust != null) psCust.close();
                if (conn != null) conn.close(); // Penting: kembalikan koneksi ke pool/tutup
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return isSuccess;
    }

    /**
     * Melakukan proses Login.
     * Mencocokkan Email dan Password Hash.
     * @return Objek User jika berhasil, null jika gagal.
     */
    public User login(String email, String passwordInput) {
        User user = null;
        String sql = "SELECT * FROM users WHERE email = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String dbPassHash = rs.getString("password_hash");
                String inputPassHash = PasswordUtil.hashPassword(passwordInput);

                // Cek kesamaan Hash
                if (dbPassHash.equals(inputPassHash)) {
                    user = new User();
                    user.setUserId(rs.getString("user_id"));
                    user.setEmail(rs.getString("email"));
                    user.setPasswordHash(dbPassHash); // Simpan hashnya saja di object
                    user.setPhoneNumber(rs.getString("phone_number"));
                    user.setRole(rs.getString("role"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }
   
    
    /**
     * Cek apakah email sudah terdaftar.
     */
    public boolean isEmailRegistered(String email) {
        String sql = "SELECT user_id FROM users WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next(); // True jika ada data
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
