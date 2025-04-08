package com.buymeproject.servlets;
import com.buymeproject.database.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

public class SetAlertsServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
	        throws ServletException, IOException {
	    HttpSession session = request.getSession();
	    Integer userID = (Integer) session.getAttribute("userID");
	    String make = request.getParameter("make").toUpperCase();
	    String model = request.getParameter("model").toUpperCase();
	    String year = request.getParameter("year").trim();
	    String color = request.getParameter("color").trim();

	    
	    int defaultCategoryID = 1; 

	    if (userID == null) {
	        response.sendRedirect("login.jsp");
	        return;
	    }

	    try (Connection conn = DBConfig.getConnection()) {
	        String sql = "INSERT INTO UserAlerts (userID, categoryID, Make, Model, Year, Color, isActive) VALUES (?, ?, ?, ?, ?, ?, TRUE)";
	        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	            stmt.setInt(1, userID);
	            stmt.setInt(2, defaultCategoryID); 
	            stmt.setString(3, make);
	            stmt.setString(4, model);
	            stmt.setObject(5, year.isEmpty() ? null : Integer.valueOf(year)); 
	            stmt.setString(6, color.isEmpty() ? null : color);
	            stmt.executeUpdate();
	        }
	        response.sendRedirect("dashboard.jsp?success=Alert set successfully for " + make + " " + model);
	    } catch (SQLException e) {
	        e.printStackTrace();
	        response.sendRedirect("dashboard.jsp?error=Error setting alert: " + e.getMessage());
	    }
	}



}
