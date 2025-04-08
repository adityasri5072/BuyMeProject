package com.buymeproject.servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import com.buymeproject.database.DBConfig;
import java.io.IOException;
import java.sql.*;

public class DeleteAccountServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
	        throws ServletException, IOException {
	    response.setContentType("text/html;charset=UTF-8");
	    String username = request.getParameter("deleteUsername");
	    String password = request.getParameter("deletePassword");

	    try (Connection connection = DBConfig.getConnection()) {
	        PreparedStatement psCheck = connection.prepareStatement(
	            "SELECT Password FROM User WHERE Username = ?");
	        psCheck.setString(1, username);
	        ResultSet resultSet = psCheck.executeQuery();
	        
	        if (resultSet.next() && resultSet.getString("Password").equals(password)) {
	            PreparedStatement psDelete = connection.prepareStatement(
	                "DELETE FROM User WHERE Username = ?");
	            psDelete.setString(1, username);
	            int result = psDelete.executeUpdate();
	            if (result > 0) {
	                HttpSession session = request.getSession();
	                session.setAttribute("message", "Account successfully deleted.");
	            } else {
	                HttpSession session = request.getSession();
	                session.setAttribute("error", "Error deleting account.");
	            }
	        } else {
	            HttpSession session = request.getSession();
	            session.setAttribute("error", "Invalid username or password.");
	        }
	        response.sendRedirect("login.jsp");
	    } catch (SQLException e) {
	        e.printStackTrace(); 
	        HttpSession session = request.getSession();
	        session.setAttribute("error", "Database access error during account deletion: " + e.getMessage());
	        response.sendRedirect("login.jsp");
	    }
	}

}