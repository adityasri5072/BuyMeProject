package com.buymeproject.servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import com.buymeproject.database.DBConfig;
import java.io.IOException;
import java.sql.*;

public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try (Connection connection = DBConfig.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("SELECT userID, Password FROM User WHERE Username = ?");
            ps.setString(1, username);
            ResultSet resultSet = ps.executeQuery();

            if (resultSet.next()) {
                String storedPassword = resultSet.getString("Password");
                if (password.equals(storedPassword)) {
                    int userId = resultSet.getInt("userID");
                    HttpSession session = request.getSession();
                    session.setAttribute("user", username);
                    session.setAttribute("userID", userId);

                    // Check if the user is an administrator
                    if (checkUserRole(connection, userId, "Administrator")) {
                        session.setAttribute("isAdmin", true);
                    } else {
                        session.setAttribute("isAdmin", false);
                    }

                    // Check if the user is a customer representative
                    if (checkUserRole(connection, userId, "CustomerRepresentative")) {
                        session.setAttribute("isCustomerRep", true);
                    } else {
                        session.setAttribute("isCustomerRep", false);
                    }

                    response.sendRedirect("dashboard.jsp");
                } else {
                    HttpSession session = request.getSession();
                    session.setAttribute("error", "Invalid username or password.");
                    response.sendRedirect("login.jsp");
                }
            } else {
                HttpSession session = request.getSession();
                session.setAttribute("error", "Invalid username or password.");
                response.sendRedirect("login.jsp");
            }
        } catch (SQLException e) {
            HttpSession session = request.getSession();
            session.setAttribute("error", "Database access error. Please contact the administrator.");
            response.sendRedirect("login.jsp");
        }
    }

    private boolean checkUserRole(Connection connection, int userId, String tableName) throws SQLException {
        String query = "SELECT * FROM " + tableName + " WHERE userID = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}