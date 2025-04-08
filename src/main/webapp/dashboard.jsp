<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.sql.*"%>
<%@ page import="com.buymeproject.database.DBConfig"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%
// Initialize the notification count to 0
int notificationCount = 0;

// Check if the user is logged in
Integer userId = (Integer) session.getAttribute("userID");
if (userId != null) {
	try (Connection conn = DBConfig.getConnection();
	PreparedStatement stmt = conn.prepareStatement(
			"SELECT COUNT(*) AS count FROM Notifications WHERE userID = ? AND isRead = FALSE")) {
		stmt.setInt(1, userId);
		try (ResultSet rs = stmt.executeQuery()) {
	if (rs.next()) {
		notificationCount = rs.getInt("count");
	}
		}
	} catch (SQLException e) {
		e.printStackTrace();
	}
}
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Dashboard | BuyMe Vehicles</title>
<link rel="stylesheet" href="dashboard.css">
<script>
window.onload = function() {
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.get('bidSuccess')) {
        alert('Bid placed successfully!');
        window.location.href = "dashboard.jsp"; // Redirect to itself without parameters to refresh data
    }
};
</script>
<script>
function confirmBid(bidAmountInputId, auctionId, currentBidId, initialPriceId, vehicleNameId) {
    var bidAmount = parseFloat(document.getElementById(bidAmountInputId).value);
    var currentBid = parseFloat(document.getElementById(currentBidId).getAttribute('data-current-bid'));
    var initialPrice = parseFloat(document.getElementById(initialPriceId).getAttribute('data-initial-price'));
    var vehicleName = document.getElementById(vehicleNameId).textContent;

    if (isNaN(bidAmount) || bidAmount <= currentBid || bidAmount < initialPrice) {
        alert("The bid you have placed is not valid or lower than the current bid or initial price.");
        return false;
    } else {
        alert("Placing a bid of $" + bidAmount + " on " + vehicleName + ".");
        placeBid(auctionId, bidAmountInputId);
        return false;
    }
}

function placeBid(auctionId, bidAmountInputId) {
    var bidAmount = document.getElementById(bidAmountInputId).value;
    var xhr = new XMLHttpRequest();
    xhr.open("POST", "PlaceBidServlet", true);
    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    xhr.onreadystatechange = function() {
        if (this.readyState === 4 && this.status === 200) {
            var response = JSON.parse(this.responseText);
            if (response.status === "success") {
                alert(response.message);
                // Update the current bid displayed on the page
                document.getElementById("currentBid" + auctionId).innerText = "Current Bid: $" + response.newCurrentBid;
               
                location.reload();
            } else {
                alert(response.message);
            }
        }
    };
    xhr.send("auctionId=" + auctionId + "&userId=" + '<%= session.getAttribute("userID") %>' + "&bidAmount=" + bidAmount);
}
    </script>
<script>
	window.onload = function() {
		const urlParams = new URLSearchParams(window.location.search);
		const successMessage = urlParams.get('success');
		const errorMessage = urlParams.get('error');

		if (successMessage) {
			alert(successMessage);
		}

		if (errorMessage) {
			alert(errorMessage);
		}
	};
</script>
</head>
<body>
	<header class="site-header">
		<div class="logo">
			<a href="dashboard.jsp">BuyMe Vehicles</a>
		</div>
		<nav class="site-navigation">
			<ul class="nav-links">
				<li><a href="index.jsp">Home</a></li>
				<li><a href="notifications.jsp" class="notification-link">
						<span class="bell-icon">&#128276;</span> <% if (notificationCount > 0) { %>
						<span class="notification-count"><%= notificationCount %></span> <% } %>
				</a></li>
				<li><a href="profile.jsp">Profile</a></li>
				<li><a href="bidHistory.jsp">Bid History</a></li>
				<li><a href="userAuctions.jsp">Find Auctions</a></li>
				<li><a href="support.jsp">Support</a></li>
				<li><a href="similarItems.jsp">Similar Items</a></li>
				<%
    	Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
   	 	if (Boolean.TRUE.equals(isAdmin)) {
		%>
				<li><a href="AdminDashboard.jsp">Admin Dashboard</a></li>
				<% 
            }
        %>
				<%
    	Boolean isCustomerRep = (Boolean) session.getAttribute("isCustomerRep");
   	 	if (Boolean.TRUE.equals(isCustomerRep)) {
		%>
				<li><a href="customerRepDashBoard.jsp">Customer Rep
						Dashboard</a></li>
				<% 
            }
        %>
			</ul>
		</nav>


		<div class="search-bar">
			<input type="text" placeholder="Search for vehicles...">
			<button type="submit">Search</button>
		</div>
		<div class="user-actions">
			<a href="auctioncreation.jsp" class="create-auction-btn">Sell</a> <a
				href="LogoutServlet" class="logout-btn">LogOut</a>
		</div>
	</header>
	<main class="main-content">
		<!-- Alert Setting Section -->
		<section class="set-alerts">
			<h2>Set Alerts for Vehicles</h2>
			<form method="post" action="SetAlertsServlet">
				<input type="text" name="make"
					placeholder="Enter make (e.g., Toyota)" required> <input
					type="text" name="model" placeholder="Enter model (e.g., Camry)"
					required> <input type="number" name="year"
					placeholder="Enter year (optional)"> <input type="text"
					name="color" placeholder="Enter color (optional)">
				<button type="submit">Set Alert</button>
			</form>
		</section>
		<section class="search-vehicles">
			<h2>Search Auctions</h2>
			<form id="searchForm" method="get" action="">
				<!-- Existing search fields -->
				<input type="text" name="make"
					placeholder="Enter make (e.g., Toyota)"> <input type="text"
					name="model" placeholder="Enter model (e.g., Camry)"> <input
					type="number" name="year" placeholder="Enter year"> <input
					type="text" name="color" placeholder="Enter color"> <input
					type="text" name="size" placeholder="Enter size">
				<button type="submit">Search</button>
				<button type="button" onclick="resetSearch()">Reset</button>
			</form>
		</section>
		<section class="sort-vehicles">
			<h2>Sort Vehicles</h2>
			<form id="sortForm" method="get" action="">

				<!-- Sorting drop down -->
				<select name="sortby">
					<option value="">Sort by</option>
					<option value="make_asc">Make (A-Z)</option>
					<option value="make_desc">Make (Z-A)</option>
					<option value="year_asc">Year (Oldest-Newest)</option>
					<option value="year_desc">Year (Newest-Oldest)</option>
					<option value="current_bid_asc">Current Bid
						(Lowest-Highest)</option>
					<option value="current_bid_desc">Current Bid
						(Highest-Lowest)</option>
				</select>
				<button type="submit">Search</button>
				<button type="button" onclick="resetSearch()">Reset</button>
			</form>
		</section>

		<script>
			function resetSearch() {
				var form = document.getElementById('searchForm');
				form.reset();
				form.submit();
			}
		</script>
		<section class="grid-item active-bids">
			<h2>Active Bids</h2>
			<div class="auction-items-container">
    <%
