/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laundryyuk.config;

/**
 *
 * @author gandisuastika
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    
    // ==============================================================================
    // PANDUAN TIM (UBAH SESUAI APLIKASI DATABASE
    // ==============================================================================
    // 1. PENGGUNA MAC (MAMP):
    //    URL: "jdbc:mysql://localhost:8889/db_laundry_yuk?useSSL=false&serverTimezone=UTC"
    //    PASSWORD: "root"
    //
    // 2. PENGGUNA WINDOWS (XAMPP):
    //    URL: "jdbc:mysql://localhost:3306/db_laundry_yuk?useSSL=false&serverTimezone=UTC"
    //    PASSWORD: "" -> SESUAIKAN!!!!
    // ==============================================================================

    // SETTING SAAT INI (MACBOOK M2 - MAMP)
    private static final String URL = "jdbc:mysql://localhost:8889/db_laundry_yuk?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "root"; 

    public static Connection getConnection() {
        Connection connection = null;
        try {
            // 1. Memuat Driver JDBC MySQL 
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // 2. Membuat koneksi
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            // System.out.println("[DatabaseConnection] Koneksi Sukses ke laundry_yuk!");
            
        } catch (ClassNotFoundException e) {
            System.err.println("[DatabaseConnection] Error: Driver JDBC tidak ditemukan.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("[DatabaseConnection] Error: Gagal terhubung ke Database.");
            System.err.println("Pesan Error: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }
    
    // Main method untuk pengujian koneksi secara langsung (Run File)
    public static void main(String[] args) {
        Connection testConn = DatabaseConnection.getConnection();
        if (testConn != null) {
            System.out.println("TEST KONEKSI BERHASIL!");
        } else {
            System.out.println("TEST KONEKSI GAGAL. Cek URL, User, Password, atau Port MAMP Anda.");
        }
    }
}