package com.buymeproject.servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import java.util.concurrent.*;
import com.buymeproject.database.DBConfig;
import java.sql.*;

public class AuctionSchedulerServlet extends HttpServlet {

    private ScheduledExecutorService scheduler;

    @Override
    public void init() throws ServletException {
        final Runnable auctionChecker = new Runnable() {
            public void run() { checkAndCloseAuctions(); }
        };
        scheduler = Executors.newSingleThreadScheduledExecutor();
        // Schedule the task to run every minute
        scheduler.scheduleAtFixedRate(auctionChecker, 0, 1, TimeUnit.SECONDS);
    }

    private void checkAndCloseAuctions() {
        try (Connection conn = DBConfig.getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            try (PreparedStatement stmt = conn.prepareStatement(
                     "SELECT Auction.auctionID, Auction.CurrentBid, Vehicle.SecretMinimum, Vehicle.vehicleID, Vehicle.categoryID " +
                     "FROM Auction INNER JOIN Vehicle ON Auction.vehicleID = Vehicle.vehicleID " +
                     "WHERE Auction.ClosingTime <= NOW() AND Auction.IsClosed = FALSE")) {

                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    int auctionID = rs.getInt("auctionID");
                    double currentBid = rs.getDouble("CurrentBid");
                    double secretMinimum = rs.getDouble("SecretMinimum");
                    int vehicleID = rs.getInt("vehicleID");
                    int categoryID = rs.getInt("categoryID");

                    boolean isSuccessful = currentBid >= secretMinimum;
                    int winnerID = isSuccessful ? getWinnerID(auctionID, conn) : -1;

                    if (winnerID > 0) {
                        sendNotification(winnerID, "Congratulations, you've won the auction for Auction ID: " + auctionID, conn);
                        
                        // Insert into Orders if there is a winner
                        PreparedStatement orderStmt = conn.prepareStatement(
                            "INSERT INTO Orders (auctionId, vehicleId, categoryId, winnerId, totalPrice) VALUES (?, ?, ?, ?, ?)");
                        orderStmt.setInt(1, auctionID);
                        orderStmt.setInt(2, vehicleID);
                        orderStmt.setInt(3, categoryID);
                        orderStmt.setInt(4, winnerID);
                        orderStmt.setDouble(5, currentBid);
                        orderStmt.executeUpdate();
                    }

                    // Update Auction table
                    PreparedStatement updateStmt = conn.prepareStatement(
                        "UPDATE Auction SET IsClosed = TRUE, WinnerID = CASE WHEN ? THEN ? ELSE NULL END WHERE auctionID = ?");
                    updateStmt.setBoolean(1, isSuccessful);
                    updateStmt.setInt(2, winnerID);
                    updateStmt.setInt(3, auctionID);
                    updateStmt.executeUpdate();
                }
                conn.commit(); // Commit transaction
            } catch (SQLException ex) {
                conn.rollback(); 
                ex.printStackTrace();
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    private int getWinnerID(int auctionID, Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
            "SELECT BidderID FROM Bid WHERE auctionID = ? ORDER BY BidAmount DESC LIMIT 1")) {
            stmt.setInt(1, auctionID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("BidderID");
            }
        }
        return -1; // No winner found
    }

    private void sendNotification(int userID, String message, Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
            "INSERT INTO Notifications (userID, message) VALUES (?, ?)")) {
            stmt.setInt(1, userID);
            stmt.setString(2, message);
            stmt.executeUpdate();
        }
    }

    @Override
    public void destroy() {
        scheduler.shutdownNow();
    }
}