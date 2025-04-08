package com.buymeproject.servlets;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import com.buymeproject.database.DBConfig;
import com.buymeproject.servlets.ReportRecord;

@WebServlet("/ViewReportsServlet")
public class ViewReportsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String reportType = request.getParameter("reportType"); 
        String sqlQuery = "";

        switch (reportType) {
            case "totalEarnings":
                sqlQuery = "SELECT 'Total Earnings' AS Identifier, SUM(totalPrice) AS Earnings, 'All' AS AuctionID, 'N/A' AS Winner FROM Orders";
                break;
            case "earningsPerItem":
                sqlQuery = "SELECT Vehicle.VIN AS Identifier, SUM(totalPrice) AS Earnings, GROUP_CONCAT(DISTINCT Auction.auctionID) AS AuctionID, GROUP_CONCAT(DISTINCT User.Username) AS Winner FROM Orders " +
                           "JOIN Auction ON Orders.auctionId = Auction.auctionID " +
                           "JOIN Vehicle ON Orders.vehicleId = Vehicle.vehicleID " +
                           "JOIN User ON Orders.winnerId = User.userID " +
                           "GROUP BY Vehicle.VIN";
                break;
            case "earningsPerItemType":
                sqlQuery = "SELECT VehicleCategory.Name AS Identifier, SUM(totalPrice) AS Earnings, GROUP_CONCAT(DISTINCT Auction.auctionID ORDER BY Auction.auctionID) AS AuctionID, GROUP_CONCAT(DISTINCT User.Username) AS Winner FROM Orders " +
                           "JOIN Vehicle ON Orders.vehicleId = Vehicle.vehicleID " +
                           "JOIN VehicleCategory ON Vehicle.categoryID = VehicleCategory.categoryID " +
                           "JOIN Auction ON Orders.auctionId = Auction.auctionID " +
                           "JOIN User ON Orders.winnerId = User.userID " +
                           "GROUP BY VehicleCategory.Name, User.Username";
                break;
            case "earningsPerEndUser":
            	sqlQuery = "SELECT User.Username AS Identifier, SUM(totalPrice) AS Earnings, GROUP_CONCAT(DISTINCT Auction.auctionID ORDER BY Auction.auctionID) AS AuctionID, User.Username AS Winner FROM Orders " +
            	           "JOIN User ON Orders.winnerId = User.userID " +
            	           "JOIN Auction ON Orders.auctionId = Auction.auctionID " +
            	           "GROUP BY User.Username";
                break;
            case "bestSellingItems":
                sqlQuery = "SELECT Vehicle.VIN AS Identifier, COUNT(*) AS SalesCount, SUM(totalPrice) AS Earnings, GROUP_CONCAT(DISTINCT Auction.auctionID) AS AuctionID, GROUP_CONCAT(DISTINCT User.Username) AS Winner FROM Orders " +
                           "JOIN Auction ON Orders.auctionId = Auction.auctionID " +
                           "JOIN Vehicle ON Orders.vehicleId = Vehicle.vehicleID " +
                           "JOIN User ON Orders.winnerId = User.userID " +
                           "GROUP BY Vehicle.VIN ORDER BY Earnings DESC";
                break;
            case "bestBuyers":
            	sqlQuery = "SELECT User.Username AS Identifier, COUNT(*) AS NumberOfWins, SUM(totalPrice) AS Earnings, GROUP_CONCAT(DISTINCT Auction.auctionID ORDER BY Auction.auctionID) AS AuctionID, User.Username AS Winner FROM Orders " +
            	           "JOIN User ON Orders.winnerId = User.userID " +
            	           "JOIN Auction ON Orders.auctionId = Auction.auctionID " +
            	           "GROUP BY User.Username ORDER BY SUM(totalPrice) DESC";
                break;
            default:
                request.setAttribute("error", "Invalid report type selected");
                request.getRequestDispatcher("/errorPage.jsp").forward(request, response);
                return;
        }

        List<ReportRecord> records = new ArrayList<>();
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlQuery)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String identifier = rs.getString("Identifier");
                BigDecimal earnings = rs.getBigDecimal("Earnings");
                String auctionIDs = rs.getString("AuctionID");
                String winner = rs.getString("Winner");

                ReportRecord record = new ReportRecord(identifier, earnings, auctionIDs, winner);
                records.add(record);
            }
            request.setAttribute("records", records);
            request.setAttribute("reportType", reportType);
            request.getRequestDispatcher("/reports.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
        }
    }
}