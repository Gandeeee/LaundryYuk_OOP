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
import com.laundryyuk.model.Driver;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DriverDAO {

    // Ambil Semua Driver
    public List<Driver> getAllDrivers() {
        List<Driver> list = new ArrayList<>();
        String sql = "SELECT * FROM drivers"; 
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Driver d = new Driver();
                d.setDriverId(rs.getString("driver_id")); 
                d.setDriverName(rs.getString("driver_name"));
                // REVISI: Sesuai struktur tabel di gambar (driver_phone)
                d.setDriverPhone(rs.getString("driver_phone")); 
                d.setAvailable(rs.getBoolean("is_available"));
                list.add(d);
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return list;
    }

    // Tambah Driver Baru
    public boolean addDriver(Driver d) {
        // REVISI: SQL Insert menggunakan driver_phone
        String sql = "INSERT INTO drivers (driver_id, driver_name, driver_phone, is_available) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, d.getDriverId());
            ps.setString(2, d.getDriverName());
            ps.setString(3, d.getDriverPhone());
            ps.setBoolean(4, d.isAvailable());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { 
            e.printStackTrace(); 
            return false; 
        }
    }

    // Update Driver
    public boolean updateDriver(Driver d) {
        // REVISI: SQL Update menggunakan driver_phone
        String sql = "UPDATE drivers SET driver_name=?, driver_phone=?, is_available=? WHERE driver_id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, d.getDriverName());
            ps.setString(2, d.getDriverPhone());
            ps.setBoolean(3, d.isAvailable());
            ps.setString(4, d.getDriverId());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { 
            e.printStackTrace(); 
            return false; 
        }
    }

    // Hapus Driver (Parameter ID String)
    public boolean deleteDriver(String id) {
        String sql = "DELETE FROM drivers WHERE driver_id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { 
            e.printStackTrace(); 
            return false; 
        }
    }
}