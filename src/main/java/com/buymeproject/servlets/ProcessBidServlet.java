package com.buymeproject.servlets;

import com.buymeproject.database.DBConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;

@WebServlet("/ProcessBidServlet")
public class ProcessBidServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        String auctionIdStr = request.getParameter("auctionId");
        String userIdStr = request.getParameter("userId");
        String bidAmountStr = request.getParameter("bidAmount");
        String maxAutoBidAmountStr = request.getParameter("maxAutoBidAmount");

        if (auctionIdStr == null || userIdStr == null || bidAmountStr == null) {
            sendResponse(response, "Missing mandatory parameters for bidding. Please check your inputs.", "placeBid.jsp");
            return;
        }

        Connection conn = null;
        try {
            conn = DBConfig.getConnection();
            conn.setAutoCommit(false);

            int auctionId = Integer.parseInt(auctionIdStr);
            int userId = Integer.parseInt(userIdStr);
            BigDecimal bidAmount = new BigDecimal(bidAmountStr);
            BigDecimal maxAutoBidAmount = isNullOrEmpty(maxAutoBidAmountStr) ? null : new BigDecimal(maxAutoBidAmountStr);
            BigDecimal currentBid = getCurrentBid(conn, auctionId);

            if (bidAmount.compareTo(currentBid) <= 0) {
                sendResponse(response, "Your bid of $" + bidAmount + " must be higher than the current highest bid of $" + currentBid, "placeBid.jsp");
                return;
            }

            if (!insertBid(conn, auctionId, userId, bidAmount, maxAutoBidAmount)) {
                conn.rollback();
                sendResponse(response, "Failed to insert your bid. Please try again.", "placeBid.jsp");
                return;
            }

            notifyOutbidUsers(conn, auctionId, userId, currentBid, bidAmount);
            updateCurrentBid(conn, auctionId, bidAmount);

            conn.commit();
            sendResponse(response, "Bid processed successfully!", "placeBid.jsp");
        } catch (NumberFormatException | SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                 
                }
            }
            sendResponse(response, "Error processing bid: " + e.getMessage(), "placeBid.jsp");
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private BigDecimal getCurrentBid(Connection conn, int auctionId) throws SQLException {
        String sql = "SELECT CurrentBid FROM Auction WHERE auctionID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, auctionId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal("CurrentBid");
            }
            return BigDecimal.ZERO; // Default if no bid found
        }
    }

    private boolean insertBid(Connection conn, int auctionId, int userId, BigDecimal bidAmount, BigDecimal maxAutoBidAmount) throws SQLException {
        String sql = "INSERT INTO Bid (auctionID, BidderID, BidAmount, MaxAutoBidAmount) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, auctionId);
            pstmt.setInt(2, userId);
            pstmt.setBigDecimal(3, bidAmount);
            pstmt.setBigDecimal(4, maxAutoBidAmount);
            return pstmt.executeUpdate() > 0;
        }
    }

    private void notifyOutbidUsers(Connection conn, int auctionId, int userId, BigDecimal currentBid, BigDecimal newBid) throws SQLException {
        // Query to get distinct bidders who have been outbid
        String sql = "SELECT DISTINCT BidderID, MaxAutoBidAmount FROM Bid WHERE auctionID = ? AND BidAmount < ? AND BidderID != ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, auctionId);
            pstmt.setBigDecimal(2, newBid);
            pstmt.setInt(3, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int outbidUserId = rs.getInt("BidderID");
                BigDecimal maxAutoBidAmount = rs.getBigDecimal("MaxAutoBidAmount");
                
             // Check if the new bid exceeds their auto-bid limit and notify them
                if (maxAutoBidAmount != null && newBid.compareTo(maxAutoBidAmount) > 0) {
                    notifyAutoBidLimitExceeded(conn, outbidUserId, auctionId, maxAutoBidAmount);
                }
                else {
                // Notify users who have been simply outbid
                sendOutbidNotification(conn, outbidUserId, auctionId);
                }
                
            }
        }
    }

    private void sendOutbidNotification(Connection conn, int userId, int auctionId) throws SQLException {
        String msg = "You have been outbid on auction #" + auctionId;
        String sql = "INSERT INTO Notifications (userID, message) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, msg);
            pstmt.executeUpdate();
        }
    }

   
    private void notifyAutoBidLimitExceeded(Connection conn, int userId, int auctionId, BigDecimal maxAutoBidAmount) throws SQLException {
        String message = "Your auto-bid limit of $" + maxAutoBidAmount + " has been exceeded in auction #" + auctionId;
        String sql = "INSERT INTO Notifications (userID, message) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, message);
            pstmt.executeUpdate();
        }
    }

    private void updateCurrentBid(Connection conn, int auctionId, BigDecimal newBid) throws SQLException {
        String sql = "UPDATE Auction SET CurrentBid = ? WHERE auctionID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBigDecimal(1, newBid);
            pstmt.setInt(2, auctionId);
            pstmt.executeUpdate();
        }
    }

    private void sendResponse(HttpServletResponse response, String message, String redirect) throws IOException {
        response.getWriter().println("<script>alert('" + message + "'); window.location='" + redirect + "';</script>");
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
