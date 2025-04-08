<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Select Report</title>
</head>
<body>
    <h1>Select a Report to View</h1>
    <form action="ViewReportsServlet" method="get">
        <select name="reportType">
            <option value="totalEarnings">Total Earnings</option>
            <option value="earningsPerItem">Earnings Per Item</option>
            <option value="earningsPerItemType">Earnings Per Item Type</option>
            <option value="earningsPerEndUser">Earnings Per End User</option>
            <option value="bestSellingItems">Best Selling Items</option>
            <option value="bestBuyers">Best Buyers</option>
        </select>
        <button type="submit">View Report</button>
    </form>
    <br>
    <button onclick="window.location.href='dashboard.jsp'">Return Home</button>
</body>
</html>
