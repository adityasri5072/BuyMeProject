<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="com.buymeproject.database.DBConfig"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>User Auctions</title>
    <link rel="stylesheet" href="userAuctions.css">
</head>
<body>
    <a href="dashboard.jsp" class="return-home-btn">Return to Home</a>
    <h1>Lookup User Auctions</h1>
    <form action="UserAuctionsServlet" method="post">
        <input type="text" name="username" placeholder="Enter username" required>
        <select name="role" required>
            <option value="buyer">Buyer</option>
            <option value="seller">Seller</option>
        </select>
        <button type="submit">Search</button>
    </form>
    <%
        String viewedUsername = (String) request.getAttribute("viewedUsername");
        if (viewedUsername != null && !viewedUsername.isEmpty()) {
            out.println("<h2>Auctions for: " + viewedUsername + "</h2>");
        }
        ResultSet auctionsList = (ResultSet) request.getAttribute("auctionsList");
        if (auctionsList != null) {
            while (auctionsList.next()) {
                out.println("<div>");
                out.println("<h3>" + auctionsList.getString("Make") + " " + auctionsList.getString("Model") + " (" + auctionsList.getInt("Year") + ")</h3>");
                out.println("<p>Auction ID: " + auctionsList.getInt("auctionID") + "</p>");
                out.println("<p>Current Bid: " + auctionsList.getDouble("CurrentBid") + "</p>");
                out.println("<p>Closing Time: " + auctionsList.getTimestamp("ClosingTime") + "</p>");
                out.println("</div>");
            }
        } else {
            out.println("<p>No auctions found for " + viewedUsername + ".</p>");
        }
    %>
</body>
</html>
