<%-- 
    Document   : dashboard
    Created on : 26 Dec 2025, 12.26.40
    Author     : gandisuastika
--%>

<%@page import="com.laundryyuk.model.OrderStatus"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.laundryyuk.model.Order"%>
<%@page import="com.laundryyuk.model.Driver"%>
<%@page import="java.util.List"%>
<%@page import="com.laundryyuk.model.User"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%
    User user = (User) session.getAttribute("user");
    if (user == null || !"ADMIN".equals(user.getRole())) { 
        response.sendRedirect(request.getContextPath() + "/access/login.jsp"); 
        return; 
    }
    
    List<Order> orderList = (List<Order>) request.getAttribute("orderList");
    List<Driver> driverList = (List<Driver>) request.getAttribute("driverList");
    Double totalRevenue = (Double) request.getAttribute("totalRevenue");
    Integer totalOrders = (Integer) request.getAttribute("totalOrders");
    Long pendingOrders = (Long) request.getAttribute("pendingOrders");

    if (totalRevenue == null) totalRevenue = 0.0;
    if (totalOrders == null) totalOrders = 0;
    if (pendingOrders == null) pendingOrders = 0L;
    
    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm");
%>

<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard - LaundryYuk</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.1/dist/chart.umd.min.js"></script>
</head>
<body>
    
<!--BAGIAN NAVBAR-->
    <nav class="sidebar" id="adminSidebar" aria-label="Sidebar Navigation">
        <a href="#" class="sidebar-brand">
            <img src="${pageContext.request.contextPath}/assets/images/logo.png" alt="Logo" class="brand-logo" style="max-height: 50px;">
            <span class="ms-2 fw-bold text-dark">LaundryYuk</span>
        </a>
        <ul class="nav flex-column mt-3" id="sidebar-nav">
            <li class="nav-item"><a class="nav-link active" href="#" data-page="dashboard"><i class="bi bi-grid-fill"></i> Dashboard</a></li>
            <li class="nav-item"><a class="nav-link" href="#" data-page="orders"><i class="bi bi-card-checklist"></i> Manajemen Order</a></li>
            <li class="nav-item"><a class="nav-link" href="#" data-page="drivers"><i class="bi bi-truck"></i> Data Driver</a></li>
            <li class="nav-item"><a class="nav-link" href="#" data-page="laporan"><i class="bi bi-file-earmark-bar-graph"></i> Laporan</a></li>
        </ul>
        <div class="sidebar-footer">
            <a class="nav-link text-danger" href="${pageContext.request.contextPath}/auth?action=logout"><i class="bi bi-box-arrow-left"></i> Logout</a>
        </div>
    </nav>
