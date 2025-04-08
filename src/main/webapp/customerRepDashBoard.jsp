<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Customer Representative Dashboard | BuyMe Vehicles</title>
    <link rel="stylesheet" href="dashboard.css">
</head>
<body>
    <header class="site-header">
        <div class="logo"><a href="dashboard.jsp">BuyMe Vehicles Customer Service</a></div>
    </header>
    <main class="main-content">
        <h1>Customer Representative Dashboard</h1>
        <div class="functions">
            <section>
                <h2>Manage Questions</h2>
                <p><a href="support.jsp">View and Respond to Questions</a></p>
            </section>
            <section>
                <h2>Account Management</h2>
                <p><a href="EditAccounts.jsp">Edit or Delete User Accounts</a></p>
            </section>
            <section>
                <h2>Bid Management</h2>
                <p><a href="deleteBids.jsp">Remove Bids</a></p>
            </section>
            <section>
                <h2>Auction Management</h2>
                <p><a href="deleteAuctions.jsp">Remove Auctions</a></p>
            </section>
        </div>
    </main>
    <footer class="site-footer">
        <p>&copy; 2024 BuyMe Vehicles. All rights reserved.</p>
    </footer>
</body>
</html>
