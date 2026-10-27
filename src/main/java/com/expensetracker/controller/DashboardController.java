package com.expensetracker.controller;

import com.expensetracker.model.*;
import com.expensetracker.service.*;
import com.expensetracker.util.DatabaseUtil;
import com.expensetracker.util.DateUtil;
import com.expensetracker.util.JsonUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller consolidating financial telemetry for the dashboard.
 */
public class DashboardController {

    private final TransactionService transactionService;
    private final AlertService alertService;
    private final BudgetService budgetService;

    public DashboardController() {
        this(new TransactionServiceImpl(), new AlertServiceImpl(), new BudgetServiceImpl());
    }

    public DashboardController(TransactionService transactionService, AlertService alertService) {
        this(transactionService, alertService, new BudgetServiceImpl());
    }

    public DashboardController(TransactionService transactionService, AlertService alertService, BudgetService budgetService) {
        this.transactionService = transactionService;
        this.alertService = alertService;
        this.budgetService = budgetService;
    }

    /**
     * Gathers all dashboard metrics calculated directly from the database registers.
     */
    public String handleGetDashboard() {
        Map<String, Object> response = new HashMap<>();
        try {
            String currentMonthYear = DateUtil.getCurrentMonthYear();

            // 1. Core financial metrics
            SpendingSummary summary = transactionService.getDashboardSummary();

            // 2. Recent transactions (ledger view)
            List<Transaction> recent = transactionService.getRecentTransactions(8);

            // 3. Category spending breakdown for current month
            List<CategoryTotal> categoryBreakdown = transactionService.getCategoryBreakdown(currentMonthYear);

            // 4. Monthly 6-month trend comparison
            Map<String, double[]> monthlyComparison = transactionService.getMonthlyComparison(6);

            // 5. Active unread spending alerts
            List<Alert> unreadAlerts = alertService.getAlerts(true);

            // 6. Category budgets and progress for current month
            List<BudgetProgress> budgetProgress = budgetService.getBudgetProgress(currentMonthYear);

            response.put("success", true);
            response.put("summary", summary);
            response.put("recentTransactions", recent);
            response.put("categoryBreakdown", categoryBreakdown);
            response.put("monthlyComparison", monthlyComparison);
            response.put("unreadAlerts", unreadAlerts);
            response.put("budgetProgress", budgetProgress);
            response.put("currentMonth", currentMonthYear);
            response.put("friendlyMonth", DateUtil.formatMonthFriendly(currentMonthYear));
            response.put("mysqlConnected", DatabaseUtil.isMySQLAvailable());
            response.put("dbUser", DatabaseUtil.getDbUser());
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error calculating dashboard metrics: " + e.getMessage());
        }
        return JsonUtil.toJson(response);
    }

    public String handleGetDashboard(User user) {
        return handleGetDashboard();
    }

    public String handleResetSampleData() {
        com.expensetracker.dao.TransactionDaoImpl.resetMemorySeed();
        com.expensetracker.dao.BudgetDaoImpl.resetMemorySeed();
        com.expensetracker.dao.AlertDaoImpl.resetMemorySeed();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Sample demo data loaded successfully!");
        return JsonUtil.toJson(response);
    }

    public String handleClearAllData() {
        com.expensetracker.dao.TransactionDaoImpl.clearMemoryStore();
        com.expensetracker.dao.BudgetDaoImpl.clearMemoryStore();
        com.expensetracker.dao.AlertDaoImpl.clearMemoryStore();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "All transactions, budgets, and alerts cleared.");
        return JsonUtil.toJson(response);
    }
}
