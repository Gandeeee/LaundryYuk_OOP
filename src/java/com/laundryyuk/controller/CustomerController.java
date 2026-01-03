/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.laundryyuk.controller;

import com.laundryyuk.dao.OrderDAO;
import com.laundryyuk.model.Order;
import com.laundryyuk.model.OrderStatus;
import com.laundryyuk.model.User;
import com.laundryyuk.model.Rating;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.Part;
import java.io.File;
import java.nio.file.Paths;

/**
 *
 * @author gandisuastika
 */

@WebServlet(name = "CustomerController", urlPatterns = {"/customer"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2, // 2MB
    maxFileSize = 1024 * 1024 * 10,      // 10MB
    maxRequestSize = 1024 * 1024 * 50    // 50MB
)

public class CustomerController extends HttpServlet {

    private OrderDAO orderDAO = new OrderDAO();

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Cek Login Session
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if (user == null || !"CUSTOMER".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/access/login.jsp");
            return;
        }

        String action = request.getParameter("action");
        if (action == null) action = "dashboard";

        switch (action) {
            case "dashboard":
                showDashboard(request, response, user);
                break;
            case "createOrder":
                createOrder(request, response, user);
                break;
            case "uploadPayment": 
                handleUploadPayment(request, response);
                break;
            case "deletePayment": // CASE BARU
                handleDeletePayment(request, response);
                break;
            case "giveRating":
                handleGiveRating(request, response);
                break;
            default:
                showDashboard(request, response, user);
                break;
        }
    }

    // MENAMPILKAN DASHBOARD DENGAN DATA DARI DB
    private void showDashboard(HttpServletRequest request, HttpServletResponse response, User user) 
            throws ServletException, IOException {
        
        // 1. Ambil Statistik
        int activeOrders = orderDAO.countActiveOrders(user.getUserId());
        int completedOrders = orderDAO.countCompletedOrders(user.getUserId());
        
        // 2. Ambil Riwayat Order
        List<Order> orderList = orderDAO.getOrdersByCustomer(user.getUserId());
        
        // 3. Kirim ke JSP
        request.setAttribute("activeOrders", activeOrders);
        request.setAttribute("completedOrders", completedOrders);
        request.setAttribute("orderList", orderList);
        
        request.getRequestDispatcher("/customer/dashboard.jsp").forward(request, response);
    }

    // MEMPROSES FORM ORDER
    private void createOrder(HttpServletRequest request, HttpServletResponse response, User user) 
            throws ServletException, IOException {
        try {
            // 1. Ambil data dari form
            String serviceType = request.getParameter("serviceType");
            String address = request.getParameter("address");
            String phone = request.getParameter("phone");
            String dateStr = request.getParameter("pickupDate");
            String timeStr = request.getParameter("pickupTime");
            String notes = request.getParameter("notes");
            
            // 2. VALIDASI INPUT
            
            // A. Validasi Kelengkapan Data Dasar
            if (serviceType == null || serviceType.isEmpty() ||
                address == null || address.trim().isEmpty() ||
                dateStr == null || dateStr.isEmpty() ||
                timeStr == null || timeStr.isEmpty()) {
                throw new IllegalArgumentException("Mohon lengkapi semua data pesanan!");
            }

            // B. Parsing Tanggal & Waktu
            LocalDate pickupDate = LocalDate.parse(dateStr);
            LocalTime pickupTime = LocalTime.parse(timeStr);
            LocalDate today = LocalDate.now();
            
            // C. Validasi Tanggal (Max 3 Hari dari Hari Ini & Tidak Boleh Lampau)
            if (pickupDate.isBefore(today)) {
                throw new IllegalArgumentException("Tanggal penjemputan tidak boleh tanggal yang sudah lewat!");
            }
            if (pickupDate.isAfter(today.plusDays(3))) {
                throw new IllegalArgumentException("Maksimal jadwal penjemputan adalah 3 hari ke depan!");
            }

            // D. Validasi Jam Kerja (09:00 - 20:00)
            LocalTime openTime = LocalTime.of(9, 0);   // 09:00
            LocalTime closeTime = LocalTime.of(20, 0); // 20:00
            
            if (pickupTime.isBefore(openTime) || pickupTime.isAfter(closeTime)) {
                throw new IllegalArgumentException("Jam operasional penjemputan hanya pukul 09:00 - 20:00 WIB!");
            }

            // 3. Jika Validasi Lolos, Proses Penyimpanan
            
            // Gabungkan Date & Time menjadi Timestamp
            String dateTimeStr = dateStr + " " + timeStr + ":00";
            Timestamp pickupSchedule = Timestamp.valueOf(dateTimeStr);
            
            // Buat Object Order
            Order order = new Order();
            order.setOrderId("ORD-" + System.currentTimeMillis() / 1000); 
            order.setCustomerId(user.getUserId());
            order.setOrderDate(new Timestamp(System.currentTimeMillis()));
            order.setStatus(OrderStatus.MENUNGGU_DIJEMPUT);
            order.setServiceType(serviceType);
            order.setTotalWeight(0); 
            order.setTotalPrice(0);  
            order.setPickupSchedule(pickupSchedule);
            order.setPickupAddress(address + " (" + phone + ")");
            order.setNotes(notes);
            order.setPaid(false);
            
            // Simpan ke DB
            if (orderDAO.createOrder(order)) {
                request.getSession().setAttribute("infoMessage", "Pesanan berhasil dibuat! Driver kami akan segera menjemput.");
                response.sendRedirect(request.getContextPath() + "/customer?action=dashboard");
            } else {
                throw new Exception("Gagal menyimpan data ke database (Database Error).");
            }
            
        } catch (IllegalArgumentException e) {
            // Catch khusus validasi input user (Tanggal/Jam salah)
            request.setAttribute("errorMessage", e.getMessage());
            // Tampilkan kembali dashboard dengan pesan error
            showDashboard(request, response, user);
            
        } catch (Exception e) {
            // Catch error sistem lainnya
            e.printStackTrace();
            request.setAttribute("errorMessage", "Terjadi kesalahan sistem: " + e.getMessage());
            showDashboard(request, response, user);
        }
    }
    
    private void handleUploadPayment(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            String orderId = request.getParameter("order_id");
            Part filePart = request.getPart("payment_proof"); // Ambil file dari form
            
            if (filePart != null && filePart.getSize() > 0) {
                // 1. Tentukan Nama File & Lokasi Simpan
                String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                // Generate nama unik agar tidak bentrok (misal: BUKTI_ORD-123_filename.jpg)
                String uniqueFileName = "BUKTI_" + orderId + "_" + System.currentTimeMillis() + "_" + fileName;
                
                // Path Absolut ke folder uploads di server
                // Pastikan folder ini ada atau buat manual di web/assets/uploads
                String uploadPath = getServletContext().getRealPath("") + File.separator + "assets" + File.separator + "uploads";
                // --- DEBUGGING: CEK LOG NETBEANS UNTUK LIHAT LOKASI ASLI ---
                System.out.println("=========================================");
                System.out.println("DEBUG UPLOAD PATH: " + uploadPath);
                System.out.println("FILE NAME: " + uniqueFileName);
                System.out.println("=========================================");
                
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) uploadDir.mkdir(); 
                
                filePart.write(uploadPath + File.separator + uniqueFileName);
                
                if (orderDAO.uploadPaymentProof(orderId, uniqueFileName)) {
                    request.getSession().setAttribute("infoMessage", "Bukti pembayaran berhasil diunggah! Mohon tunggu verifikasi admin.");
                } else {
                    throw new Exception("Gagal menyimpan data ke database.");
                }
            } else {
                throw new Exception("File tidak ditemukan atau kosong.");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Gagal upload: " + e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/customer?action=dashboard");
    }
    
    private void handleDeletePayment(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String orderId = request.getParameter("order_id");
        
        if (orderId != null && !orderId.isEmpty()) {
            // Panggil DAO untuk set payment_proof jadi NULL
            if (orderDAO.deletePaymentProof(orderId)) {
                request.getSession().setAttribute("infoMessage", "Bukti pembayaran berhasil dihapus. Silakan upload ulang.");
            } else {
                request.getSession().setAttribute("errorMessage", "Gagal menghapus bukti pembayaran.");
            }
        }
        response.sendRedirect(request.getContextPath() + "/customer?action=dashboard");
    }
    
    private void handleGiveRating(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            String orderId = request.getParameter("order_id");
            String scoreStr = request.getParameter("score");
            String reviewText = request.getParameter("review_text");

            // DEBUG: Cek data masuk
            System.out.println("DEBUG RATING: OrderID=" + orderId + ", Score=" + scoreStr + ", Text=" + reviewText);

            if (orderId == null || scoreStr == null) {
                throw new IllegalArgumentException("Data rating tidak lengkap.");
            }

            int score = Integer.parseInt(scoreStr);
            
            // Validasi Input
            if (score < 1 || score > 10) {
                throw new IllegalArgumentException("Skor harus antara 1-10");
            }
            
            // Gunakan Constructor 3 Parameter (ID otomatis dibuat di Model)
            Rating rating = new Rating(orderId, score, reviewText);
            
            // DEBUG: Cek ID yang dibuat
            System.out.println("DEBUG RATING: Generated ID=" + rating.getRatingId());

            if (orderDAO.saveRating(rating)) {
                request.getSession().setAttribute("infoMessage", "Terima kasih! Ulasan Anda berhasil dikirim.");
            } else {
                throw new Exception("Gagal menyimpan ulasan ke database. Cek log server.");
            }
            
        } catch (Exception e) {
            e.printStackTrace(); // Wajib print stack trace ke output NetBeans
            request.getSession().setAttribute("errorMessage", "Gagal mengirim ulasan: " + e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/customer?action=dashboard");
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