package com.buymeproject.servlets;

import com.buymeproject.database.DBConfig;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DeleteAuctionsServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try (Connection conn = DBConfig.getConnection()) {
            String sql = "SELECT a.auctionID, a.OpeningTime, a.ClosingTime, a.CurrentBid, v.Make, v.Model FROM Auction a JOIN Vehicle v ON a.vehicleID = v.vehicleID";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            request.setAttribute("auctions", rs);
            request.getRequestDispatcher("/deleteAuctions.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Database error: " + e.getMessage(), e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String auctionIdStr = request.getParameter("auctionId");
        if (auctionIdStr == null || auctionIdStr.trim().isEmpty()) {
            request.setAttribute("error", "Auction ID must not be null or empty");
            doGet(request, response);
            return;
        }

        try (Connection conn = DBConfig.getConnection()) {
            conn.setAutoCommit(false);
            // Delete bids associated with the auction
            String sqlDeleteBids = "DELETE FROM Bid WHERE auctionID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlDeleteBids)) {
                pstmt.setInt(1, Integer.parseInt(auctionIdStr));
                pstmt.executeUpdate();
            }

            // Delete the auction
            String sqlDeleteAuction = "DELETE FROM Auction WHERE auctionID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlDeleteAuction)) {
                pstmt.setInt(1, Integer.parseInt(auctionIdStr));
                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    conn.commit();
                    request.setAttribute("success", "Auction deleted successfully!");
                } else {
                    conn.rollback();
                    request.setAttribute("error", "Failed to delete auction. No auction found with the specified ID.");
                }
            }
            doGet(request, response);
        } catch (SQLException e) {
            request.setAttribute("error", "Database error: " + e.getMessage());
            doGet(request, response);
        }
    }
}
