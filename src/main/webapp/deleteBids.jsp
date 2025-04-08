<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.ResultSet" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Delete Bids</title>
</head>
<body>
    <h1>Delete Bids</h1>
    <% if (request.getAttribute("error") != null) { %>
        <p style="color: red;"><%= request.getAttribute("error") %></p>
    <% } %>
    <form action="DeleteBidsServlet" method="GET">
        <button type="submit">Load Bids</button>
    </form>
    <!-- Return Home Button -->
    <a href="dashboard.jsp"><button type="button">Return Home</button></a>
    <table border="1">
        <thead>
            <tr>
                <th>Bid ID</th>
                <th>Bid Amount</th>
                <th>Bidder ID</th>
                <th>Auction ID</th>
                <th>Current Auction Bid</th>
                <th>Action</th>
            </tr>
        </thead>
        <tbody>
            <% ResultSet bids = (ResultSet) request.getAttribute("bids");
               if (bids != null) {
                   while (bids.next()) { %>
                       <tr>
                           <td><%= bids.getInt("bidID") %></td>
                           <td><%= bids.getBigDecimal("BidAmount") %></td>
                           <td><%= bids.getInt("BidderID") %></td>
                           <td><%= bids.getInt("auctionID") %></td>
                           <td><%= bids.getBigDecimal("CurrentBid") %></td>
                           <td>
                               <form method="post" action="DeleteBidsServlet">
                                   <input type="hidden" name="bidId" value="<%= bids.getInt("bidID") %>">
                                   <button type="submit">Remove</button>
                               </form>
                           </td>
                       </tr>
                   <% }
               } else { %>
                   <tr><td colspan="6">No bids found.</td></tr>
               <% } %>
        </tbody>
    </table>
</body>
</html>
