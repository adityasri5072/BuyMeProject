<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Users | BuyMe Vehicles</title>
    <link rel="stylesheet" href="dashboard.css"> 
</head>
<body>
    <header class="site-header">
        <div class="logo">
            <a href="dashboard.jsp">BuyMe Vehicles Admin</a>
        </div>
        <nav class="site-navigation">
            <ul class="nav-links">
                <li><a href="AdminDashboard.jsp">Admin Dashboard</a></li>
                <li><a href="LogoutServlet">Log Out</a></li>
            </ul>
        </nav>
    </header>
    <main class="main-content">
        <h1>Manage Users</h1>
        <table class="user-management-table">
            <thead>
                <tr>
                    <th>User ID</th>
                    <th>Username</th>
                    <th>Role</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                ${userTable}
            </tbody>
        </table>
    </main>
    <footer class="site-footer">
        <p>&copy; 2024 BuyMe, Inc. All rights reserved.</p>
    </footer>
</body>
</html>
