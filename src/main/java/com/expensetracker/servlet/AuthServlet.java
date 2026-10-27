package com.expensetracker.servlet;

import com.expensetracker.controller.AuthController;
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
 * Servlet handling user authentication in Apache Tomcat.
 * Maps to /api/auth
 */
@WebServlet(name = "AuthServlet", urlPatterns = {"/api/auth"})
public class AuthServlet extends HttpServlet {

    private final AuthController authController = new AuthController();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(true);
        Map<String, Object> sessionMap = getSessionMap(session);

        String json = authController.handleGetCurrentUser(sessionMap);
        writeResponse(resp, json);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(true);
        Map<String, Object> sessionMap = getSessionMap(session);

        Map<String, String> params = new HashMap<>();
        req.getParameterMap().forEach((k, v) -> {
            if (v != null && v.length > 0) params.put(k, v[0]);
        });

        String action = params.getOrDefault("action", "");
        String json;
        if ("register".equalsIgnoreCase(action)) {
            json = authController.handleRegister(params, sessionMap);
        } else if ("login".equalsIgnoreCase(action)) {
            json = authController.handleLogin(params, sessionMap);
        } else if ("logout".equalsIgnoreCase(action)) {
            json = authController.handleLogout(sessionMap);
            session.invalidate();
        } else {
            json = "{\"success\":false,\"message\":\"Unknown action\"}";
        }

        // Sync session map back to HttpSession
        if (sessionMap.containsKey("user")) {
            session.setAttribute("user", sessionMap.get("user"));
        } else {
            session.removeAttribute("user");
        }

        writeResponse(resp, json);
    }

    private Map<String, Object> getSessionMap(HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        if (session != null && session.getAttribute("user") != null) {
            map.put("user", session.getAttribute("user"));
        }
        return map;
    }

    private void writeResponse(HttpServletResponse resp, String json) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        resp.getWriter().write(json);
    }
}