<!--BAGIAN NAVBAR-->

    <div id="sidebarOverlay" class="sidebar-overlay"></div>
    <main class="content">
        <nav class="top-navbar" aria-label="Top Navigation">
            <div class="d-flex align-items-center">
                <button class="btn btn-light d-lg-none me-3" id="sidebarToggleBtn"><i class="bi bi-list"></i></button>
                <h5 class="mb-0 fw-bold" id="page-title">Dashboard</h5>
            </div>
        </nav>
        
        <% if (session.getAttribute("infoMessage") != null) { %>
        <div class="alert alert-success alert-dismissible fade show m-4 mb-0" role="alert">
            <%= session.getAttribute("infoMessage") %>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
        <% session.removeAttribute("infoMessage"); %>
        <% } %>

        <%-- A. DASHBOARD --%>
        <div id="page-dashboard" class="page-content active">
            <div class="dashboard-stats">
                <div class="stat-card primary"><div class="stat-icon"><i class="bi bi-box-seam"></i></div><div class="stat-content"><h3><%= totalOrders %></h3><p>Total Pesanan</p></div></div>
                <div class="stat-card success"><div class="stat-icon"><i class="bi bi-cash-stack"></i></div><div class="stat-content"><h3>Rp <%= String.format("%,.0f", totalRevenue) %></h3><p>Total Pendapatan</p></div></div>
                <div class="stat-card warning"><div class="stat-icon"><i class="bi bi-arrow-repeat"></i></div><div class="stat-content"><h3><%= pendingOrders %></h3><p>Perlu Diproses</p></div></div>
            </div>
            <div class="card-modern">
                <div class="card-header-modern"><h5>Ringkasan Order Terbaru</h5></div>
                <div class="table-responsive">
                    <table class="table table-modern mb-0">
                        <thead><tr><th>Order ID</th><th>Pelanggan</th><th>Status</th><th>Total</th></tr></thead>
                        <tbody>
                            <% if (orderList != null && !orderList.isEmpty()) { 
                                int limit = Math.min(orderList.size(), 5);
                                for (int i = 0; i < limit; i++) { Order o = orderList.get(i); %>
                            <tr>
                                <td><strong>#<%= o.getOrderId() %></strong></td>
                                <td>Cust: <%= o.getCustomerId() %></td>
                                <td><span class="badge bg-secondary"><%= o.getStatus() %></span></td>
                                <td class="fw-bold">Rp <%= (int) o.getTotalPrice() %></td>
                            </tr>
                            <% }} else { %> <tr><td colspan="4" class="text-center text-muted">Belum ada data</td></tr> <% } %>
                        </tbody>
                    </table>
                </div>
            </div>
                        
            <div class="row g-4 mb-4">
                <div class="col-lg-6">
                    <div class="card-modern h-100">
                        <div class="card-header-modern">
                            <h5>Tren Order</h5>
                        </div>
                        <div class="card-body p-4">
                            <canvas id="dash_trenChart"></canvas>
                        </div>
                    </div>
                </div>
                <div class="col-lg-6"><div class="card-modern h-100">
                        <div class="card-header-modern">
                            <h5>Pendapatan</h5></div>
                        <div class="card-body p-4">
                            <canvas id="dash_revChart"></canvas>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <%-- B. MANAJEMEN ORDER --%>
        <div id="page-orders" class="page-content">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <div><h4 class="fw-bold mb-1">Daftar Pesanan</h4><p class="text-muted mb-0">Kelola semua transaksi laundry.</p></div>
            </div>
            <div class="card-order-management">
                <div class="card-header-order"><h5 class="mb-0"><i class="bi bi-card-checklist me-2"></i> Semua Order</h5></div>
                <div class="table-responsive">
                    
                    <table class="table table-order-management mb-0">
                        <thead><tr><th>ID</th><th>Pelanggan</th><th>Jadwal</th><th>Status</th><th>Total</th><th class="text-end">Aksi</th></tr></thead>
                        <tbody>
                             <% if (orderList != null) { for(Order o : orderList) { 
                                 // 1. LOGIKA STATUS
                                 String currentStatus = o.getStatus().name();
                                 boolean isWaitingDriver = "MENUNGGU_DIJEMPUT".equals(currentStatus);
                                 boolean isBillRequired = "CUCIAN_DIAMBIL".equals(currentStatus); 
                                 
                                 // 2. BADGE WARNA
                                 String badgeClass = "bg-primary";
                                 if(currentStatus.equals("SELESAI_DICUCI")) badgeClass = "bg-dark";
                                 else if(currentStatus.equals("PROSES_PENCUCIAN")) badgeClass = "bg-success";
                                 else if(currentStatus.equals("MENUNGGU_PEMBAYARAN")) badgeClass = "bg-warning text-dark";
                             %>
                            <tr>
                                <td><span class="fw-bold text-primary">#<%= o.getOrderId() %></span></td>
                                <td>
                                    <div class="fw-bold">Cust: <%= o.getCustomerId() %></div>
                                    <small class="text-muted"><%= o.getNotes() != null ? o.getNotes() : "-" %></small>
                                </td>
                                <td><div class="fw-bold"><%= o.getPickupSchedule() != null ? sdf.format(o.getPickupSchedule()) : "WALK-IN" %></div></td>
                                <td><span class="status-badge <%= badgeClass %>"><%= o.getStatus().name().replace("_", " ") %></span></td>
                                <td><% if(o.isPaid()) { %> <span class="payment-badge bg-success">LUNAS</span> <% } else { %> <span class="payment-badge bg-warning">TAGIHAN: <%= (int)o.getTotalPrice() %></span> <% } %></td>
                                <td class="text-end action-buttons-order">
                                    <%-- 1: JEMPUT (Muncul jika Menunggu Dijemput) --%>
                                    <% if(isWaitingDriver) { %>
                                        <button class="btn-action btn-tagihan" 
                                                data-bs-toggle="modal" 
                                                data-bs-target="#assignDriverModal" 
                                                data-order-id="<%= o.getOrderId() %>"
                                                data-status-target="DRIVER_OTW"
                                                data-modal-title="Tugaskan Driver (Jemput)"
                                                data-modal-info="Status akan berubah menjadi <b>DRIVER OTW</b>.">
                                            <i class="bi bi-truck"></i> Jemput
                                        </button>
                                    <% } %>
                                    
                                    <%-- 2: ANTAR (Muncul jika Selesai Dicuci) --%>
                                    <% if("SELESAI_DICUCI".equals(currentStatus)) { %>
                                        <button class="btn-action btn-tagihan" 
                                                data-bs-toggle="modal" 
                                                data-bs-target="#assignDriverModal" 
                                                data-order-id="<%= o.getOrderId() %>"
                                                data-status-target="DIKIRIM"
                                                data-modal-title="Tugaskan Driver (Antar)"
                                                data-modal-info="Status akan berubah menjadi <b>DIKIRIM</b>.">
                                            <i class="bi bi-bicycle"></i> Antar
                                        </button>
                                    <% } %>
                                    
                                    <%-- 3: INPUT TAGIHAN --%>
                                    <% if(isBillRequired) { %>
                                        <button class="btn-action btn-success border-success text-success" 
                                                data-bs-toggle="modal" 
                                                data-bs-target="#billModal_<%= o.getOrderId() %>">
                                            <i class="bi bi-calculator"></i> Input Tagihan
                                        </button>
                                    <% } %>
                                    <% if ("MENUNGGU_PEMBAYARAN".equals(o.getStatus().name())) { %>
                                        <button class="btn-action btn-success border-success text-success" 
                                                data-bs-toggle="modal" 
                                                data-bs-target="#verifyModal_<%= o.getOrderId() %>">
                                            <i class="bi bi-shield-check"></i> Verify
                                        </button>
                                    <% } %>

                                    <%-- 4: STATUS --%>
                                    <button class="btn-action btn-status" data-bs-toggle="modal" data-bs-target="#statusModal_<%= o.getOrderId() %>">
                                        <i class="bi bi-list-check"></i> Status
                                    </button>
                                        
                                    <%-- 5: Hapus --%>
                                    <a href="${pageContext.request.contextPath}/admin?action=deleteOrder&order_id=<%= o.getOrderId() %>" 
                                       class="btn-action text-danger border-danger me-1"
                                       onclick="return confirm('PERINGATAN: Yakin ingin menghapus Order #<%= o.getOrderId() %>? Data akan hilang permanen!')"
                                       title="Hapus Order">
                                       <i class="bi bi-trash-fill"></i>
                                    </a>
                                </td>
                            </tr>
                            <% }} %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
        
        <%-- C. DRIVERS & D. LAPORAN --%>
        <div id="page-drivers" class="page-content">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h4 class="fw-bold mb-1">Data Driver</h4>
                    <p class="text-muted mb-0">Kelola armada kurir laundry.</p>
                </div>
                <button class="btn-add-order" data-bs-toggle="modal" data-bs-target="#driverModal" data-action="create">
                    <i class="bi bi-person-plus-fill"></i> Tambah Driver
                </button>
            </div>

            <div class="card-order-management">
                <div class="card-header-order">
                    <h5 class="mb-0"><i class="bi bi-truck me-2"></i> List Driver</h5>
                </div>
                <div class="table-responsive">
                    <table class="table table-order-management mb-0">
                        <thead>
                            <tr>
                                <th>ID Driver</th>
                                <th>Informasi</th>
                                <th>Ketersediaan</th>
                                <th class="text-end">Aksi</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (driverList != null) { for(Driver d : driverList) { %>
                            <tr>
                                <td><span class="badge bg-light text-dark border">#<%= d.getDriverId() %></span></td>
                                <td>
                                    <div class="fw-bold text-dark"><%= d.getDriverName() %></div>
                                    <div class="text-muted small"><i class="bi bi-whatsapp text-success"></i> <%= d.getDriverPhone() %></div>
                                </td>
                                <td>
                                    <% if(d.isAvailable()) { %> 
                                        <span class="status-badge bg-success"><i class="bi bi-check-circle"></i> Tersedia</span> 
                                    <% } else { %> 
                                        <span class="status-badge bg-secondary"><i class="bi bi-slash-circle"></i> Sibuk/Off</span> 
                                    <% } %>
                                </td>
                                <td class="text-end">
                                    <button class="btn-action" 
                                            data-bs-toggle="modal" data-bs-target="#driverModal" 
                                            data-action="update" 
                                            data-driver-id="<%= d.getDriverId() %>" 
                                            data-name="<%= d.getDriverName() %>" 
                                            data-phone="<%= d.getDriverPhone() %>" 
                                            data-available="<%= d.isAvailable() %>">
                                        <i class="bi bi-pencil-square"></i> Edit
                                    </button>
                                    <a href="${pageContext.request.contextPath}/admin?action=deleteDriver&driver_id=<%= d.getDriverId() %>" 
                                       class="btn-action text-danger border-danger ms-1" 
                                       onclick="return confirm('Yakin ingin menghapus driver ini?')">
                                        <i class="bi bi-trash-fill"></i> Hapus
                                    </a>
                                </td>
                            </tr>
                            <% }} %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
        
        <div id="page-laporan" class="page-content">
            <%-- DATA JEMBATAN UNTUK CHART JS --%>
            <input type="hidden" id="chartDataLabels" value='<%= request.getAttribute("chartLabels") %>'>
            <input type="hidden" id="chartDataOrder" value='<%= request.getAttribute("chartOrderData") %>'>
            <input type="hidden" id="chartDataRevenue" value='<%= request.getAttribute("chartRevenueData") %>'>
            
            <%-- HEADER LAPORAN DENGAN TOMBOL EXPORT --%>
            <div class="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h4 class="fw-bold mb-1">Laporan Keuangan</h4>
                    <p class="text-muted mb-0">Ringkasan performa dan export data.</p>
                </div>
                <%-- TOMBOL EXPORT CSV --%>
                <a href="${pageContext.request.contextPath}/admin?action=exportCSV" 
                   class="btn-add-order text-decoration-none">
                    <i class="bi bi-file-earmark-spreadsheet-fill"></i> Download Laporan
                </a>
            </div>
            
            <div class="dashboard-stats">
                <div class="stat-card primary"><div class="stat-icon"><i class="bi bi-box-seam"></i></div><div class="stat-content"><h3><%= totalOrders %></h3><p>Total Pesanan</p></div></div>
                <div class="stat-card success"><div class="stat-icon"><i class="bi bi-cash-stack"></i></div><div class="stat-content"><h3>Rp <%= String.format("%,.0f", totalRevenue) %></h3><p>Total Pendapatan</p></div></div>
                <div class="stat-card warning"><div class="stat-icon"><i class="bi bi-arrow-repeat"></i></div><div class="stat-content"><h3><%= pendingOrders %></h3><p>Perlu Diproses</p></div></div>
            </div>
            
            <div class="row g-4">
                <div class="col-lg-6"><div class="card-modern h-100"><div class="card-header-modern"><h5>Tren Order</h5></div><div class="card-body p-4"><canvas id="lap_trenChart"></canvas></div></div></div>
                <div class="col-lg-6"><div class="card-modern h-100"><div class="card-header-modern"><h5>Pendapatan</h5></div><div class="card-body p-4"><canvas id="lap_revChart"></canvas></div></div></div>
            </div>
        </div>

    </main>

    <%-- ======================================================== --%>
    <%-- BAGIAN GENERATE MODAL                                    --%>
    <%-- ======================================================== --%>
    <% if (orderList != null) { 
        for(Order o : orderList) { 
            String currentStatus = o.getStatus().name();
            boolean isBillRequired = "CUCIAN_DIAMBIL".equals(currentStatus);
    %>

    <%-- 1. MODAL STATUS --%>
    <div class="modal fade" id="statusModal_<%= o.getOrderId() %>" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <form action="${pageContext.request.contextPath}/admin" method="POST">
                    <input type="hidden" name="action" value="updateOrder">
                    <input type="hidden" name="order_id" value="<%= o.getOrderId() %>">
                    <div class="modal-header">
                        <h5 class="modal-title fw-bold">Update Status #<%= o.getOrderId() %></h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <p class="fw-bold mb-3">Pilih Status Selanjutnya:</p>
                        <% 
                           // LOGIKA JAVA VALIDASI STATUS
                           OrderStatus[] allStatus = OrderStatus.values();
                           int currentIndex = -1;
                           for(int i=0; i<allStatus.length; i++) { if(allStatus[i].name().equals(currentStatus)) { currentIndex = i; break; } }

                           for (int i = 0; i < allStatus.length; i++) {
                               String sName = allStatus[i].name();
                               boolean isDisabled = true; boolean isChecked = (i == currentIndex);
                               
                                   // 1. Aturan Dasar: Hanya boleh pilih status saat ini atau 1 langkah ke depan
                                   if (i == currentIndex || i == currentIndex + 1) isDisabled = false;
                                   
                                   // 2. EXCEPTION: Kunci status DRIVER_OTW (Harus via tombol Jemput)
                                   if (currentStatus.equals("MENUNGGU_DIJEMPUT") && sName.equals("DRIVER_OTW")) {
                                       isDisabled = true;
                                   }
                                   
                                   // 3. EXCEPTION: Kunci status MENUNGGU_PEMBAYARAN (Harus via tombol Input Tagihan)
                                   if (currentStatus.equals("CUCIAN_DIAMBIL") && sName.equals("MENUNGGU_PEMBAYARAN") && o.getTotalPrice() <= 0) {
                                       isDisabled = true;
                                   }

                                   // 4. EXCEPTION BARU (ANTI-BYPASS): Kunci status PROSES_PENCUCIAN 
                                   // Admin TIDAK BOLEH ubah manual. Wajib via tombol "Verify".
                                   if (currentStatus.equals("MENUNGGU_PEMBAYARAN") && sName.equals("PROSES_PENCUCIAN")) {
                                       isDisabled = true;
                                   }
                                   
                                   // 5. EXCEPTION BARU: Kunci status DIKIRIM (Harus via tombol ANTAR / Pilih Driver)
                                   if (currentStatus.equals("SELESAI_DICUCI") && sName.equals("DIKIRIM")) {
                                       isDisabled = true;
                                   }        

                                   // 5. Aturan Batal: Boleh dibatalkan kapan saja (kecuali sudah selesai/tiba)
                                   if (sName.equals("DIBATALKAN") && !currentStatus.equals("SELESAI_DICUCI") && !currentStatus.equals("TIBA")) {
                                       isDisabled = false;
                                   }
                        %>
                        <div class="form-check mb-2">
                                <input class="form-check-input" type="radio" name="new_status" value="<%= sName %>" 
                                       id="opt_<%= o.getOrderId() %>_<%= i %>" 
                                       <%= isChecked ? "checked" : "" %> 
                                       <%= isDisabled ? "disabled" : "" %>>
                                       
                                <label class="form-check-label <%= isDisabled ? "text-muted" : "fw-bold" %>" for="opt_<%= o.getOrderId() %>_<%= i %>">
                                    <%= sName.replace("_", " ") %>
                                </label>
                                
                                <%-- PESAN PANDUAN (HELPER TEXT) --%>
                                <% if(isDisabled && currentStatus.equals("MENUNGGU_DIJEMPUT") && sName.equals("DRIVER_OTW")) { %>
                                    <div class="text-danger small ms-2"><i class="bi bi-info-circle"></i> Gunakan tombol 'JEMPUT'.</div>
                                    
                                <% } else if (isDisabled && currentStatus.equals("CUCIAN_DIAMBIL") && sName.equals("MENUNGGU_PEMBAYARAN")) { %>
                                    <div class="text-danger small ms-2"><i class="bi bi-calculator"></i> Wajib 'Input Tagihan' dulu.</div>
                                    
                                <%-- PESAN PERINGATAN BARU --%>
                                <% } else if (isDisabled && currentStatus.equals("MENUNGGU_PEMBAYARAN") && sName.equals("PROSES_PENCUCIAN")) { %>
                                    <div class="text-danger small ms-2"><i class="bi bi-shield-lock"></i> Wajib lewat tombol 'VERIFY'.</div>
                                
                                <%-- PESAN PERINGATAN BARU UNTUK DIKIRIM --%>
                                <% } else if (isDisabled && currentStatus.equals("SELESAI_DICUCI") && sName.equals("DIKIRIM")) { %>
                                     <div class="text-danger small ms-2"><i class="bi bi-bicycle"></i> Gunakan tombol 'ANTAR' (Pilih Driver).</div>
                                <% } %>
                        </div>
                        <% } %>
                    </div>
                    <div class="modal-footer"><button type="submit" class="btn btn-danger">Simpan Perubahan</button></div>
                </form>
            </div>
        </div>
    </div>

    <%-- 2. MODAL INPUT TAGIHAN (Hanya dirender jika status CUCIAN_DIAMBIL) --%>
    <% if(isBillRequired) { 
         // 1. AMBIL DATA REAL DARI DATABASE
         String svcType = o.getServiceType();
         if(svcType == null) svcType = "";
         
         double fixedPrice = 0;
         boolean isSatuan = false;

         // 2. MAPPING HARGA
         // Kiloan
         if (svcType.equals("Cuci Kering")) fixedPrice = 5000;
         else if (svcType.equals("Cuci Kering Lipat")) fixedPrice = 5500;
         else if (svcType.equals("Cuci Kering Express 6 Jam")) fixedPrice = 6000;
         else if (svcType.equals("Cuci Kering Express 3 Jam")) fixedPrice = 7000;
         else if (svcType.equals("Cuci Setrika 2 Hari")) fixedPrice = 8000;
         else if (svcType.equals("Cuci Setrika 3 Hari")) fixedPrice = 7000;
         else if (svcType.equals("Cuci Setrika 4 Hari")) fixedPrice = 6500;
         else if (svcType.equals("Cuci Setrika Express 6 Jam")) fixedPrice = 13000;
         else if (svcType.equals("Cuci Setrika Express 1 Jam")) fixedPrice = 10000;
         else if (svcType.equals("Setrika Saja")) fixedPrice = 5000;
         else if (svcType.equals("Kering Saja")) fixedPrice = 4000;
         
         // Satuan
         else if (svcType.startsWith("Satuan")) {
             isSatuan = true;
             if (svcType.equals("Satuan Bedcover")) fixedPrice = 35000; // Ambil rata-rata/terendah
             else if (svcType.equals("Satuan Sprei")) fixedPrice = 15000;
             else if (svcType.equals("Satuan Tas")) fixedPrice = 25000;
             else if (svcType.equals("Satuan Boneka")) fixedPrice = 10000;
             else if (svcType.equals("Satuan Sepatu")) fixedPrice = 25000;
             else if (svcType.equals("Satuan Karpet")) fixedPrice = 30000;
         }
    %>
    <div class="modal fade text-start" id="billModal_<%= o.getOrderId() %>" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <form action="${pageContext.request.contextPath}/admin" method="POST">
                    <input type="hidden" name="action" value="inputBill">
                    <input type="hidden" name="order_id" value="<%= o.getOrderId() %>">
                    <%-- Kirim harga satuan ke backend untuk dikalikan --%>
                    <input type="hidden" name="price_per_kg" value="<%= fixedPrice %>"> 
                    <div class="modal-header bg-success text-white">
                        <h5 class="modal-title"><i class="bi bi-calculator"></i> Input Tagihan #<%= o.getOrderId() %></h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <%-- TAMPILKAN DATA DARI DATABASE --%>
                        <div class="mb-3">
                            <label class="fw-bold" for="layananInput">Layanan Terpilih (Data Database)</label>
                            <input type="text" class="form-control bg-light fw-bold text-dark" 
                                   value="<%= svcType %>" readonly>
                            <div class="form-text">Tarif Sistem: Rp <%= (int)fixedPrice %> / <%= isSatuan ? "Pcs" : "Kg" %></div>
                        </div>

                        <div class="mb-3">
                            <label class="fw-bold" for="inputAktual"><%= isSatuan ? "Jumlah (Pcs)" : "Berat Aktual (Kg)" %></label>
                            <%-- Input dengan Script Kalkulator Sederhana --%>
                            <input type="number" step="0.1" class="form-control" name="weight" 
                                   placeholder="Masukkan angka..." required
                                   oninput="let val = parseFloat(this.value) || 0; let total = val * <%= fixedPrice %>; document.getElementById('total_<%= o.getOrderId() %>').innerText = 'Rp ' + total.toLocaleString('id-ID')">
                        </div>
                        <div class="p-3 bg-light rounded border text-center">
                            <label class="small text-muted" for="totalTagihan">Total Tagihan:</label>
                            <h3 class="fw-bold text-success mb-0" id="total_<%= o.getOrderId() %>">Rp 0</h3>
                        </div>
                        
                        <% if(fixedPrice == 0) { %>
                        <div class="alert alert-danger small mt-2">
                            <i class="bi bi-exclamation-circle"></i> Layanan tidak dikenali sistem. Cek database.
                        </div>
                        <% } %>
                    </div>
                    <div class="modal-footer">
                        <button type="submit" class="btn btn-success" <%= fixedPrice == 0 ? "disabled" : "" %>>Simpan & Kirim Tagihan</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
    <% } %>
    
    <%-- MODAL VERIFY PAYMENT --%>
    <% if ("MENUNGGU_PEMBAYARAN".equals(o.getStatus().name())) { 
         String rawPhone = "";
         if(o.getPickupAddress().contains("(")) {
             rawPhone = o.getPickupAddress().substring(o.getPickupAddress().lastIndexOf("(") + 1, o.getPickupAddress().lastIndexOf(")"));
         }
         boolean hasProof = (o.getPaymentProof() != null && !o.getPaymentProof().isEmpty());
    %>
    <div class="modal fade" id="verifyModal_<%= o.getOrderId() %>" tabindex="-1">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                
                <div class="modal-header bg-warning-subtle">
                    <h5 class="modal-title fw-bold"><i class="bi bi-cash-coin"></i> Cek Pembayaran</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>

                <div class="modal-body text-center p-4">
                    <p class="mb-1">Order ID: <strong>#<%= o.getOrderId() %></strong></p>
                    <p class="text-muted mb-3">Total Tagihan: <span class="text-danger fw-bold">Rp <%= (int)o.getTotalPrice() %></span></p>
                    
                    <%-- KONDISI 1: CUSTOMER BELUM UPLOAD --%>
                    <% if (!hasProof) { %>
                        <div class="alert alert-secondary d-flex align-items-center justify-content-center gap-2 py-4" role="alert">
                            <i class="bi bi-hourglass-split fs-1 text-secondary"></i>
                            <div class="text-start">
                                <strong class="d-block">Belum Ada Bukti</strong>
                                <span class="small">Customer belum mengupload foto bukti transfer.</span>
                            </div>
                        </div>
                        
                        <a href="https://wa.me/<%= rawPhone %>?text=Halo%20kak,%20mohon%20segera%20upload%20bukti%20pembayaran%20untuk%20Order%20#<%= o.getOrderId() %>" 
                           target="_blank" class="btn btn-outline-success btn-sm mt-2">
                            <i class="bi bi-whatsapp"></i> Ingatkan Customer
                        </a>

                    <%-- KONDISI 2: CUSTOMER SUDAH UPLOAD --%>
                    <% } else { %>
                        <div class="bg-light p-2 border rounded d-inline-block mb-3 position-relative">
                            <a href="${pageContext.request.contextPath}/assets/uploads/<%= o.getPaymentProof() %>" target="_blank">
                                <img src="${pageContext.request.contextPath}/assets/uploads/<%= o.getPaymentProof() %>"
                                     class="img-fluid rounded"
                                     style="max-height: 350px; cursor: zoom-in;"
                                     alt="Bukti Pembayaran">
                            </a>
                            <div class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">
                                Baru
                            </div>
                        </div>
                        <p class="small text-muted mb-0"><i class="bi bi-search"></i> Klik gambar untuk memperbesar</p>
                    <% } %>
                </div>

                <%-- FOOTER: TOMBOL AKSI --%>
                <div class="modal-footer justify-content-center bg-light">
                    <% if (!hasProof) { %>
                        <%-- Jika belum upload, Admin hanya bisa tutup --%>
                        <button type="button" class="btn btn-secondary w-100" data-bs-dismiss="modal">Tutup (Menunggu Customer)</button>
                    <% } else { %>
                        <%-- Jika sudah upload, Admin HARUS memverifikasi --%>
                        <div class="d-flex gap-2 w-100">
                            <%-- TOMBOL TIDAK SAH --%>
                            <form action="${pageContext.request.contextPath}/admin" method="POST" class="w-50">
                                <input type="hidden" name="action" value="verifyPayment">
                                <input type="hidden" name="order_id" value="<%= o.getOrderId() %>">
                                <input type="hidden" name="decision" value="invalid">
                                <input type="hidden" name="customer_phone" value="<%= rawPhone %>">
                                <button type="submit" class="btn btn-outline-danger w-100">
                                    <i class="bi bi-x-circle"></i> Tolak / Buram
                                </button>
                            </form>
                            <%-- TOMBOL SAH --%>
                            <form action="${pageContext.request.contextPath}/admin" method="POST" class="w-50">
                                <input type="hidden" name="action" value="verifyPayment">
                                <input type="hidden" name="order_id" value="<%= o.getOrderId() %>">
                                <input type="hidden" name="decision" value="valid">
                                <button type="submit" class="btn btn-success w-100 text-white">
                                    <i class="bi bi-check-circle"></i> SAH / Lanjut
                                </button>
                            </form>
                        </div>
                    <% } %>
                </div>
            </div>
        </div>
    </div>
    <% } %>
    <% }} %>

    <%-- MODAL MODAL STATIS LAINNYA --%>
    <div class="modal fade" id="assignDriverModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <form action="${pageContext.request.contextPath}/admin" method="POST">
                    <input type="hidden" name="action" value="assignDriver">
                    <input type="hidden" name="order_id" id="assign_order_id">
                    <%-- INPUT HIDDEN UNTUK MENENTUKAN STATUS (DRIVER_OTW / DIKIRIM) --%>
                    <input type="hidden" name="target_status" id="assign_target_status">
                    
                    <div class="modal-header bg-primary text-white">
                        <%-- ID JUDUL DINAMIS --%>
                        <h5 class="modal-title" id="assign_modal_title"><i class="bi bi-truck"></i> Tugaskan Driver</h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div class="mb-3">
                            <label class="fw-bold" for="assign_order_id_view">Order ID</label>
                            <input type="text" class="form-control" id="assign_order_id_view" readonly>
                        </div>
                        <div class="mb-3">
                            <label class="fw-bold" for="assign_driver_select">Pilih Driver Tersedia</label>
                            <select class="form-select" name="driver_id" id="assign_driver_select" required>
                                <option value="" selected disabled>-- Pilih Driver --</option>
                                <% if (driverList != null) { for (Driver d : driverList) { if (d.isAvailable()) { %>
                                <option value="<%= d.getDriverId() %>"><%= d.getDriverName() %></option>
                                <% }}} %>
                            </select>
                        </div>
                        <%-- INFO TEXT DINAMIS --%>
                        <div class="alert alert-info small m-0" id="assign_modal_info">Status otomatis berubah.</div>
                    </div>
                    <div class="modal-footer"><button type="submit" class="btn btn-primary">Tugaskan</button></div>
                </form>
            </div>
        </div>
    </div>
    
    <div class="modal fade" id="driverModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <form action="${pageContext.request.contextPath}/admin" method="POST" id="formDriver">
                    <input type="hidden" name="action" value="saveDriver">
                    <input type="hidden" name="driver_id" id="driver_id_input">
                    <div class="modal-header"><h5 class="modal-title fw-bold" id="driverModalTitle">Kelola Driver</h5><button type="button" class="btn-close" data-bs-dismiss="modal"></button></div>
                    <div class="modal-body">
                        <div class="mb-3">
                            <label class="fw-bold" for="driverName">Nama Driver</label>
                            <input type="text" class="form-control" id="driverName" name="name" required>
                        </div>
                        <div class="mb-3">
                            <label class="fw-bold" for="driverPhone">Nomor WhatsApp</label>
                            <input type="tel" class="form-control" id="driverPhone" name="phone" required>
                        </div>
                        <div class="form-check form-switch p-3 border rounded">
                            <input class="form-check-input" type="checkbox" name="isAvailable" id="driverAvailable" checked>
                            <label class="fw-bold" for="driverAvailable">Tersedia</label>
                        </div>
                    </div>
                    <div class="modal-footer"><button type="submit" class="btn btn-danger">Simpan</button></div>
                </form>
            </div>
        </div>
    </div>
                    
    <%-- ALTERNATIF: TAMPILKAN TOMBOL WA --%>
    <% if (session.getAttribute("openWaUrl") != null) { %>
        <div class="alert alert-warning alert-dismissible fade show fixed-top m-3 shadow" role="alert" style="z-index: 9999;">
            <strong>Pembayaran Ditolak!</strong> Silakan hubungi customer:
            <a href="<%= session.getAttribute("openWaUrl") %>" target="_blank" class="btn btn-success btn-sm ms-2">
                <i class="bi bi-whatsapp"></i> Chat Customer Sekarang
            </a>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <% session.removeAttribute("openWaUrl"); %>
    <% } %>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/admin.js"></script>
</body>
</html>