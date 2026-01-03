<%-- 
    Document   : dashboard
    Created on : 26 Dec 2025, 10.52.59
    Author     : gandisuastika
--%>

<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.laundryyuk.model.Order"%>
<%@page import="java.util.List"%>
<%@page import="com.laundryyuk.model.User"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.laundryyuk.dao.OrderDAO"%>

<%-- 
   Akseslah via Servlet: /customer?action=dashboard
--%>

<%
    User user = (User) session.getAttribute("user");
    if (user == null) { response.sendRedirect(request.getContextPath() + "/access/login.jsp"); return; }
    
    // Ambil data yang dikirim Servlet
    Integer activeOrders = (Integer) request.getAttribute("activeOrders");
    Integer completedOrders = (Integer) request.getAttribute("completedOrders");
    List<Order> orderList = (List<Order>) request.getAttribute("orderList");
    
    if(activeOrders == null) activeOrders = 0;
    if(completedOrders == null) completedOrders = 0;
    OrderDAO orderDAO = new OrderDAO();
%>

<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>LaundryYuk - Dashboard Pelanggan</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;700;800&display=swap" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/customer.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
</head>
<body>

    <nav class="navbar navbar-expand-lg navbar-light shadow-sm sticky-top">
        <div class="container">
            <a class="navbar-brand d-flex align-items-center" href="#">
                <img src="${pageContext.request.contextPath}/assets/images/logo.png" alt="LaundryYuk Logo" class="brand-logo-img me-2">
                <span class="fs-5 fw-bold" style="color: var(--admin-dark);">LaundryYuk!</span>
            </a>

            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>

            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav mx-auto mb-2 mb-lg-0">
                    <li class="nav-item">
                        <a class="nav-link active" id="nav-dashboard" href="#">Dashboard</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" id="nav-order" href="#">Buat Pesanan</a>
                    </li>
                </ul>

                <div class="d-flex flex-column flex-lg-row align-items-lg-center">
                    <span class="me-lg-3 mb-2 mb-lg-0" style="color: var(--admin-gray);">Hai, <%= user.getEmail() %></span>
                    <a href="${pageContext.request.contextPath}/auth?action=logout" class="btn btn-danger btn-sm">Logout</a>
                </div>
            </div>
        </div>
    </nav>       
                
    <div class="container mt-4 mt-md-5 mb-5">

        <%-- PESAN ERROR DARI SERVLET (VALIDASI TANGGAL/JAM) --%>
        <% if (request.getAttribute("errorMessage") != null) { %>
            <div class="alert alert-danger alert-dismissible fade show mb-4" role="alert">
                <i class="bi bi-exclamation-triangle-fill me-2"></i>
                <%= request.getAttribute("errorMessage") %>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
            <%-- Script untuk otomatis membuka Tab Order saat ada error --%>
            <script>
                document.addEventListener("DOMContentLoaded", function() {
                    document.getElementById('nav-order').click(); // Pindah ke tab order
                });
            </script>
        <% } %>
        
        <%-- PESAN NOTIFIKASI DARI SERVLET --%>
        <% if (session.getAttribute("infoMessage") != null) { %>
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <%= session.getAttribute("infoMessage") %>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
            <% session.removeAttribute("infoMessage"); %>
        <% } %>

        <%-- SECTION 1: DASHBOARD STATS --%>
        <div id="dashboard-page" class="page-section">
            <h2 class="page-title mb-4">Dashboard Saya</h2>
            <div class="row">
                <div class="col-lg-6 mb-4">
                    <div class="card stat-card">
                        <div class="card-body p-4 d-flex align-items-center">
                            <div class="stat-icon bg-warning me-4">
                                <i class="bi bi-arrow-repeat"></i>
                            </div>
                            <div>
                                <h5 class="card-title">Pesanan Diproses</h5>
                                <p class="card-value"><%= activeOrders %></p>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-lg-6 mb-4">
                    <div class="card stat-card">
                        <div class="card-body p-4 d-flex align-items-center">
                            <div class="stat-icon bg-success me-4">
                                <i class="bi bi-patch-check-fill"></i>
                            </div>
                            <div>
                                <h5 class="card-title">Total Pesanan Selesai</h5>
                                <p class="card-value"><%= completedOrders %></p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            
            <h3 class="page-subtitle mt-4 mb-3">Ringkasan Orderku</h3>
            <div class="card">
                <div class="card-body p-3 p-md-4">
                    <div class="table-responsive">
                        <table class="table align-middle">
                            <thead>
                                <tr>
                                    <th scope="col">Order ID</th>
                                    <th scope="col">Tanggal</th>
                                    <th scope="col">Status</th>
                                    <th scope="col">Total</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% 
                                    if (orderList != null && !orderList.isEmpty()) {
                                        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
                                        for (Order o : orderList) {
                                            String statusClass = "bg-primary";
                                            String statusText = o.getStatus().name().replace("_", " ");
                                            boolean isArrived = "TIBA".equals(o.getStatus().name());
                                            boolean isRated = false;

                                            // Cek apakah order TIBA sudah dirating?
                                            if (isArrived) {
                                                isRated = orderDAO.isOrderRated(o.getOrderId());
                                                statusClass = "bg-success"; // Warna hijau untuk TIBA
                                            } else if ("SELESAI_DICUCI".equals(o.getStatus().name())) {
                                                statusClass = "bg-success";
                                            } else if ("MENUNGGU_PEMBAYARAN".equals(o.getStatus().name())) {
                                                statusClass = "bg-warning";
                                            } else if ("DIBATALKAN".equals(o.getStatus().name())) {
                                                statusClass = "bg-danger";
                                            }
                                %>
                                <tr>
                                    <th scope="row">#<%= o.getOrderId() %></th>
                                    <td><%= sdf.format(o.getOrderDate()) %></td>
                                    <td>
                                        <%-- LOGIKA TAMPILAN BADGE (KEMBALI KE STANDAR) --%>
                                        <%-- Semua status membuka StatusModal. Jika TIBA & Belum Rating, beri tanda seru kecil --%>
                                        <span class="badge status-badge <%= statusClass %>" 
                                              style="cursor: pointer;"
                                              data-bs-toggle="modal" 
                                              data-bs-target="#statusModal_<%= o.getOrderId() %>">
                                            <%= statusText %>

                                            <%-- Indikator kecil jika belum rating --%>
                                            <% if(isArrived && !isRated) { %> 
                                                <i class="bi bi-exclamation-circle-fill text-warning ms-1"></i> 
                                            <% } else if (isArrived && isRated) { %>
                                                <i class="bi bi-check-all ms-1"></i>
                                            <% } %>
                                        </span>
                                    </td>
                                    <td>Rp <%= (int) o.getTotalPrice() %></td>
                                </tr>
                                <%      } 
                                    } else { %>
                                <tr><td colspan="4" class="text-center text-muted">Belum ada pesanan. Yuk buat pesanan baru!</td></tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>

        <%-- SECTION 2: FORM ORDER (Submit ke Servlet) --%>
        <div id="order-page" class="page-section" style="display: none;">
            <h2 class="page-title mb-4">Buat Pesanan Baru</h2>
            <div class="card">
                <div class="card-body p-4 p-md-5">
                    <%-- FORM ACTION MENGARAH KE SERVLET --%>
                    <form id="orderForm" action="${pageContext.request.contextPath}/customer" method="POST">
                        <input type="hidden" name="action" value="createOrder">
                        
                        <div class="mb-4">
                            <label for="serviceType" class="form-label fw-bold">Pilih Layanan</label>
                            <select class="form-select form-select-lg" id="serviceType" name="serviceType" required>
                                <option value="" disabled selected>Pilih jenis layanan...</option>
                                <optgroup label="Layanan Cuci (Kiloan)">
                                    <option value="Cuci Kering">Cuci Kering (5.000/KG)</option>
                                    <option value="Cuci Kering Lipat">Cuci Kering Lipat (5.500/KG)</option>
                                    <option value="Cuci Kering Express 6 Jam">Cuci Kering Express 6 Jam (6.000/KG)</option>
                                    <option value="Cuci Kering Express 3 Jam">Cuci Kering Express 3 Jam (7.000/KG)</option>
                                    <option value="Cuci Setrika 2 Hari">Cuci Kering Setrika 2 Hari (8.000/KG)</option>
                                    <option value="Cuci Setrika 3 Hari">Cuci Kering Setrika 3 Hari (7.000/KG)</option>
                                    <option value="Cuci Setrika 4 Hari">Cuci Kering Setrika 4 Hari (6.500/KG)</option>
                                    <option value="Cuci Setrika Express 6 Jam">Cuci Setrika Express 6 Jam (13.000/KG)</option>
                                    <option value="Cuci Setrika Express 1 Jam">Cuci Setrika Express 1 Hari (10.000/KG)</option>
                                    <option value="Setrika Saja">Setrika Saja 3 Hari (5.000/KG)</option>
                                    <option value="Kering Saja">Kering (4.000/KG)</option>
                                </optgroup>
                                <optgroup label="Layanan Satuan">
                                    <option value="Satuan Bedcover">Bedcover (25RB-40RB/PC)</option>
                                    <option value="Satuan Sprei">Cuci Sprei/Selimut (10RB-15RB/SET)</option>
                                    <option value="Satuan Tas">Tas Ransel (25RB-40RB/PC)</option>
                                    <option value="Satuan Boneka">Boneka (5RB-70RB)</option>
                                    <option value="Satuan Sepatu">Sepatu (25RB/PSG)</option>
                                    <option value="Satuan Karpet">Karpet Tebal (30RB/M²)</option>
                                </optgroup>
                            </select>
                        </div>

                        <div class="mb-4">
                            <label for="address" class="form-label fw-bold">Alamat Penjemputan</label>
                            <textarea class="form-control" id="address" name="address" rows="4" required></textarea>
                        </div>

                        <div class="mb-4">
                            <label for="phone" class="form-label fw-bold">Nomor Telepon</label>
                            <input type="tel" class="form-control" id="phone" name="phone" placeholder="08xxxxxxxxxx" required>
                        </div>

                        <div class="row mb-4">
                            <div class="col-md-6 mb-3 mb-md-0">
                                <label for="pickupDate" class="form-label fw-bold">Tanggal Jemput</label>
                                <input type="date" class="form-control" id="pickupDate" name="pickupDate" required>
                            </div>
                            <div class="col-md-6">
                                <label for="pickupTime" class="form-label fw-bold">Waktu Jemput</label>
                                <input type="time" class="form-control" id="pickupTime" name="pickupTime" required>
                            </div>
                        </div>

                        <div class="mb-4">
                            <label for="notes" class="form-label fw-bold">Catatan (Opsional)</label>
                            <input type="text" class="form-control" id="notes" name="notes">
                        </div>

                        <div class="d-grid gap-2 d-md-flex justify-content-md-end mt-4 pt-3 border-top">
                            <button type="button" class="btn btn-light btn-lg me-md-2" onclick="$('#nav-dashboard').click()">Batal</button>
                            <button type="submit" class="btn btn-danger btn-lg">Kirim Pesanan</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
    
    <footer>
        <div class="container text-center">
            <p class="mb-0">&copy; 2025 LaundryYuk. All rights reserved.</p>
        </div>
    </footer>

    <%-- MODAL DETAIL --%>
    <div class="modal fade" id="statusDetailModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content card">
                <div class="modal-header border-0 pb-0">
                    <h5 class="modal-title fw-bold">Detail Pesanan</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <p class="text-center">Memuat...</p>
                </div>
            </div>
        </div>
    </div>
    
    <%-- ======================================================== --%>
    <%-- GENERATE MODAL STATUS CUSTOMER (ADMIN-LIKE STYLE)        --%>
    <%-- ======================================================== --%>
    <% if (orderList != null) { 
        for (Order o : orderList) { 
            String currentStatus = o.getStatus().name();
            String[] driverInfo = null;
            
            // 1. LOGIKA JAVA: AMBIL DATA DRIVER
            if ("DRIVER_OTW".equals(currentStatus) || "DIKIRIM".equals(currentStatus)) {
                driverInfo = orderDAO.getAssignedDriverInfo(o.getOrderId());
            }
    %>
    <div class="modal fade" id="statusModal_<%= o.getOrderId() %>" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                
                <div class="modal-header">
                    <h5 class="modal-title fw-bold">Lacak Order #<%= o.getOrderId() %></h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>

                <div class="modal-body">
                    <p class="fw-bold mb-3">Status Perjalanan Pesanan:</p>
                    
                    <div class="list-group list-group-flush">
                        <% 
                           // 2. LOGIKA JAVA: URUTAN STATUS (SAMA SEPERTI ADMIN)
                           com.laundryyuk.model.OrderStatus[] allStatus = com.laundryyuk.model.OrderStatus.values();
                           int currentIndex = -1;
                           
                           for(int i=0; i<allStatus.length; i++) { 
                               if(allStatus[i].name().equals(currentStatus)) { currentIndex = i; break; } 
                           }

                           for (int i = 0; i < allStatus.length; i++) {
                               String sName = allStatus[i].name();
                               
                               // Filter Tampilan (Sama seperti Admin)
                               if (sName.equals("DIBATALKAN") && !currentStatus.equals("DIBATALKAN")) continue;
                               
                               // State Logic
                               boolean isPassed = (i < currentIndex);
                               boolean isActive = (i == currentIndex);
                               if (currentStatus.equals("DIBATALKAN") && !sName.equals("DIBATALKAN")) isPassed = true;

                               // Visual Styles
                               String itemClass = "list-group-item py-3 border-0 d-flex gap-3 align-items-start";
                               String iconHtml = "<i class='bi bi-circle text-muted'></i>"; // Default (Future)
                               String textClass = "text-muted";

                               if (isPassed) {
                                   iconHtml = "<i class='bi bi-check-circle-fill text-success fs-5'></i>";
                                   textClass = "text-dark"; // Yang sudah lewat tetap hitam wajar
                               } else if (isActive) {
                                   iconHtml = "<i class='bi bi-record-circle-fill text-primary fs-5'></i>";
                                   textClass = "fw-bold text-primary"; // Highlight Status Aktif
                                   itemClass += " bg-light rounded"; // Beri background tipis
                               }
                        %>
                        
                        <%-- ITEM LIST --%>
                        <div class="<%= itemClass %>">
                            <%-- Icon Indicator --%>
                            <div class="mt-1"><%= iconHtml %></div>
                            
                            <%-- Content --%>
                            <div class="w-100">
                                <div class="<%= textClass %> mb-1"><%= sName.replace("_", " ") %></div>
                                
                                <%-- KONTEN DINAMIS (HANYA MUNCUL DI STATUS AKTIF) --%>
                                <% if (isActive) { %>
                                
                                    <%-- A. DRIVER INFO --%>
                                    <% if (sName.equals("DRIVER_OTW") || sName.equals("DIKIRIM")) { %>
                                        <div class="card mt-2 border p-2 bg-white">
                                            <div class="badge-driver-status <%= sName.equals("DIKIRIM") ? "delivery" : "" %>">
                                                <%-- Ikon berbeda untuk tiap status --%>
                                                <% if (sName.equals("DIKIRIM")) { %>
                                                    <i class="bi bi-box-seam"></i> Sedang Mengantar Cucian
                                                <% } else { %>
                                                    <i class="bi bi-bicycle"></i> Sedang Menjemput Cucian
                                                <% } %>
                                            </div>
                                            <div class="d-flex align-items-center gap-2">
                                                <div class="bg-light rounded-circle p-2"><i class="bi bi-person-fill"></i></div>
                                                <div class="flex-grow-1">
                                                    <% if (driverInfo != null && driverInfo[0] != null) { %>
                                                        <div class="fw-bold small"><%= driverInfo[0] %></div>
                                                        <div class="small text-muted"><%= driverInfo[1] %></div>
                                                    <% } else { %>
                                                        <div class="small text-muted">Mencari driver...</div>
                                                    <% } %>
                                                </div>
                                                <% if (driverInfo != null && driverInfo[0] != null) { %>
                                                    <a href="https://wa.me/<%= driverInfo[1] %>" target="_blank" class="btn btn-sm btn-success rounded-circle">
                                                        <i class="bi bi-whatsapp"></i>
                                                    </a>
                                                <% } %>
                                            </div>
                                        </div>
                                    <% } %>

                                    <%-- B. PEMBAYARAN --%>
                                    <% if (sName.equals("MENUNGGU_PEMBAYARAN")) { %>
                                        <div class="mt-2 p-2 border rounded bg-white">
                                            <div class="d-flex justify-content-between align-items-center mb-2">
                                                <small>Tagihan:</small>
                                                <strong class="text-danger">Rp <%= (int)o.getTotalPrice() %></strong>
                                            </div>
                                            
                                            <%-- LOGIKA: Cek apakah customer sudah upload bukti? --%>
                                            <% if (o.getPaymentProof() == null || o.getPaymentProof().isEmpty()) { %>
                                                <%-- KONDISI 1: BELUM UPLOAD --%>
                                                <div class="text-center mb-2">
                                                    <%-- Pastikan gambar qris_laundry.jpeg sudah ada di folder assets/images --%>
                                                    <img src="${pageContext.request.contextPath}/assets/images/qris_laundry.jpeg" 
                                                         alt="Scan QRIS" 
                                                         class="img-fluid border p-1 rounded bg-white shadow-sm" 
                                                         style="width: 150px; cursor: zoom-in;"
                                                         onclick="openLightbox(this.src)">
                                                    <div style="font-size: 0.7rem;" class="text-muted mt-1">BCA: 1234567890</div>
                                                </div>
                                                <button class="btn btn-sm btn-danger w-100 fw-bold" data-bs-target="#uploadPaymentModal_<%= o.getOrderId() %>" data-bs-toggle="modal">
                                                    <i class="bi bi-upload me-1"></i> Upload Bukti
                                                </button>
                                                
                                            <% } else { %>
                                                <%-- KONDISI 2: SUDAH UPLOAD (TAMPILKAN STATUS VERIFIKASI + TOMBOL HAPUS) --%>
                                                <div class="alert alert-success p-2 mb-0 text-center border-0 bg-success-subtle">
                                                    <i class="bi bi-check-circle-fill text-success fs-1 d-block mb-1"></i>
                                                    <div class="fw-bold small text-success">Bukti Terkirim!</div>
                                                    <div class="small text-muted mb-2" style="font-size: 0.75rem;">Menunggu verifikasi admin.</div>
                                                    
                                                    <div class="d-flex justify-content-center gap-3">
                                                        <%-- Tombol Lihat Bukti --%>
                                                        <button class="btn btn-link btn-sm text-decoration-none p-0" 
                                                                style="font-size: 0.8rem;"
                                                                onclick="openLightbox('${pageContext.request.contextPath}/assets/uploads/<%= o.getPaymentProof() %>')">
                                                            <i class="bi bi-eye"></i> Lihat
                                                        </button>

                                                        <%-- Tombol Hapus (Form Kecil) --%>
                                                        <form action="${pageContext.request.contextPath}/customer" method="POST" class="d-inline">
                                                            <input type="hidden" name="action" value="deletePayment">
                                                            <input type="hidden" name="order_id" value="<%= o.getOrderId() %>">
                                                            <button type="submit" class="btn btn-link btn-sm text-danger text-decoration-none p-0" 
                                                                    style="font-size: 0.8rem;"
                                                                    onclick="return confirm('Yakin ingin menghapus bukti ini dan upload ulang?')">
                                                                <i class="bi bi-trash"></i> Hapus
                                                            </button>
                                                        </form>
                                                    </div>
                                                </div>
                                            <% } %>
                                        </div>
                                    <% } %>
                                    
                                    <%-- C. FITUR BARU: JIKA TIBA (MUNCUL TOMBOL RATING) --%>
                                    <% if (sName.equals("TIBA")) { %>
                                        <div class="mt-3 p-3 bg-light rounded border text-center">
                                            <div class="mb-2 text-success fw-bold">
                                                <i class="bi bi-box-seam-fill me-1"></i> Pesanan Diterima
                                            </div>
                                            
                                            <%-- Cek apakah sudah rating atau belum (Pakai DAO) --%>
                                            <% if (!orderDAO.isOrderRated(o.getOrderId())) { %>
                                                <p class="small text-muted mb-2">Mohon berikan penilaian Anda.</p>
                                                <%-- TOMBOL INI AKAN MEMBUKA MODAL RATING --%>
                                                <button class="btn btn-warning w-100 fw-bold shadow-sm" 
                                                        data-bs-toggle="modal" 
                                                        data-bs-target="#ratingModal_<%= o.getOrderId() %>">
                                                    <i class="bi bi-star-fill me-1"></i> BERI ULASAN
                                                </button>
                                            <% } else { %>
                                                <div class="alert alert-warning py-1 px-2 small mb-0">
                                                    <i class="bi bi-star-fill text-warning"></i> Terima kasih atas ulasan Anda!
                                                </div>
                                            <% } %>
                                        </div>
                                    <% } %>
                                <% } // End if isActive %>
                            </div>
                        </div>
                        <% } // End Loop %>
                    </div>
                </div>
                
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Tutup</button>
                </div>
            </div>
        </div>
    </div>
                    
    <%-- 2. MODAL UPLOAD PEMBAYARAN (Form Upload) --%>
    <% if ("MENUNGGU_PEMBAYARAN".equals(currentStatus)) { %>
    <div class="modal fade" id="uploadPaymentModal_<%= o.getOrderId() %>" tabindex="-1">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <form action="${pageContext.request.contextPath}/customer" method="POST" enctype="multipart/form-data">
                    <input type="hidden" name="action" value="uploadPayment">
                    <input type="hidden" name="order_id" value="<%= o.getOrderId() %>">
                    
                    <div class="modal-header">
                        <h5 class="modal-title fw-bold">Upload Bukti Pembayaran</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div class="mb-3">
                            <label class="form-label">Order ID</label>
                            <input type="text" class="form-control" value="#<%= o.getOrderId() %>" readonly>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Total Tagihan</label>
                            <input type="text" class="form-control fw-bold" value="Rp <%= (int)o.getTotalPrice() %>" readonly>
                        </div>
                        <div class="mb-3">
                            <label for="proofFile" class="form-label fw-bold">Pilih Foto Bukti Transfer</label>
                            <input class="form-control" type="file" id="proofFile" name="payment_proof" accept="image/*" required>
                            <div class="form-text">Format: JPG, PNG, JPEG. Max 5MB.</div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Batal</button>
                        <button type="submit" class="btn btn-danger">Kirim Bukti</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
    <% } %>
    
    <%-- 3. MODAL GIVE RATING (FORM) --%>
    <% if ("TIBA".equals(o.getStatus().name()) && !orderDAO.isOrderRated(o.getOrderId())) { %>
    <div class="modal fade" id="ratingModal_<%= o.getOrderId() %>" tabindex="-1">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <form action="${pageContext.request.contextPath}/customer" method="POST">
                    <input type="hidden" name="action" value="giveRating">
                    <input type="hidden" name="order_id" value="<%= o.getOrderId() %>">
                    
                    <div class="modal-header bg-warning text-dark">
                        <h5 class="modal-title fw-bold"><i class="bi bi-star-fill"></i> Beri Ulasan</h5>
                        <%-- Tombol close ini akan menutup rating dan kembali ke dashboard --%>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    
                    <div class="modal-body text-center p-4">
                        <h5 class="mb-2">Bagaimana Layanan Kami?</h5>
                        <p class="text-muted small mb-4">Masukan Anda membantu kami berkembang.</p>
                        
                        <div class="mb-4 p-3 bg-light rounded border">
                            <label class="form-label fw-bold d-block text-start mb-3">Skor (1-10)</label>
                            <div class="d-flex align-items-center justify-content-center gap-3">
                                <span class="fw-bold text-muted">1</span>
                                <input type="range" class="form-range" min="1" max="10" step="1" 
                                       name="score" value="10" 
                                       oninput="document.getElementById('scoreVal_<%= o.getOrderId() %>').innerText = this.value">
                                <span class="fw-bold text-muted">10</span>
                            </div>
                            <div class="mt-2 fw-bold fs-4 text-warning">
                                <span id="scoreVal_<%= o.getOrderId() %>">10</span>/10
                            </div>
                        </div>
                        
                        <div class="mb-3 text-start">
                            <label class="form-label fw-bold">Ulasan Singkat</label>
                            <textarea class="form-control" name="review_text" rows="3" required></textarea>
                        </div>
                    </div>
                    
                    <div class="modal-footer">
                        <%-- Tombol kembali ke status --%>
                        <button type="button" class="btn btn-light" data-bs-toggle="modal" data-bs-target="#statusModal_<%= o.getOrderId() %>">Kembali</button>
                        <button type="submit" class="btn btn-danger">Kirim</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
    <% } %>
    <% }} %>
    
    <%-- ========================================== --%>
    <%-- LIGHTBOX OVERLAY (UNTUK ZOOM QRIS)         --%>
    <%-- ========================================== --%>
    <div id="qrisLightbox" class="lightbox-overlay" onclick="closeLightbox()">
        <span class="close-btn">&times;</span>
        <img class="lightbox-content" id="lightboxImg">
        <div class="lightbox-caption">Klik di mana saja untuk menutup</div>
    </div>

    <%-- SCRIPT KHUSUS LIGHTBOX --%>
    <script>
        function openLightbox(imgSrc) {
            var lightbox = document.getElementById('qrisLightbox');
            var lightboxImg = document.getElementById('lightboxImg');
            
            lightbox.style.display = "flex"; // Tampilkan Overlay
            lightboxImg.src = imgSrc;        // Set gambar sesuai yang diklik
            
            // Animasi kecil agar smooth
            setTimeout(() => {
                lightboxImg.style.transform = "scale(1)";
            }, 10);
        }

        function closeLightbox() {
            var lightbox = document.getElementById('qrisLightbox');
            var lightboxImg = document.getElementById('lightboxImg');
            
            lightboxImg.style.transform = "scale(0.8)"; // Reset animasi
            setTimeout(() => {
                lightbox.style.display = "none";
            }, 200);
        }
    </script>
   
    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/customer.js"></script>
</body>
</html>