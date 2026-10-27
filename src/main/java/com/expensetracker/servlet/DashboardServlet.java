package com.expensetracker.servlet;

import com.expensetracker.controller.DashboardController;
import com.expensetracker.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet handling Dashboard telemetry aggregation in Apache Tomcat.
 * Maps to /api/dashboard
 */
@WebServlet(name = "DashboardServlet", urlPatterns = {"/api/dashboard"})
public class DashboardServlet extends HttpServlet {

    private final DashboardController dashboardController = new DashboardController();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = getAuthenticatedUser(req);
        if (user == null) {
            sendUnauthorized(resp);
            return;
        }

        String json = dashboardController.handleGetDashboard(user);
        resp.setContentType("application/json; charset=UTF-8");
        resp.getWriter().write(json);
    }

    private User getAuthenticatedUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            return (User) session.getAttribute("user");
        }
        return null;
    }

    private void sendUnauthorized(HttpServletResponse resp) throws IOException {
        resp.setStatus(401);
        resp.setContentType("application/json; charset=UTF-8");
        resp.getWriter().write("{\"success\":false,\"authenticated\":false,\"message\":\"Session expired. Please log in.\"}");
    }
}
