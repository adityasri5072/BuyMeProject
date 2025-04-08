package com.buymeproject.servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import com.buymeproject.database.DBConfig;

public class RoleManagementServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String action = request.getParameter("action");
        String userIdStr = request.getParameter("userID");

        if (action != null && userIdStr != null) {
            manageUserRole(request, response, action, Integer.parseInt(userIdStr));
        } else {
            displayUsers(request, response);
        }
    }

    private void displayUsers(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try (Connection conn = DBConfig.getConnection()) {
            String sql = "SELECT u.userID, u.Username, (c.userID IS NOT NULL) AS isCustomerRep, " +
                         "(a.userID IS NOT NULL) AS isAdmin " +
                         "FROM User u " +
                         "LEFT JOIN CustomerRepresentative c ON u.userID = c.userID " +
                         "LEFT JOIN Administrator a ON u.userID = a.userID";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            StringBuilder userTable = new StringBuilder();
            while (rs.next()) {
                userTable.append("<tr>");
                userTable.append("<td>").append(rs.getInt("userID")).append("</td>");
                userTable.append("<td>").append(rs.getString("Username")).append("</td>");
                if (rs.getBoolean("isAdmin")) {
                    userTable.append("<td>Admin</td><td></td>");
                } else {
                    userTable.append("<td>").append(rs.getBoolean("isCustomerRep") ? "Customer Rep" : "User").append("</td>");
                    String action = rs.getBoolean("isCustomerRep") ? "Demote" : "Promote";
                    userTable.append("<td><a href='RoleManagementServlet?userID=").append(rs.getInt("userID")).append("&action=").append(action.toLowerCase()).append("'>").append(action).append("</a></td>");
                }
                userTable.append("</tr>");
            }
            request.setAttribute("userTable", userTable.toString());
            request.getRequestDispatcher("/manageUsers.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Database error: " + e.getMessage(), e);
        }
    }

    private void manageUserRole(HttpServletRequest request, HttpServletResponse response, String action, int userID) 
            throws ServletException, IOException {
        try (Connection conn = DBConfig.getConnection()) {
            conn.setAutoCommit(false);
            String sql;
            if ("promote".equals(action)) {
                sql = "INSERT INTO CustomerRepresentative (userID, Name, ContactInfo) SELECT userID, Username, Email FROM User WHERE userID = ? AND userID NOT IN (SELECT userID FROM Administrator)";
            } else {
                sql = "DELETE FROM CustomerRepresentative WHERE userID = ?";
            }
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userID);
            stmt.executeUpdate();
            conn.commit();
            response.sendRedirect("RoleManagementServlet");
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
