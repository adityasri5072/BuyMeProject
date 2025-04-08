package com.buymeproject.servlets;

import com.buymeproject.database.DBConfig;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DeleteBidsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        loadBids(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String bidIdStr = request.getParameter("bidId");
        if (bidIdStr == null || bidIdStr.trim().isEmpty()) {
            request.setAttribute("error", "Bid ID must not be null or empty");
        } else {
            deleteBid(bidIdStr, request); // Pass the request object to deleteBid
        }
        loadBids(request, response);
    }

    private void loadBids(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try (Connection conn = DBConfig.getConnection()) {
            String sql = "SELECT b.bidID, b.BidAmount, b.BidderID, a.auctionID, a.CurrentBid FROM Bid b " +
                         "JOIN Auction a ON b.auctionID = a.auctionID";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            request.setAttribute("bids", rs);
            request.getRequestDispatcher("/deleteBids.jsp").forward(request, response);
        } catch (SQLException e) {
            request.setAttribute("error", "Database error: " + e.getMessage());
            request.getRequestDispatcher("/deleteBids.jsp").forward(request, response);
        }
    }

    private void deleteBid(String bidIdStr, HttpServletRequest request) { 
        try (Connection conn = DBConfig.getConnection()) {
            String sql = "DELETE FROM Bid WHERE bidID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, Integer.parseInt(bidIdStr));
                int affectedRows = pstmt.executeUpdate();
                if (affectedRows == 0) {
                    request.setAttribute("error", "No bid found with the specified ID.");
                }
            }
        } catch (SQLException e) {
            request.setAttribute("error", "Database error during bid deletion: " + e.getMessage());
        }
    }
}
