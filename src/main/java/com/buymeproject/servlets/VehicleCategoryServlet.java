package com.buymeproject.servlets;
import com.buymeproject.database.DBConfig;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;


public class VehicleCategoryServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ArrayList<String> categories = new ArrayList<>();
        try (Connection connection = DBConfig.getConnection()) {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT categoryName FROM VehicleCategories");
            while (rs.next()) {
                categories.add(rs.getString("categoryName"));
            }
            request.setAttribute("categories", categories);
            RequestDispatcher dispatcher = request.getRequestDispatcher("dashboard.jsp");
            dispatcher.forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}