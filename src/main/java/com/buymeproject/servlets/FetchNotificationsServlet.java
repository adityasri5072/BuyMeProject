package com.buymeproject.servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import com.buymeproject.database.DBConfig;

public class FetchNotificationsServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Integer userID = (Integer) session.getAttribute("userID");

        if (userID == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try (Connection conn = DBConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT notificationID, message FROM Notifications WHERE userID = ? AND isRead = FALSE")) {
            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();

            StringBuilder notifications = new StringBuilder();
            while (rs.next()) {
                notifications.append("<div class='notification'>")
                             .append(rs.getString("message"))
                             .append("</div>");
            }
            request.setAttribute("notifications", notifications.toString());
            request.getRequestDispatcher("dashboard.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("dashboard.jsp?error=Error fetching notifications.");
        }
    }
}
