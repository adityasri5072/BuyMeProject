package com.buymeproject.servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import com.buymeproject.database.DBConfig;

public class AdminServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        switch (action) {
            case "fetchReports":
                fetchReports(response);
                break;
            case "listUsers":
                listUsers(response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        try {
            switch (action) {
                case "createUser":
                    createUser(request);
                    response.sendRedirect("adminDashboard.jsp?success=1");
                    break;
                case "deleteUser":
                    deleteUser(request);
                    response.sendRedirect("adminDashboard.jsp?success=1");
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
            }
        } catch (SQLException ex) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + ex.getMessage());
        }
    }

    private void fetchReports(HttpServletResponse response) throws IOException {
        try (Connection connection = DBConfig.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT type, SUM(amount) AS total FROM Reports GROUP BY type");
            ResultSet rs = stmt.executeQuery();
            StringBuilder json = new StringBuilder("[");
            while (rs.next()) {
                if (json.length() > 1) json.append(",");
                json.append(String.format("{\"type\":\"%s\", \"total\":\"%s\"}", rs.getString("type"), rs.getDouble("total")));
            }
            json.append("]");
            response.setContentType("application/json");
            response.getWriter().write(json.toString());
        } catch (SQLException ex) {
            throw new IOException("Database error during fetchReports", ex);
        }
    }

    private void listUsers(HttpServletResponse response) throws IOException {
        try (Connection connection = DBConfig.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT userID, Username, Email FROM User");
            ResultSet rs = stmt.executeQuery();
            StringBuilder html = new StringBuilder("<ul>");
            while (rs.next()) {
                html.append(String.format("<li>%d: %s, %s</li>", rs.getInt("userID"), rs.getString("Username"), rs.getString("Email")));
            }
            html.append("</ul>");
            response.setContentType("text/html");
            response.getWriter().write(html.toString());
        } catch (SQLException ex) {
            throw new IOException("Database error during listUsers", ex);
        }
    }

    private void createUser(HttpServletRequest request) throws SQLException {
        String username = request.getParameter("username");
        String password = request.getParameter("password"); 
        String email = request.getParameter("email");
        try (Connection connection = DBConfig.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO User (Username, Password, Email) VALUES (?, ?, ?)");
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, email);
            stmt.executeUpdate();
        }
    }

    private void deleteUser(HttpServletRequest request) throws SQLException {
        int userId = Integer.parseInt(request.getParameter("userId"));
        try (Connection connection = DBConfig.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM User WHERE userID = ?");
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }
}
