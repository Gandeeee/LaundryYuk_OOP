<%-- 
    Document   : login
    Created on : 22 Dec 2025, 22.14.24
    Author     : gandisuastika
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - LaundryYuk</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/login.css" rel="stylesheet">
</head>
<body>
    <div class="login-wrapper">
        <div class="card shadow-lg border-0">
            <div class="card-header bg-danger text-white text-center">
                <h3 class="my-2">LaundryYuk</h3>
            </div>
            
            <div class="card-body p-4 p-md-5">
                <h5 class="card-title text-center mb-4">Silakan Login</h5>
                
                <% if (session.getAttribute("successMessage") != null) { %>
                    <div class="alert alert-success">
                        <%= session.getAttribute("successMessage") %>
                    </div>
                    <% session.removeAttribute("successMessage"); %>
                <% } %>

                <% if (request.getAttribute("errorMessage") != null) { %>
                    <div class="alert alert-danger">
                        <%= request.getAttribute("errorMessage") %>
                    </div>
                <% } %>
                
                <form id="loginForm" action="${pageContext.request.contextPath}/auth" method="POST">
                    <input type="hidden" name="action" value="login">

                    <div class="mb-3">
                        <label for="email" class="form-label">Email</label>
                        <input type="email" class="form-control" id="email" name="email" required>
                    </div>
                    
                    <div class="mb-4"> <label for="password" class="form-label">Password</label>
                        <input type="password" class="form-control" id="password" name="password" required>
                    </div>
                    
                    <div class="d-grid pt-2">
                        <button type="submit" class="btn btn-danger">Login</button>
                    </div>
                </form>

                <div class="text-center mt-4">
                    <small class="text-muted">Belum punya akun?
                       <a href="${pageContext.request.contextPath}/access/register.jsp">Daftar di sini</a>
                    </small>
                </div>
            </div>
        </div>
    </div>

    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/login.js"></script>
</body>
</html>