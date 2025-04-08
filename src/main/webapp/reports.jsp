<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.buymeproject.servlets.ReportRecord" %>
<%@ page import="java.util.*" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Reports Page</title>
    <style>
        table {
            width: 100%;
            border-collapse: collapse;
        }
        table, th, td {
            border: 1px solid black;
        }
        th, td {
            padding: 8px;
            text-align: left;
        }
        th {
            background-color: #f2f2f2;
        }
    </style>
</head>
<body>
    <h1>View Reports</h1>
    <table>
        <thead>
            <tr>
                <th>Identifier</th>
                <th>Earnings</th>
                <th>Auction IDs</th>
                <th>Winner</th>
            </tr>
        </thead>
        <tbody>
            <% 
            List<ReportRecord> records = (List<ReportRecord>) request.getAttribute("records");
            if (records != null) {
                out.println("Number of records: " + records.size());
                for (ReportRecord record : records) {
            %>
            <tr>
                <td><%= record.getIdentifier() %></td>
                <td><%= record.getResult().toString() %></td>
                <td><%= record.getAuctionIDs() %></td>
                <td><%= record.getWinner() %></td>
            </tr>
            <% 
                } 
            } else {
            %>
            <tr>
                <td colspan="4">No data available</td>
            </tr>
            <% 
            }
            %>
        </tbody>
    </table>
    <br>
    <button onclick="window.location.href='reportsSelection.jsp'">Back to Report Selection</button>
</body>
</html>
