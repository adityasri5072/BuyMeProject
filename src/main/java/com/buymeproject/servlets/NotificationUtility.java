package com.buymeproject.servlets;

import java.sql.*;

public class NotificationUtility {
	public static void addNotification(Connection conn, int userID, String message) throws SQLException {
	    String sql = "INSERT INTO Notifications (userID, message) VALUES (?, ?)";
	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setInt(1, userID);
	        stmt.setString(2, message);
	        stmt.executeUpdate();
	    }
	}

}
