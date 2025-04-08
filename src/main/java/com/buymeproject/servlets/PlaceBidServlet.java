package com.buymeproject.servlets;

import com.buymeproject.database.DBConfig;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PlaceBidServlet extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        String auctionIdStr = request.getParameter("auctionId");
        String userIdStr = request.getParameter("userId");
        String bidAmountStr = request.getParameter("bidAmount");
        String maxAutoBidAmountStr = request.getParameter("maxAutoBidAmount");

        if (isNullOrEmpty(auctionIdStr) || isNullOrEmpty(userIdStr) || isNullOrEmpty(bidAmountStr)) {
            response.getWriter().write("Error: Invalid input format. One or more parameters are missing.");
            return;
        }

        Connection conn = null;
        try {
            conn = DBConfig.getConnection();
            conn.setAutoCommit(false);

            int auctionId = Integer.parseInt(auctionIdStr.trim());
            int userId = Integer.parseInt(userIdStr.trim());
            BigDecimal bidAmount = new BigDecimal(bidAmountStr.trim());
            BigDecimal maxAutoBidAmount = isNullOrEmpty(maxAutoBidAmountStr) ? null : new BigDecimal(maxAutoBidAmountStr.trim());

            // Insert the initial user bid
            insertBid(conn, auctionId, userId, bidAmount, maxAutoBidAmount);

            // Process auto-bidding logic
            processAutoBidding(conn, auctionId, userId, bidAmount, maxAutoBidAmount);

            // Commit the transaction
            conn.commit();

            response.getWriter().write("Success: Bid placed successfully!");
        } catch (NumberFormatException | SQLException e) {
            response.getWriter().write("Error: " + e.getMessage());
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback(); 
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private void insertBid(Connection conn, int auctionId, int userId, BigDecimal bidAmount, BigDecimal maxAutoBidAmount) throws SQLException {
        String sql = "INSERT INTO Bid (auctionID, BidderID, BidAmount, MaxAutoBidAmount) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, auctionId);
            pstmt.setInt(2, userId);
            pstmt.setBigDecimal(3, bidAmount);
            pstmt.setBigDecimal(4, maxAutoBidAmount);
            pstmt.executeUpdate();
        }
    }

    private void processAutoBidding(Connection conn, int auctionId, int userId, BigDecimal bidAmount, BigDecimal maxAutoBidAmount) throws SQLException {
        String sqlSelect = "SELECT BidderID, MaxAutoBidAmount FROM Bid WHERE auctionID = ? AND BidderID != ? AND MaxAutoBidAmount > ? ORDER BY MaxAutoBidAmount DESC";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlSelect)) {
            pstmt.setInt(1, auctionId);
            pstmt.setInt(2, userId);
            pstmt.setBigDecimal(3, bidAmount);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                BigDecimal nextMaxAutoBid = rs.getBigDecimal("MaxAutoBidAmount");
                int nextBidderId = rs.getInt("BidderID");

                // Check if the next auto-bid exceeds the current highest bid plus the increment
                BigDecimal bidIncrement = getBidIncrement(conn, auctionId);
                BigDecimal nextBid = bidAmount.add(bidIncrement);

                if (nextMaxAutoBid.compareTo(nextBid) >= 0) {
                    insertBid(conn, auctionId, nextBidderId, nextBid, nextMaxAutoBid);
                    bidAmount = nextBid; // Update the current highest bid
                }
            }
        }
    }

    private BigDecimal getBidIncrement(Connection conn, int auctionId) throws SQLException {
        String sql = "SELECT BidIncrement FROM Auction WHERE auctionID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, auctionId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal("BidIncrement");
            }
            throw new SQLException("Failed to retrieve bid increment for auction ID: " + auctionId);
        }
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}