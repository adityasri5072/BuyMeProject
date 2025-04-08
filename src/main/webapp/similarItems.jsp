<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.time.ZoneId" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Similar Items</title>
    <link rel="stylesheet" href="similarItems.css">
</head>
<body>
    <h1>Similar Items</h1>
    <form action="SimilarItemsServlet" method="get">
        <label for="make">Make:</label>
        <input type="text" id="make" name="make" required>
        <label for="model">Model (optional):</label>
        <input type="text" id="model" name="model">
        <button type="submit">Search</button>
    </form>
    <table>
        <tr>
            <th>Make</th>
            <th>Model</th>
            <th>Year</th>
            <th>Current Bid</th>
            <th>Auction End Time</th>
            <th>Image</th>
        </tr>
        <%
            ResultSet similarItems = (ResultSet) request.getAttribute("similarItems");
            if (similarItems != null) {
                while (similarItems.next()) {
        %>
        <tr>
            <td><%= similarItems.getString("Make") %></td>
            <td><%= similarItems.getString("Model") %></td>
            <td><%= similarItems.getInt("Year") %></td>
            <td><%= similarItems.getBigDecimal("CurrentBid") %></td>
            <td><%= similarItems.getTimestamp("ClosingTime") %></td>
            <td><img src="<%= similarItems.getString("ImagePath") %>" alt="Vehicle Image" width="100"></td>
        </tr>
        <%
                }
            } else {
        %>
        <tr>
            <td colspan="6">No similar items found.</td>
        </tr>
        <%
            }
        %>
    </table>
    <a href="dashboard.jsp" class="home-button">Return to Home</a>
</body>
</html>