String make = request.getParameter("make");
String model = request.getParameter("model");
String year = request.getParameter("year");
String color = request.getParameter("color");
String size = request.getParameter("size");
String sortby = request.getParameter("sortby");
SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss"); 

StringBuilder sqlBuilder = new StringBuilder("SELECT Vehicle.Make, Vehicle.Model, Vehicle.Year, Vehicle.Color, Vehicle.Size, Auction.auctionID, Auction.CurrentBid, Vehicle.InitialPrice, Auction.ClosingTime, Vehicle.ImagePath FROM Auction JOIN Vehicle ON Auction.vehicleID = Vehicle.vehicleID WHERE Auction.IsClosed = FALSE");

// Dynamic search conditions
if (make != null && !make.isEmpty() || model != null && !model.isEmpty() || year != null && !year.isEmpty() || color != null && !color.isEmpty() || size != null && !size.isEmpty()) {
    sqlBuilder.append(" AND (1=1");
    if (make != null && !make.isEmpty()) sqlBuilder.append(" AND Vehicle.Make LIKE ?");
    if (model != null && !model.isEmpty()) sqlBuilder.append(" AND Vehicle.Model LIKE ?");
    if (year != null && !year.isEmpty()) sqlBuilder.append(" AND Vehicle.Year = ?");
    if (color != null && !color.isEmpty()) sqlBuilder.append(" AND Vehicle.Color LIKE ?");
    if (size != null && !size.isEmpty()) sqlBuilder.append(" AND Vehicle.Size LIKE ?");
    sqlBuilder.append(")");
}

// Sorting
if (sortby != null && !sortby.isEmpty()) {
    switch (sortby) {
        case "make_asc": sqlBuilder.append(" ORDER BY Vehicle.Make ASC"); break;
        case "make_desc": sqlBuilder.append(" ORDER BY Vehicle.Make DESC"); break;
        case "year_asc": sqlBuilder.append(" ORDER BY Vehicle.Year ASC"); break;
        case "year_desc": sqlBuilder.append(" ORDER BY Vehicle.Year DESC"); break;
        case "current_bid_asc": sqlBuilder.append(" ORDER BY Auction.CurrentBid ASC"); break;
        case "current_bid_desc": sqlBuilder.append(" ORDER BY Auction.CurrentBid DESC"); break;
    }
}

try (Connection conn = DBConfig.getConnection();
    PreparedStatement pstmt = conn.prepareStatement(sqlBuilder.toString())) {
    int index = 1;
    if (make != null && !make.isEmpty()) pstmt.setString(index++, "%" + make + "%");
    if (model != null && !model.isEmpty()) pstmt.setString(index++, "%" + model + "%");
    if (year != null && !year.isEmpty()) pstmt.setInt(index++, Integer.parseInt(year));
    if (color != null && !color.isEmpty()) pstmt.setString(index++, "%" + color + "%");
    if (size != null && !size.isEmpty()) pstmt.setString(index++, "%" + size + "%");

    try (ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {
            out.println("<div class='auction-item'>");
            out.println("<img src='" + rs.getString("ImagePath") + "' alt='Vehicle Image' style='width:100px; height:100px;' />");
            out.println("<h3>" + rs.getString("Make") + " " + rs.getString("Model") + " - " + rs.getInt("Year") + "</h3>");
            out.println("<p>Current Bid: $" + rs.getBigDecimal("CurrentBid") + "</p>");
            out.println("<p>Initial Price: $" + rs.getBigDecimal("InitialPrice") + "</p>");
            out.println("<p>Closes on: " + sdf.format(rs.getTimestamp("ClosingTime")) + "</p>");
            out.println("<a href='placeBid.jsp?auctionId=" + rs.getInt("auctionID") + "&currentBid=" + rs.getBigDecimal("CurrentBid") + "&initialPrice=" + rs.getBigDecimal("InitialPrice") + "'>Place Bid</a>");
            out.println("</div>");
        }
    }
} catch (SQLException e) {
    e.printStackTrace();
}
%>
</div>
			</div>
		</section>
	</main>
	<footer class="site-footer"> © 2024 BuyMe, Inc. All rights
		reserved by Ahnaf, Anant, Aditya, Dan. </footer>
</body>
</html>