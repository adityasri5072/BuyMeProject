package com.buymeproject.servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.*;
import com.buymeproject.database.DBConfig;

@WebServlet("/UserAuctionsServlet")
public class UserAuctionsServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String role = request.getParameter("role"); // 'buyer' or 'seller'

        if (username == null || username.isEmpty()) {
            request.setAttribute("error", "Username is required.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("userAuctions.jsp");
            dispatcher.forward(request, response);
            return;
        }

        String sql;
        if (role.equals("buyer")) {
            sql = "SELECT Auction.*, Vehicle.Make, Vehicle.Model, Vehicle.Year FROM Auction " +
                  "INNER JOIN Bid ON Auction.auctionID = Bid.auctionID " +
                  "INNER JOIN Vehicle ON Auction.vehicleID = Vehicle.vehicleID " +
                  "INNER JOIN User ON Bid.BidderID = User.userID WHERE User.Username = ?";
        } else {
            sql = "SELECT Auction.*, Vehicle.Make, Vehicle.Model, Vehicle.Year FROM Auction " +
                  "INNER JOIN Vehicle ON Auction.vehicleID = Vehicle.vehicleID " +
                  "INNER JOIN User ON Auction.CreatorID = User.userID WHERE User.Username = ?";
        }

        try (Connection conn = DBConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            request.setAttribute("auctionsList", rs);
            request.setAttribute("viewedUsername", username);
            RequestDispatcher dispatcher = request.getRequestDispatcher("userAuctions.jsp");
            dispatcher.forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Database access error", e);
        }
    }
}


