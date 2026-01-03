/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.laundryyuk.controller;

/**
 *
 * @author gandisuastika
 */

import com.laundryyuk.dao.UserDAO;
import com.laundryyuk.model.Customer;
import com.laundryyuk.model.User;
import java.io.IOException;
import java.util.UUID;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


@WebServlet(name = "AuthServlet", urlPatterns = {"/auth"})
public class AuthServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAO();

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");

        if ("register".equals(action)) {
            handleRegister(request, response);
        } else if ("login".equals(action)) {
            handleLogin(request, response);
        } else if ("logout".equals(action)) {
            HttpSession session = request.getSession();
            session.invalidate();
            response.sendRedirect(request.getContextPath() + "/access/login.jsp");
        } else {
            response.sendRedirect(request.getContextPath() + "/access/login.jsp");
        }
    }

    private void handleRegister(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // 1. Ambil data
            String nama = request.getParameter("name");
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            String confirmPassword = request.getParameter("confirmPassword");

            // 2. VALIDASI DATA 

            // A. Cek Kekosongan
            if (nama == null || nama.trim().isEmpty() || 
                email == null || email.trim().isEmpty() || 
                password == null || password.trim().isEmpty()) {
                throw new IllegalArgumentException("Semua kolom wajib diisi!");
            }

            // B. Validasi Nama (Hanya Huruf dan Spasi)
            if (!nama.matches("^[a-zA-Z\\s]+$")) {
                throw new IllegalArgumentException("Nama hanya boleh berisi huruf!");
            }

            // C. Validasi Format Email
            if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
                throw new IllegalArgumentException("Format email tidak valid (harus mengandung '@' dan domain)!");
            }

            // D. Validasi Password ( > 8 Karakter DAN Kombinasi Huruf + Angka)
            if (password.length() <= 8) {
                throw new IllegalArgumentException("Password harus lebih dari 8 karakter!");
            }
            if (!password.matches("^(?=.*[a-zA-Z])(?=.*[0-9]).+$")) {
                throw new IllegalArgumentException("Password harus kombinasi huruf dan angka!");
            }

            // E. Cek Password Match
            if (!password.equals(confirmPassword)) {
                throw new IllegalArgumentException("Password dan Konfirmasi Password tidak cocok!");
            }

            // F. Cek Email Terdaftar (Database)
            if (userDAO.isEmailRegistered(email)) {
                throw new IllegalArgumentException("Email " + email + " sudah terdaftar!");
            }

            // 3. Jika Lolos Validasi, Lanjut Simpan
            Customer customer = new Customer();
            customer.setUserId("CUST-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            customer.setEmail(email);
            customer.setPasswordHash(password); 
            customer.setCustomerName(nama);
            customer.setPhoneNumber("-");
            customer.setAddress("-");

            if (userDAO.registerCustomer(customer)) {
                request.getSession().setAttribute("successMessage", "Registrasi Berhasil! Silakan Login.");
                response.sendRedirect(request.getContextPath() + "/access/login.jsp");
            } else {
                throw new Exception("Gagal menyimpan data ke database. Silakan coba lagi.");
            }

        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("oldName", request.getParameter("name"));
            request.setAttribute("oldEmail", request.getParameter("email"));
            request.getRequestDispatcher("/access/register.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Terjadi kesalahan sistem: " + e.getMessage());
            request.getRequestDispatcher("/access/register.jsp").forward(request, response);
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            String email = request.getParameter("email");
            String password = request.getParameter("password");

            // VALIDASI INPUT LOGIN 
            
            // 1. Validasi Format Email (Tetap cek format email)
            if (email == null || !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
                throw new IllegalArgumentException("Format email tidak valid!");
            }

            // 2. Cek Password (HANYA CEK KEKOSONGAN)
            if (password == null || password.trim().isEmpty()) {
                throw new IllegalArgumentException("Password tidak boleh kosong!");
            }
            

            // 3. Cek Database (DAO akan mencocokkan hash password)
            User user = userDAO.login(email, password);

            if (user != null) {
                // Login Sukses
                HttpSession session = request.getSession();
                session.setAttribute("user", user); 
                
                if ("ADMIN".equals(user.getRole())) {
                    response.sendRedirect(request.getContextPath() + "/admin?action=dashboard");
                } else {
                    response.sendRedirect(request.getContextPath() + "/customer?action=dashboard");
                }
            } else {
                // Login Gagal (Email tidak ada ATAU Password salah)
                throw new IllegalArgumentException("Email atau Password salah!");
            }

        } catch (IllegalArgumentException e) {
            // Tangkap error validasi login / login gagal
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("oldEmail", request.getParameter("email"));
            request.getRequestDispatcher("/access/login.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Terjadi kesalahan sistem.");
            request.getRequestDispatcher("/access/login.jsp").forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}