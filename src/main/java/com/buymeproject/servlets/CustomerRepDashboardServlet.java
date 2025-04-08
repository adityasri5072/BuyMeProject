package com.buymeproject.servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import com.buymeproject.database.DBConfig;
import java.io.IOException;
import java.sql.*;

public class CustomerRepDashboardServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userID");

        if (userId == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try (Connection connection = DBConfig.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM CustomerRepresentative WHERE userID = ?");
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                request.getRequestDispatcher("customerRepDashboard.jsp").forward(request, response);
            } else {
                response.sendRedirect("dashboard.jsp"); // Redirect to regular dashboard if not a rep
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("dashboard.jsp"); // Redirect on error
        }
    }
}
