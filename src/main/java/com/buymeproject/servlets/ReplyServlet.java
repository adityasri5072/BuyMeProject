package com.buymeproject.servlets;

import com.buymeproject.database.DBConfig;
import java.io.IOException;
import java.sql.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;

public class ReplyServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    HttpSession session = request.getSession(false);
	    if (session != null && session.getAttribute("userID") != null) {
	        Integer userId = (Integer) session.getAttribute("userID");
	        String replyText = request.getParameter("replyText");
	        int questionId = Integer.parseInt(request.getParameter("questionId"));

	        if (replyText == null || replyText.trim().isEmpty()) {
	            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Reply text is missing.");
	            return;
	        }

	        try (Connection conn = DBConfig.getConnection();
	             PreparedStatement ps = conn.prepareStatement("INSERT INTO Replies (user_id, question_id, reply_text) VALUES (?, ?, ?)")) {
	            ps.setInt(1, userId);
	            ps.setInt(2, questionId);
	            ps.setString(3, replyText);
	            int affectedRows = ps.executeUpdate();
	            if (affectedRows > 0) {
	                response.sendRedirect("support.jsp"); // Redirect to refresh the page
	            } else {
	                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to insert the reply.");
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
	        }
	    } else {
	        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User session not found or invalid.");
	    }
	}
}
