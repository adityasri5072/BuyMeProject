package com.buymeproject.servlets;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.buymeproject.database.DBConfig;

@WebServlet("/SimilarItemsServlet")
public class SimilarItemsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String make = request.getParameter("make");
        String model = request.getParameter("model");
        String sql;

        try (Connection conn = DBConfig.getConnection()) {
            if (model != null && !model.isEmpty()) {
                sql = "SELECT Vehicle.Make, Vehicle.Model, Vehicle.Year, Auction.auctionID, Auction.CurrentBid, " +
                      "Auction.ClosingTime, Vehicle.ImagePath FROM Auction " +
                      "JOIN Vehicle ON Auction.vehicleID = Vehicle.vehicleID " +
                      "WHERE Vehicle.Make = ? AND Vehicle.Model = ? " +
                      "AND Auction.ClosingTime BETWEEN DATE_SUB(NOW(), INTERVAL 1 MONTH) AND NOW()";
            } else {
                sql = "SELECT Vehicle.Make, Vehicle.Model, Vehicle.Year, Auction.auctionID, Auction.CurrentBid, " +
                      "Auction.ClosingTime, Vehicle.ImagePath FROM Auction " +
                      "JOIN Vehicle ON Auction.vehicleID = Vehicle.vehicleID " +
                      "WHERE Vehicle.Make = ? " +
                      "AND Auction.ClosingTime BETWEEN DATE_SUB(NOW(), INTERVAL 1 MONTH) AND NOW()";
            }

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, make);
            if (model != null && !model.isEmpty()) {
                stmt.setString(2, model);
            }
            ResultSet rs = stmt.executeQuery();

            request.setAttribute("similarItems", rs);
            RequestDispatcher dispatcher = request.getRequestDispatcher("similarItems.jsp");
            dispatcher.forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Database access error", e);
        }
    }
}
