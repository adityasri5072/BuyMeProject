<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.buymeproject.database.DBConfig" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Bid History</title>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 40px; }
        table { width: 100%; border-collapse: collapse; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f0f0f0; }
        .home-button { padding: 10px 20px; background-color: #007bff; color: white; text-decoration: none; border-radius: 5px; }
    </style>
</head>
<body>
    <h1>Your Bid History</h1>
    <a href="dashboard.jsp" class="home-button">Return to Home</a>
    <table>
        <thead>
            <tr>
                <th>Vehicle</th>
                <th>Bid Amount</th>
                <th>Bid Time</th>
                <th>Auction Status</th>
                <th>Max Auto-Bid Amount</th>
            </tr>
        </thead>
        <tbody>
            <%
            Connection conn = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            try {
                Integer userID = (Integer) session.getAttribute("userID");
                if (userID == null) {
                    out.println("<tr><td colspan='7'>Please log in to view your bid history.</td></tr>");
                } else {
                    conn = DBConfig.getConnection();
                    String sql = "SELECT Vehicle.Make, Vehicle.Model, Bid.BidAmount, Bid.BidTime, Auction.IsClosed, Bid.AutomaticBid, Bid.MaxAutoBidAmount, Auction.BidIncrement "
                               + "FROM Bid INNER JOIN Auction ON Bid.auctionID = Auction.auctionID "
                               + "INNER JOIN Vehicle ON Auction.vehicleID = Vehicle.vehicleID "
                               + "WHERE Bid.BidderID = ? ORDER BY Bid.BidTime DESC";
                    pstmt = conn.prepareStatement(sql);
                    pstmt.setInt(1, userID);
                    rs = pstmt.executeQuery();

                    if (!rs.isBeforeFirst()) {
                        out.println("<tr><td colspan='7'>No bids found.</td></tr>");
                    } else {
                        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");
                        while (rs.next()) {
                            String vehicle = rs.getString("Make") + " " + rs.getString("Model");
                            BigDecimal bidAmount = rs.getBigDecimal("BidAmount");
                            Timestamp bidTime = rs.getTimestamp("BidTime");
                            boolean isClosed = rs.getBoolean("IsClosed");
                            BigDecimal maxAutoBidAmount = rs.getBigDecimal("MaxAutoBidAmount");
                            

                            String status = isClosed ? "Closed" : "Active";
                            String maxBidDisplay = maxAutoBidAmount != null ? "$" + maxAutoBidAmount.toString() : "N/A";
                            
                            
                            out.println("<tr>");
                            out.println("<td>" + vehicle + "</td>");
                            out.println("<td>$" + bidAmount + "</td>");
                            out.println("<td>" + sdf.format(bidTime) + "</td>");
                            out.println("<td>" + status + "</td>");
                            out.println("<td>" + maxBidDisplay + "</td>");
                            
                            out.println("</tr>");
                        }
                    }
                }
            } catch (SQLException e) {
                out.println("<tr><td colspan='7'>Error retrieving bid history: " + e.getMessage() + "</td></tr>");
                e.printStackTrace();
            } finally {
                try { if (rs != null) rs.close(); } catch (Exception e) { /* ignored */ }
                try { if (pstmt != null) pstmt.close(); } catch (Exception e) { /* ignored */ }
                try { if (conn != null) conn.close(); } catch (Exception e) { /* ignored */ }
            }
            %>
        </tbody>
    </table>
</body>
</html>
