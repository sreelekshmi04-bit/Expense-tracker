package com.expensetracker.servlet;

import com.expensetracker.controller.ReportController;
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
 * Servlet handling Reports and CSV downloads in Apache Tomcat.
 * Maps to /api/reports
 */
@WebServlet(name = "ReportServlet", urlPatterns = {"/api/reports"})
public class ReportServlet extends HttpServlet {

    private final ReportController reportController = new ReportController();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = getAuthenticatedUser(req);
        if (user == null) {
            sendUnauthorized(resp);
            return;
        }

        Map<String, String> params = extractParams(req);
        String format = params.get("format");

        if ("csv".equalsIgnoreCase(format) || "txt".equalsIgnoreCase(format)) {
            ReportController.ExportResult result = reportController.handleExport(user, params, format);
            resp.setContentType(result.contentType);
            resp.setHeader("Content-Disposition", "attachment; filename=\"" + result.filename + "\"");
            resp.getWriter().write(result.content);
        } else {
            String json = reportController.handleGetFilteredReport(user, params);
            resp.setContentType("application/json; charset=UTF-8");
            resp.getWriter().write(json);
        }
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

    private void sendUnauthorized(HttpServletResponse resp) throws IOException {
        resp.setStatus(401);
        resp.setContentType("application/json; charset=UTF-8");
        resp.getWriter().write("{\"success\":false,\"authenticated\":false,\"message\":\"Session expired. Please log in.\"}");
    }
}
