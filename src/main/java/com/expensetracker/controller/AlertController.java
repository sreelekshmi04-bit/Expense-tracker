package com.expensetracker.controller;

import com.expensetracker.model.Alert;
import com.expensetracker.model.User;
import com.expensetracker.service.AlertService;
import com.expensetracker.service.AlertServiceImpl;
import com.expensetracker.util.JsonUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller handling Smart Spending Alert notifications (Star Feature).
 */
public class AlertController {

    private final AlertService alertService;

    public AlertController() {
        this(new AlertServiceImpl());
    }

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    public String handleListAlerts(Map<String, String> params) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean unreadOnly = "true".equalsIgnoreCase(params.get("unreadOnly"));
            List<Alert> alerts = alertService.getAlerts(unreadOnly);

            long unreadCount = alerts.stream().filter(a -> !a.isRead()).count();

            response.put("success", true);
            response.put("alerts", alerts);
            response.put("unreadCount", unreadCount);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error loading spending alerts: " + e.getMessage());
        }
        return JsonUtil.toJson(response);
    }

    public String handleListAlerts(User user, Map<String, String> params) {
        return handleListAlerts(params);
    }

    public String handleMarkRead(Map<String, String> params) {
        Map<String, Object> response = new HashMap<>();
        try {
            int id = Integer.parseInt(params.get("id"));
            boolean marked = alertService.acknowledgeAlert(id);
            response.put("success", marked);
            response.put("message", marked ? "Alert acknowledged." : "Alert not found.");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
        }
        return JsonUtil.toJson(response);
    }

    public String handleMarkRead(User user, Map<String, String> params) {
        return handleMarkRead(params);
    }

    public String handleMarkAllRead() {
        Map<String, Object> response = new HashMap<>();
        try {
            alertService.acknowledgeAllAlerts();
            response.put("success", true);
            response.put("message", "All alerts marked as read.");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
        }
        return JsonUtil.toJson(response);
    }

    public String handleMarkAllRead(User user) {
        return handleMarkAllRead();
    }

    public String handleDelete(Map<String, String> params) {
        Map<String, Object> response = new HashMap<>();
        try {
            int id = Integer.parseInt(params.get("id"));
            boolean deleted = alertService.deleteAlert(id);
            response.put("success", deleted);
            response.put("message", deleted ? "Alert dismissed." : "Alert not found.");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
        }
        return JsonUtil.toJson(response);
    }

    public String handleDelete(User user, Map<String, String> params) {
        return handleDelete(params);
    }
}
