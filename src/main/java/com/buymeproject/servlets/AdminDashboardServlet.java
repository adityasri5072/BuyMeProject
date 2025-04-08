package com.buymeproject.servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;

public class AdminDashboardServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");

        if (isAdmin != null && isAdmin) {
            request.getRequestDispatcher("/adminDashboard.jsp").forward(request, response);
        } else {
            response.sendRedirect("login.jsp?error=Unauthorized Access");
        }
    }
}
