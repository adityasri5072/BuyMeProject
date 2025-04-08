package com.buymeproject.servlets;

import com.buymeproject.database.DBConfig;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;


public class QuestionServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    HttpSession session = request.getSession(false); 
	    if (session != null) {
	        Integer userId = (Integer) session.getAttribute("userID"); 
	        if (userId != null) {
	            String question = request.getParameter("question");

	           
	            System.out.println("Received user ID: " + userId);
	            System.out.println("Received question text: " + question);

	            if (question == null || question.trim().isEmpty()) {
	                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Question text is missing.");
	                return;
	            }

	            try {
	               
	                System.out.println("Parsed user ID: " + userId);

	                try (Connection conn = DBConfig.getConnection();
	                     PreparedStatement ps = conn.prepareStatement("INSERT INTO Questions (user_id, question_text) VALUES (?, ?)")) {
	                    ps.setInt(1, userId);
	                    ps.setString(2, question);
	                    int affectedRows = ps.executeUpdate();
	                    if (affectedRows > 0) {
	                        response.sendRedirect("support.jsp"); // Redirect to refresh the page
	                    } else {
	                        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to insert the question.");
	                    }
	                } catch (SQLException e) {
	                    e.printStackTrace();
	                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
	                }
	            } catch (NumberFormatException e) {
	                e.printStackTrace();
	                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID format.");
	            }
	        } else {
	            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User session not found or invalid.");
	        }
	    } else {
	        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User session not found or invalid.");
	    }
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT q.question_id, q.question_text, u.Username FROM Questions q JOIN User u ON q.user_id = u.userID ORDER BY q.created_at DESC")) {
            ResultSet rs = stmt.executeQuery();
            StringBuilder htmlBuilder = new StringBuilder();
            while (rs.next()) {
                htmlBuilder.append("<div><strong>").append(rs.getString("Username")).append(" says:</strong> ")
                           .append(rs.getString("question_text")).append("</div>");
            }
            response.getWriter().write(htmlBuilder.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
        }
    }

}
