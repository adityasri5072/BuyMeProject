<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.ResultSet" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Delete Auctions</title>
</head>
<body>
    <h1>Delete Auctions</h1>
    <% if (request.getAttribute("error") != null) { %>
        <p style="color: red;"><%= request.getAttribute("error") %></p>
    <% }
       if (request.getAttribute("success") != null) { %>
        <p style="color: green;"><%= request.getAttribute("success") %></p>
    <% } %>
    <form action="DeleteAuctionsServlet" method="GET">
        <button type="submit">Load Auctions</button>
    </form>
    <!-- Return Home Button -->
    <a href="dashboard.jsp"><button type="button">Return Home</button></a>
    <table border="1">
        <thead>
            <tr>
                <th>Auction ID</th>
                <th>Vehicle Make</th>
                <th>Vehicle Model</th>
                <th>Opening Time</th>
                <th>Closing Time</th>
                <th>Current Bid</th>
                <th>Action</th>
            </tr>
        </thead>
        <tbody>
            <% ResultSet auctions = (ResultSet) request.getAttribute("auctions");
               if (auctions != null) {
                   while (auctions.next()) { %>
                       <tr>
                           <td><%= auctions.getInt("auctionID") %></td>
                           <td><%= auctions.getString("Make") %></td>
                           <td><%= auctions.getString("Model") %></td>
                           <td><%= auctions.getTimestamp("OpeningTime") %></td>
                           <td><%= auctions.getTimestamp("ClosingTime") %></td>
                           <td><%= auctions.getDouble("CurrentBid") %></td>
                           <td>
                               <form method="post" action="DeleteAuctionsServlet">
                                   <input type="hidden" name="auctionId" value="<%= auctions.getInt("auctionID") %>">
                                   <button type="submit">Delete</button>
                               </form>
                           </td>
                       </tr>
                   <% }
               } else { %>
                   <tr><td colspan="7">No auctions found.</td></tr>
               <% } %>
        </tbody>
    </table>
</body>
</html>
