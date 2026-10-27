package com.expensetracker.servlet;

import com.expensetracker.controller.BudgetController;
import com.expensetracker.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Servlet handling Budget operations in Apache Tomcat.
 * Maps to /api/budgets
 */
@WebServlet(name = "BudgetServlet", urlPatterns = {"/api/budgets"})
public class BudgetServlet extends HttpServlet {

    private final BudgetController budgetController = new BudgetController();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = getAuthenticatedUser(req);
        if (user == null) {
            sendUnauthorized(resp);
            return;
        }

        Map<String, String> params = extractParams(req);
        String json = budgetController.handleListProgress(user, params);
        writeResponse(resp, json);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = getAuthenticatedUser(req);
        if (user == null) {
            sendUnauthorized(resp);
            return;
        }

        Map<String, String> params = extractParams(req);
        String action = params.getOrDefault("action", "set");
        String json;
        if ("delete".equalsIgnoreCase(action)) {
            json = budgetController.handleDeleteBudget(user, params);
        } else {
            json = budgetController.handleSetBudget(user, params);
        }

        writeResponse(resp, json);
    }

    private User getAuthenticatedUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            return (User) session.getAttribute("user");
        }
        return null;
    }

    private Map<String, String> extractParams(HttpServletRequest req) {
        Map<String, String> params = new HashMap<>();
        req.getParameterMap().forEach((k, v) -> {
            if (v != null && v.length > 0) params.put(k, v[0]);
        });
        return params;
    }

    private void writeResponse(HttpServletResponse resp, String json) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        resp.getWriter().write(json);
    }

    private void sendUnauthorized(HttpServletResponse resp) throws IOException {
        resp.setStatus(401);
        resp.setContentType("application/json; charset=UTF-8");
        resp.getWriter().write("{\"success\":false,\"authenticated\":false,\"message\":\"Session expired. Please log in.\"}");
    }
}
