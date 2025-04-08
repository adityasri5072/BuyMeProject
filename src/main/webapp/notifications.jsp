<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%@ page import="com.buymeproject.database.DBConfig" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Notifications | BuyMe Vehicles</title>
    <link rel="stylesheet" href="notifications.css"> 
</head>
<body>
    <header class="site-header">
        <div class="logo">
            <a href="dashboard.jsp">BuyMe Vehicles</a>
        </div>
     
    </header>

    <main class="main-content">
    <section class="notifications-section">
        <h2>Your Notifications</h2>
        <!-- Return to Dashboard Button -->
        <a href="dashboard.jsp" class="return-home-btn">Return to Home</a>
        <div class="notifications-container">
            <% 
            Integer userId = (Integer) session.getAttribute("userID");
            if (userId == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            try (Connection conn = DBConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                     "SELECT notificationID, message, createdAt FROM Notifications WHERE userID = ? ORDER BY createdAt DESC")) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    out.println("<div class='notification-item'>");
                    out.println("<p>" + rs.getString("message") + "</p>");
                    out.println("<span class='notification-time'>" + rs.getTimestamp("createdAt") + "</span>");
                    out.println("</div>");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                out.println("<p>Error fetching notifications.</p>");
            }
            %>
        </div>
    </section>
</main>


    <footer class="site-footer">
        © 2024 BuyMe, Inc. All rights reserved by Ahnaf, Anant, Aditya, Dan.
    </footer>
</body>
</html>
