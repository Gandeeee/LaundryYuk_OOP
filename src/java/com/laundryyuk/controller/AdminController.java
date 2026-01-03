/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.laundryyuk.controller;

import com.laundryyuk.dao.AdminDAO;
import com.laundryyuk.dao.DriverDAO;
import com.laundryyuk.dao.OrderDAO;
import com.laundryyuk.model.Driver;
import com.laundryyuk.model.Order;
import com.laundryyuk.model.OrderStatus;
import com.laundryyuk.model.User;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author gandisuastika
 */


@WebServlet(name = "AdminController", urlPatterns = {"/admin"})
public class AdminController extends HttpServlet {

    private AdminDAO adminDAO = new AdminDAO();
    private DriverDAO driverDAO = new DriverDAO();
    private OrderDAO orderDAO = new OrderDAO();

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        // Cek Login
        if (user == null || !"ADMIN".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/access/login.jsp");
            return;
        }

        String action = request.getParameter("action");
        if (action == null) action = "dashboard";

        try {
            switch (action) {
                case "dashboard":
                    showDashboard(request, response);
                    break;
                case "updateOrder":
                    handleUpdateOrder(request, response);
                    break;
                case "saveDriver":
                    handleSaveDriver(request, response);
                    break;
                case "deleteDriver":
                    handleDeleteDriver(request, response);
                    break;
                case "assignDriver":
                    handleAssignDriver(request, response);
                    break;
                case "inputBill":
                    handleInputBill(request, response);
                    break; 
                case "verifyPayment":
                    handleVerifyPayment(request, response);
                    break;
                case "deleteOrder":
                    handleDeleteOrder(request, response);
                    break;
                case "exportCSV":
                    exportSalesReport(request, response);
                    break;
                default:
                    showDashboard(request, response);
                    break;
            }
        } catch (Exception e) {
            // --- PERBAIKAN UTAMA DI SINI ---
            e.printStackTrace(); // Print error lengkap di Output NetBeans (PENTING!)
            
            // Jangan Redirect! Tapi Forward ke dashboard dengan pesan error.
            // Agar JSP tetap tampil meskipun datanya kosong/error.
            request.setAttribute("errorMessage", "Terjadi Kesalahan Sistem: " + e.getMessage());
            
            // Pastikan kita tidak memanggil showDashboard lagi (karena itu penyebab errornya)
            // Langsung lempar ke JSP
            request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);
        }
    }
    
    private void handleInputBill(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String orderId = request.getParameter("order_id");
        String weightStr = request.getParameter("weight");
        String priceStr = request.getParameter("price_per_kg");

        try {
            double weight = Double.parseDouble(weightStr);
            double pricePerKg = Double.parseDouble(priceStr);

            if (adminDAO.inputBill(orderId, weight, pricePerKg)) {
                request.getSession().setAttribute("infoMessage", "Tagihan berhasil diinput. Status kini MENUNGGU PEMBAYARAN.");
            } else {
                request.getSession().setAttribute("errorMessage", "Gagal menginput tagihan.");
            }
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "Format angka salah.");
        }
        response.sendRedirect(request.getContextPath() + "/admin?action=dashboard&page=orders");
    }

    private void showDashboard(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Ambil Data Dasar
        List<Order> orderList = adminDAO.getAllOrders();
        List<Driver> driverList = driverDAO.getAllDrivers();
        double totalRevenue = adminDAO.getTotalRevenue();
        long pendingOrders = orderList.stream()
                .filter(o -> !"SELESAI_DICUCI".equals(o.getStatus().name()) && !"DIBATALKAN".equals(o.getStatus().name()))
                .count();

        // 2. PROSES DATA CHART (LOGIKA BARU)
        // Kita butuh array 6 bulan terakhir yang urut, meskipun datanya 0 di database
        List<double[]> dbStats = adminDAO.getChartData();
        
        StringBuilder labels = new StringBuilder("[");
        StringBuilder dataOrder = new StringBuilder("[");
        StringBuilder dataRevenue = new StringBuilder("[");
        
        // Loop 6 bulan ke belakang dari sekarang
        java.time.LocalDate now = java.time.LocalDate.now();
        String[] monthNames = {"", "Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Ags", "Sep", "Okt", "Nov", "Des"};
        
        // Array sementara untuk menampung data 6 bulan (Index 0 = 5 bulan lalu, Index 5 = Bulan ini)
        for (int i = 5; i >= 0; i--) {
            java.time.LocalDate date = now.minusMonths(i);
            int monthInt = date.getMonthValue();
            
            // Cari data di list DB
            double count = 0;
            double rev = 0;
            
            for (double[] row : dbStats) {
                if ((int)row[0] == monthInt) {
                    count = row[1];
                    rev = row[2];
                    break;
                }
            }
            
            // Append ke String Builder (Format JSON Array)
            labels.append("\"").append(monthNames[monthInt]).append("\"");
            dataOrder.append((int)count);
            dataRevenue.append((int)rev); // Konversi ke juta nanti di JS atau biarkan raw
            
            if (i > 0) {
                labels.append(",");
                dataOrder.append(",");
                dataRevenue.append(",");
            }
        }
        
        labels.append("]");
        dataOrder.append("]");
        dataRevenue.append("]");

        // 3. Kirim ke JSP
        request.setAttribute("orderList", orderList);
        request.setAttribute("driverList", driverList);
        request.setAttribute("totalRevenue", totalRevenue);
        request.setAttribute("totalOrders", orderList.size());
        request.setAttribute("pendingOrders", pendingOrders);
        
        // Data Chart Real Time
        request.setAttribute("chartLabels", labels.toString());
        request.setAttribute("chartOrderData", dataOrder.toString());
        request.setAttribute("chartRevenueData", dataRevenue.toString());
        
        request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);
    }

    private void handleUpdateOrder(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String orderId = request.getParameter("order_id");
        String statusStr = request.getParameter("new_status");
        
        if (orderId != null && statusStr != null) {
            // Update Status saja (sesuai modal update status di referensi)
            // Berat/Harga bisa ditambahkan jika formnya mengirim data tersebut
            OrderStatus status = OrderStatus.valueOf(statusStr);
            // Default 0 untuk weight/price jika tidak diupdate di form ini
            adminDAO.updateOrder(orderId, status, 0, 0); 
            request.getSession().setAttribute("infoMessage", "Status Order " + orderId + " diperbarui.");
        }
        response.sendRedirect(request.getContextPath() + "/admin?action=dashboard&page=orders");
    }


    private void handleSaveDriver(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Ambil ID sebagai String (bisa null jika tambah baru)
        String idStr = request.getParameter("driver_id");
        String name = request.getParameter("name");
        String phone = request.getParameter("phone");
        // Checkbox value 'on' jika dicentang, null jika tidak
        boolean isAvailable = "on".equals(request.getParameter("isAvailable"));

        Driver d = new Driver();
        d.setDriverName(name);
        d.setDriverPhone(phone);
        d.setAvailable(isAvailable);

        if (idStr == null || idStr.trim().isEmpty()) {
            // LOGIKA TAMBAH BARU (Create ID String)
            // Generate ID Unik, misal: DRV-Timestamp
            String newId = "DRV-" + System.currentTimeMillis() % 100000; 
            d.setDriverId(newId);
            
            if(driverDAO.addDriver(d)) {
                request.getSession().setAttribute("infoMessage", "Driver baru berhasil ditambahkan.");
            } else {
                request.getSession().setAttribute("errorMessage", "Gagal menambah driver.");
            }
        } else {
            // LOGIKA UPDATE (Pakai ID lama)
            d.setDriverId(idStr);
            
            if(driverDAO.updateDriver(d)) {
                request.getSession().setAttribute("infoMessage", "Data Driver berhasil diupdate.");
            } else {
                request.getSession().setAttribute("errorMessage", "Gagal update driver.");
            }
        }
        response.sendRedirect(request.getContextPath() + "/admin?action=dashboard&page=drivers");
    }

    private void handleDeleteDriver(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Ambil ID sebagai String
        String id = request.getParameter("driver_id");
        
        if (id != null && !id.isEmpty()) {
            if(driverDAO.deleteDriver(id)) {
                request.getSession().setAttribute("infoMessage", "Driver berhasil dihapus.");
            } else {
                request.getSession().setAttribute("errorMessage", "Gagal menghapus driver (mungkin sedang bertugas).");
            }
        }
        response.sendRedirect(request.getContextPath() + "/admin?action=dashboard&page=drivers");
    }
    
    private void handleAssignDriver(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String orderId = request.getParameter("order_id");
        String driverId = request.getParameter("driver_id");
        String targetStatus = request.getParameter("target_status"); // Ambil dari Hidden Input

        if (orderId != null && driverId != null && targetStatus != null) {
            if (adminDAO.assignDriver(orderId, driverId, targetStatus)) {
                String msg = targetStatus.equals("DIKIRIM") ? 
                        "Driver berhasil ditugaskan untuk MENGANTAR cucian (Status: DIKIRIM)." : 
                        "Driver berhasil ditugaskan untuk MENJEMPUT cucian (Status: DRIVER OTW).";
                
                request.getSession().setAttribute("infoMessage", msg);
            } else {
                request.getSession().setAttribute("errorMessage", "Gagal menugaskan driver.");
            }
        }
        response.sendRedirect(request.getContextPath() + "/admin?action=dashboard&page=orders");
    }
    
    private void handleVerifyPayment(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String orderId = request.getParameter("order_id");
        String decision = request.getParameter("decision"); // "valid" atau "invalid"
        String customerPhone = request.getParameter("customer_phone"); // Untuk WA jika invalid
        
        boolean isValid = "valid".equalsIgnoreCase(decision);
        
        if (adminDAO.verifyPayment(orderId, isValid)) {
            if (isValid) {
                request.getSession().setAttribute("infoMessage", "Pembayaran Order #" + orderId + " telah diverifikasi SAH.");
            } else {
                
                String waUrl = "https://wa.me/" + customerPhone + "?text=Halo%20kak,%20bukti%20pembayaran%20untuk%20Order%20" + orderId + "%20tidak%20valid/buram.%20Mohon%20upload%20ulang%20ya.";
                request.getSession().setAttribute("infoMessage", "Pembayaran ditolak. Bukti di-reset.");
                request.getSession().setAttribute("openWaUrl", waUrl); // Trigger JS di JSP
            }
        } else {
            request.getSession().setAttribute("errorMessage", "Gagal memverifikasi pembayaran.");
        }
        
        response.sendRedirect(request.getContextPath() + "/admin?action=dashboard");
    }
    
    private void handleDeleteOrder(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String orderId = request.getParameter("order_id");
        
        if (orderId != null && !orderId.isEmpty()) {
            if (adminDAO.deleteOrder(orderId)) {
                request.getSession().setAttribute("infoMessage", "Order #" + orderId + " berhasil dihapus permanen.");
            } else {
                request.getSession().setAttribute("errorMessage", "Gagal menghapus order. Pastikan order ID valid.");
            }
        }
        response.sendRedirect(request.getContextPath() + "/admin?action=dashboard&page=orders");
    }
    
    
    public void exportSalesReport(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 1. Ambil Semua Data
        List<Order> allOrders = adminDAO.getAllOrders();
        
        // 2. Set Header Response
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"Laporan_Penjualan_Laundry.csv\"");
        
        // 3. Tulis Data
        try (java.io.PrintWriter writer = response.getWriter()) {
            
            // Header Kolom
            writer.println("Order ID,Tanggal Order,Nama Customer,Layanan,Berat (Kg),Total Harga,Status Order,Status Pembayaran");
            
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
            
            for (Order o : allOrders) {
                // Filter: Hanya yang LUNAS (Sesuai request Anda sebelumnya)
                if (o.isPaid()) {
                    String customerClean = (o.getCustomerId() + " " + (o.getNotes() != null ? o.getNotes() : "")).replace(",", " ");
                    String serviceClean = (o.getServiceType() != null ? o.getServiceType() : "").replace(",", " ");
                    
                    writer.print(o.getOrderId());
                    writer.print(",");
                    writer.print(sdf.format(o.getOrderDate()));
                    writer.print(",");
                    writer.print(customerClean);
                    writer.print(",");
                    writer.print(serviceClean);
                    writer.print(",");
                    writer.print(o.getTotalWeight());
                    writer.print(",");
                    writer.print((int)o.getTotalPrice());
                    writer.print(",");
                    writer.print(o.getStatus().name());
                    writer.print(",");
                    writer.println("LUNAS");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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