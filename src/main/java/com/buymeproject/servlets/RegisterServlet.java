package com.buymeproject.servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import com.buymeproject.database.DBConfig;
import java.io.IOException;
import java.sql.*;

public class RegisterServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String username = request.getParameter("newUsername");
        String password = request.getParameter("newPassword");
        String email = request.getParameter("email");

        try (Connection connection = DBConfig.getConnection()) {
            String query = "INSERT INTO User (Username, Password, Email) VALUES (?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, email);
            int result = ps.executeUpdate();
            if (result > 0) {
                HttpSession session = request.getSession();
                session.setAttribute("message", "Account successfully created. Please log in.");
            } else {
                HttpSession session = request.getSession();
                session.setAttribute("error", "Could not create user. Please try again.");
            }
            response.sendRedirect("login.jsp");
        } catch (SQLException e) {
            HttpSession session = request.getSession();
            session.setAttribute("error", "Database access error during registration.");
            response.sendRedirect("login.jsp");
        }
    }
}
