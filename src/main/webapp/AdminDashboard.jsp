<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard | BuyMe Vehicles</title>
    <link rel="stylesheet" href="dashboard.css">
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            margin: 0;
            padding: 0;
        }
        .site-header {
            background-color: #333;
            color: white;
            padding: 10px 20px;
            text-align: center;
        }
        .site-navigation ul {
            list-style-type: none;
            padding: 0;
        }
        .site-navigation ul li {
            display: inline;
            margin-right: 10px;
        }
        .site-navigation ul li a {
            color: white;
            text-decoration: none;
            font-weight: bold;
        }
        .main-content {
            margin: 20px;
            padding: 20px;
            background-color: white;
            border-radius: 8px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }
        .admin-functions {
            margin-top: 20px;
        }
        .admin-functions section {
            margin-bottom: 20px;
        }
        .admin-functions a {
            display: inline-block;
            padding: 10px 15px;
            background-color: #007BFF;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            margin-top: 10px;
        }
        footer {
            text-align: center;
            padding: 20px;
            background-color: #333;
            color: white;
            position: fixed;
            width: 100%;
            bottom: 0;
        }
    </style>
</head>
<body>
    <header class="site-header">
        <div class="logo">
            <a href="dashboard.jsp">BuyMe Vehicles Admin</a>
        </div>
        <nav class="site-navigation">
            <ul class="nav-links">
                <li><a href="RoleManagementServlet">Manage Users</a></li>
                <li><a href="reportsSelection.jsp">View Reports</a></li>
                <li><a href="LogoutServlet">Log Out</a></li>
            </ul>
        </nav>
    </header>
    <main class="main-content">
        <h1>Welcome to the Admin Dashboard</h1>
        <div class="admin-functions">
            <section>
                <h2>User Management</h2>
                <p>Handle all user roles and permissions from one place.</p>
                <a href="RoleManagementServlet">Manage Users</a>
            </section>
            <section>
                <h2>Reports</h2>
                <p>Access detailed reports and analytics for site activities.</p>
                <!-- Add new link for reports selection -->
                <a href="reportsSelection.jsp">Select and View Reports</a>
            </section>
        </div>
    </main>
    <footer>
        &copy; 2024 BuyMe, Inc. All rights reserved.
    </footer>
</body>
</html>