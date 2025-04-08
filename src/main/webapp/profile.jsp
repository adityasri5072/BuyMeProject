<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*"%>
<%@ page import="com.buymeproject.database.DBConfig"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>User Profile | BuyMe Vehicles</title>
    <link rel="stylesheet" href="profilestyle.css">
</head>
<body>
    <h1>User Profile</h1>
    <% 
        Integer userId = (Integer) session.getAttribute("userID");
        if (userId == null) {
            response.sendRedirect("login.jsp"); 
            return;
        }

        try (Connection conn = DBConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "SELECT Username, Email, Address FROM User WHERE userID = ?")) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
    %>
    <div class="profile-info">
        <p><strong>Username:</strong> <%= rs.getString("Username") %></p>
        <p><strong>Email:</strong> <%= rs.getString("Email") %></p>
        <p><strong>Address:</strong> <%= rs.getString("Address") != null ? rs.getString("Address") : "Not provided" %></p>
    </div>
    <% 
                } else {
                    out.println("<p>User profile not found.</p>");
                }
            }
        } catch (SQLException e) {
            out.println("<p>Error accessing database: " + e.getMessage() + "</p>");
            e.printStackTrace();
        }
    %>
    <button onclick="window.location.href='dashboard.jsp'">Return to Dashboard</button>
</body>
</html>
