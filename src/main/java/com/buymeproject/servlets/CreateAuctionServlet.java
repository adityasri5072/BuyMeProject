package com.buymeproject.servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import com.buymeproject.database.DBConfig;
import java.io.IOException;
import java.io.File;
import java.sql.*;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@MultipartConfig 
public class CreateAuctionServlet extends HttpServlet {
    
    private void checkAndNotifyAlerts(Connection conn, int vehicleID) throws SQLException {
        String fetchVehicleQuery = "SELECT Make, Model, Year, Color FROM Vehicle WHERE vehicleID = ?";
        PreparedStatement psFetchVehicle = conn.prepareStatement(fetchVehicleQuery);
        psFetchVehicle.setInt(1, vehicleID);
        ResultSet vehicleDetails = psFetchVehicle.executeQuery();
        
        // Check if vehicle details exist
        if (!vehicleDetails.next()) {
            return; // If no details are found, exit the method
        }
        
        // Safely handle null values
        String make = vehicleDetails.getString("Make");
        String model = vehicleDetails.getString("Model");
        Integer year = vehicleDetails.getObject("Year", Integer.class);
        String color = vehicleDetails.getString("Color");

        make = (make != null) ? make.toUpperCase() : "";
        model = (model != null) ? model.toUpperCase() : "";
        color = (color != null) ? color.toUpperCase() : "";
        String yearString = (year != null) ? String.valueOf(year).toUpperCase() : "";

        String fetchAlertsQuery = "SELECT userID FROM UserAlerts WHERE Make = ? AND Model = ? AND (Year = ? OR Year IS NULL) AND (Color = ? OR Color IS NULL) AND isActive = TRUE";
        PreparedStatement psFetchAlerts = conn.prepareStatement(fetchAlertsQuery);
        psFetchAlerts.setString(1, make);
        psFetchAlerts.setString(2, model);
        psFetchAlerts.setString(3, yearString.isEmpty() ? null : yearString);
        psFetchAlerts.setString(4, color);
        ResultSet alerts = psFetchAlerts.executeQuery();

        while (alerts.next()) {
            int userID = alerts.getInt("userID");
            String notificationMessage = "New " + make + " " + model;
            if (!yearString.isEmpty()) {
                notificationMessage += " " + yearString;
            }
            if (!color.isEmpty()) {
                notificationMessage += " in " + color;
            }
            notificationMessage += " available now!";

            // Add the notification for the user
            NotificationUtility.addNotification(conn, userID, notificationMessage);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userID"); 

        // Retrieve vehicle details from the form
        String make = request.getParameter("make");
        String model = request.getParameter("model");
        String vin = request.getParameter("vin"); 
        int year = Integer.parseInt(request.getParameter("year"));
        int mileage = Integer.parseInt(request.getParameter("mileage"));
        int categoryID = Integer.parseInt(request.getParameter("categoryID"));
        double initialPrice = Double.parseDouble(request.getParameter("initialPrice"));
        double secretMinimum = Double.parseDouble(request.getParameter("secretMinimum"));
        double bidIncrement = Double.parseDouble(request.getParameter("bidIncrement"));
        String closingTimeStr = request.getParameter("closingTime");
        Timestamp closingTime = null;
        
       
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        try {
            Date parsedDate = dateFormat.parse(closingTimeStr);
            closingTime = new Timestamp(parsedDate.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            response.sendRedirect("dashboard.jsp?error=Invalid date format.");
            return;
        }
        
      
        Part filePart = request.getPart("image");
        String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        
        
        String imagesDirPath = getServletContext().getRealPath("") + File.separator + "pictures";
        File imagesDir = new File(imagesDirPath);
        if (!imagesDir.exists()) {
            boolean created = imagesDir.mkdirs();
            if (!created) {
                System.out.println("Could not create images directory.");
                response.sendRedirect("dashboard.jsp?error=Unable to save image.");
                return;
            }
        }
        
        // Save the file on the server
        String imagePath = imagesDirPath + File.separator + fileName;
        try {
            filePart.write(imagePath);
        } catch (IOException e) {
            e.printStackTrace(); 
            response.sendRedirect("dashboard.jsp?error=Unable to save image.");
            return;
        }

        Connection connection = null;
        try {
            connection = DBConfig.getConnection();
            // Start transaction block
            connection.setAutoCommit(false);

            // Insert vehicle details into the Vehicle table
            String insertVehicleQuery = "INSERT INTO Vehicle (Make, Model, Year, Mileage, VIN, categoryID, InitialPrice, SecretMinimum, ImagePath) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement psVehicle = connection.prepareStatement(insertVehicleQuery, Statement.RETURN_GENERATED_KEYS);
            psVehicle.setString(1, make);
            psVehicle.setString(2, model);
            psVehicle.setInt(3, year);
            psVehicle.setInt(4, mileage);
            psVehicle.setString(5, vin);
            psVehicle.setInt(6, categoryID);
            psVehicle.setDouble(7, initialPrice);
            psVehicle.setDouble(8, secretMinimum);
            psVehicle.setString(9, "pictures/" + fileName);
            int vehicleRowsAffected = psVehicle.executeUpdate();

            if (vehicleRowsAffected == 0) {
                throw new SQLException("Creating vehicle failed, no rows affected.");
            }

            int vehicleID;
            try (ResultSet generatedKeys = psVehicle.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    vehicleID = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating vehicle failed, no ID obtained.");
                }
            }
            
            // Insert auction details into the Auction table with CreatorID
            String insertAuctionQuery = "INSERT INTO Auction (vehicleID, CreatorID, OpeningTime, ClosingTime, CurrentBid, BidIncrement) VALUES (?, ?, NOW(), ?, ?, ?)";
            PreparedStatement psAuction = connection.prepareStatement(insertAuctionQuery);
            psAuction.setInt(1, vehicleID);
            psAuction.setInt(2, userId); 
            psAuction.setTimestamp(3, closingTime);
            psAuction.setDouble(4, initialPrice);
            psAuction.setDouble(5, bidIncrement);
            int auctionRowsAffected = psAuction.executeUpdate();

            if (auctionRowsAffected == 0) {
                throw new SQLException("Creating auction failed, no rows affected.");
            }
            
            // Commit transaction
            connection.commit();
            checkAndNotifyAlerts(connection, vehicleID);

            // Redirect to dashboard with success message
            response.sendRedirect("dashboard.jsp?message=Auction created successfully.");
        } catch (SQLException e) {
            e.printStackTrace(); 
            if (connection != null) {
                try {
                    connection.rollback(); 
                } catch (SQLException se) {
                    se.printStackTrace(); 
                }
            }
            response.sendRedirect("dashboard.jsp?error=Error creating auction.");
        } finally {
            // End transaction block
            if (connection != null) {
                try {
                    connection.setAutoCommit(true); 
                    connection.close();
                } catch (SQLException se) {
                    se.printStackTrace(); 
                }
            }
        }
    }
}
