package com.buymeproject.servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

import com.buymeproject.database.DBConfig;

public class EditAccountServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("edit".equals(action)) {
            prepareEditForm(request, response);
        } else if ("delete".equals(action)) {
            deleteUser(request, response);
        } else {
            loadUsers(request, response);
        }
    }

    private void loadUsers(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try (Connection conn = DBConfig.getConnection()) {
            String sql = "SELECT userID, Username, Email, Password FROM User";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            request.setAttribute("userList", rs);
            request.getRequestDispatcher("/EditAccounts.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Database error: " + e.getMessage(), e);
        }
    }

    private void prepareEditForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String userID = request.getParameter("userID");
        if (userID != null) {
            try (Connection conn = DBConfig.getConnection()) {
                String sql = "SELECT userID, Username, Email, Password FROM User WHERE userID = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, Integer.parseInt(userID));
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    request.setAttribute("userID", rs.getInt("userID"));
                    request.setAttribute("username", rs.getString("Username"));
                    request.setAttribute("email", rs.getString("Email"));
                    request.setAttribute("password", rs.getString("Password"));
                }
                rs.close();
                request.getRequestDispatcher("/EditUserForm.jsp").forward(request, response);
            } catch (SQLException e) {
                throw new ServletException("Database error: " + e.getMessage(), e);
            }
        } else {
            throw new ServletException("Invalid user ID.");
        }
    }

    private void deleteUser(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String userID = request.getParameter("userID");
        try (Connection conn = DBConfig.getConnection()) {
            String sql = "DELETE FROM User WHERE userID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, Integer.parseInt(userID));
            stmt.executeUpdate();
            response.sendRedirect("EditAccountServlet");
        } catch (SQLException e) {
            throw new ServletException("Database error: " + e.getMessage(), e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("updateUser".equals(action)) {
            updateUser(request, response);
        }
    }

    private void updateUser(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String userID = request.getParameter("userID");
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (username == null || username.isEmpty() || email == null || email.isEmpty() || password == null || password.isEmpty()) {
            request.setAttribute("error", "Username, email, and password cannot be null or empty.");
            prepareEditForm(request, response);  // Send back to the edit form
        } else {
            try (Connection conn = DBConfig.getConnection()) {
                String sql = "UPDATE User SET Username = ?, Email = ?, Password = ? WHERE userID = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, username);
                stmt.setString(2, email);
                stmt.setString(3, password);
                stmt.setInt(4, Integer.parseInt(userID));
                stmt.executeUpdate();
                response.sendRedirect("EditAccountServlet");
            } catch (SQLException e) {
                throw new ServletException("Database error: " + e.getMessage(), e);
            }
        }
    }
}
