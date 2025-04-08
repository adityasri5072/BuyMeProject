<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.ResultSet" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Manage Users</title>
</head>
<body>
    <h1>Manage Users</h1>
    <!-- Return Home Button -->
    <a href="dashboard.jsp"><button type="button">Return Home</button></a>
    <form action="EditAccountServlet" method="GET">
        <input type="hidden" name="action" value="loadUsers">
        <button type="submit">Load Users</button>
    </form>
    <table border="1">
        <thead>
            <tr>
                <th>User ID</th>
                <th>Username</th>
                <th>Email</th>
                <th>Password</th>
                <th>Edit</th>
                <th>Delete</th>
            </tr>
        </thead>
        <tbody>
            <% 
            ResultSet userList = (ResultSet) request.getAttribute("userList");
            if (userList != null) {
                while (userList.next()) { %>
                    <tr>
                        <td><%= userList.getInt("userID") %></td>
                        <td><%= userList.getString("Username") %></td>
                        <td><%= userList.getString("Email") %></td>
                        <td><%= userList.getString("Password") %></td>
                        <td><a href="EditAccountServlet?action=edit&userID=<%= userList.getInt("userID") %>">Edit</a></td>
                        <td><a href="EditAccountServlet?action=delete&userID=<%= userList.getInt("userID") %>" onclick="return confirm('Are you sure?');">Delete</a></td>
                    </tr>
                <% }
            } else { %>
                <tr><td colspan="6">No users found.</td></tr>
            <% } %>
        </tbody>
    </table>
</body>
</html>
