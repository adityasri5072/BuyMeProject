<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Edit User</title>
</head>
<body>
    <h1>Edit User</h1>
    <% if (request.getAttribute("error") != null) { %>
        <p style="color: red;"><%= request.getAttribute("error") %></p>
    <% } 
       Integer userID = (Integer) request.getAttribute("userID");
       if (userID != null) { %>
        <form action="EditAccountServlet" method="post">
            User ID: <input type="text" name="userID" value="<%= userID %>" readonly><br>
            Username: <input type="text" name="username" value="<%= (String) request.getAttribute("username") %>" required><br>
            Email: <input type="email" name="email" value="<%= (String) request.getAttribute("email") %>" required><br>
            Password: <input type="text" name="password" value="<%= (String) request.getAttribute("password") %>" required><br>
            <input type="hidden" name="action" value="updateUser">
            <button type="submit">Update User</button>
        </form>
    <% } else { %>
        <p>User not found.</p>
    <% } %>
</body>
</html>
