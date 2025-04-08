<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="javax.servlet.http.HttpSession"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Welcome to BuyMe!</title>
    <script type="text/javascript">
        window.onload = function() {
            // Check for messages in the session and alert them
            <% 
            String message = (String) session.getAttribute("message");
            String error = (String) session.getAttribute("error");
            if (message != null) {
                out.println("alert('" + message + "');");
                session.removeAttribute("message"); 
            }
            if (error != null) {
                out.println("alert('" + error + "');");
                session.removeAttribute("error"); 
            }
            %>
        }
    </script>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f7f7f7;
            text-align: center;
            padding-top: 100px;
        }
        .container {
            margin: auto;
            width: fit-content;
            padding: 20px;
            border: 1px solid #ddd;
            border-radius: 4px;
            background: #fff;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .message {
            margin-bottom: 20px;
            padding: 10px;
            color: #3c763d;
            background-color: #dff0d8;
            border-color: #d6e9c6;
            border-radius: 4px;
            display: inline-block;
        }
        .action-button {
            padding: 10px 15px;
            background-color: #428bca;
            color: white;
            border: none;
            border-radius: 4px;
            text-decoration: none;
            margin: 5px;
            cursor: pointer;
            display: inline-block;
        }
        .logout-button {
            background-color: #d9534f;
        }
        .logout-button:hover {
            background-color: #c9302c;
        }
        .login-container {
            margin-top: 20px;
        }
        form label {
            margin-bottom: 5px;
        }
        form input[type="text"],
        form input[type="password"],
        form input[type="email"] {
            margin-bottom: 15px;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
        }
        form button {
            padding: 10px;
            background-color: #0056b3;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            margin-top: 10px;
        }
        form button:hover {
            background-color: #003d82;
        }
    </style>
</head>
<body>

<div class="container login-container">
    <h1>Welcome to BuyMe!</h1>
    <form action="LoginServlet" method="post">
        <label for="username">Username:</label>
        <input type="text" id="username" name="username" required>
        <label for="password">Password:</label>
        <input type="password" id="password" name="password" required>
        <button type="submit">Login</button>
    </form>

    <form action="RegisterServlet" method="post">
        <h2>Register</h2>
        <label for="newUsername">New Username:</label>
        <input type="text" id="newUsername" name="newUsername" required>
        <label for="newPassword">New Password:</label>
        <input type="password" id="newPassword" name="newPassword" required>
        <label for="email">Email:</label>
        <input type="email" id="email" name="email" required>
        <button type="submit">Register</button>
    </form>

    <form action="DeleteAccountServlet" method="post">
        <h2>Delete Account</h2>
        <label for="deleteUsername">Username:</label>
        <input type="text" id="deleteUsername" name="deleteUsername" required>
        <label for="deletePassword">Password (for verification):</label>
        <input type="password" id="deletePassword" name="deletePassword" required>
        <button type="submit" style="background-color: #d9534f;">Delete Account</button>
    </form>
</div>

</body>
</html>