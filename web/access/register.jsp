<%-- 
    Document   : register
    Created on : 22 Dec 2025, 22.12.14
    Author     : gandisuastika
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Daftar Akun - LaundryYuk</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/register.css" rel="stylesheet">
</head>
<body>
    <div class="login-wrapper">
        <div class="card shadow-lg border-0">
            <div class="card-header bg-danger text-white text-center">
                <h3 class="my-2">LaundryYuk</h3>
            </div>
            
            <div class="card-body p-4 p-md-5">
                <h5 class="card-title text-center mb-4">Buat Akun Customer Baru</h5>
                
                <%-- MENAMPILKAN PESAN ERROR DARI SERVLET --%>
                <% if (request.getAttribute("errorMessage") != null) { %>
                    <div class="alert alert-danger">
                        <%= request.getAttribute("errorMessage") %>
                    </div>
                <% } %>
                
                <form id="registerForm" action="${pageContext.request.contextPath}/auth" method="POST" novalidate>
                    <input type="hidden" name="action" value="register">

                    <div class="mb-3">
                        <label for="nama" class="form-label">Nama Lengkap</label>
                        <%-- Tambahan value: Agar teks tidak hilang saat terjadi error --%>
                        <input type="text" class="form-control" id="nama" name="name" 
                               value="<%= request.getAttribute("oldName") != null ? request.getAttribute("oldName") : "" %>" required>
                        <div class="invalid-feedback">Nama lengkap wajib diisi.</div>
                    </div>

                    <div class="mb-3">
                        <label for="email" class="form-label">Email</label>
                        <%-- Tambahan value: Agar teks tidak hilang saat terjadi error --%>
                        <input type="email" class="form-control" id="email" name="email" 
                               value="<%= request.getAttribute("oldEmail") != null ? request.getAttribute("oldEmail") : "" %>" required>
                        <div class="invalid-feedback">Email tidak valid.</div>
                    </div>
                    
                    <div class="mb-3">
                        <label for="password" class="form-label">Password</label>
                        <input type="password" class="form-control" id="password" name="password" required minlength="8">
                        <div class="invalid-feedback">Password minimal 8 karakter.</div>
                    </div>

                    <%-- BAGIAN BARU: KONFIRMASI PASSWORD (WAJIB ADA UNTUK VALIDASI JAVA) --%>
                    <div class="mb-3">
                        <label for="konfirmasiPassword" class="form-label">Konfirmasi Password</label>
                        <input type="password" class="form-control" id="konfirmasiPassword" name="confirmPassword" required>
                        <div class="invalid-feedback">Password harus sama.</div>
                    </div>

                    <div class="d-grid pt-2">
                        <button type="submit" class="btn btn-danger">Daftar</button>
                    </div>
                </form>

                <div class="text-center mt-4">
                    <small class="text-muted">Sudah punya akun?
                        <a href="${pageContext.request.contextPath}/access/login.jsp">Login di sini</a>
                    </small>
                </div>
            </div>
        </div>
    </div>

    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <%-- JS tetap diload tapi logikanya sudah kita kosongkan/kurangi --%>
    <script src="${pageContext.request.contextPath}/assets/js/register.js"></script>
</body>
</html>