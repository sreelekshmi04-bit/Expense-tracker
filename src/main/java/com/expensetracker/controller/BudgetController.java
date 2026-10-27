package com.expensetracker.controller;

import com.expensetracker.exception.ValidationException;
import com.expensetracker.model.Budget;
import com.expensetracker.model.BudgetProgress;
import com.expensetracker.model.User;
import com.expensetracker.service.BudgetService;
import com.expensetracker.service.BudgetServiceImpl;
import com.expensetracker.util.DateUtil;
import com.expensetracker.util.JsonUtil;
import com.expensetracker.util.ValidationUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller handling Monthly Category Budget endpoints.
 */
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController() {
        this(new BudgetServiceImpl());
    }

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    public String handleListProgress(Map<String, String> params) {
        Map<String, Object> response = new HashMap<>();
        try {
            String monthYear = params.get("month");
            if (monthYear == null || monthYear.isBlank()) {
                monthYear = DateUtil.getCurrentMonthYear();
            }

            List<BudgetProgress> list = budgetService.getBudgetProgress(monthYear);

            double totalBudgeted = 0.0;
            double totalSpentInBudgets = 0.0;
            for (BudgetProgress bp : list) {
                totalBudgeted += bp.getAmountLimit();
                totalSpentInBudgets += bp.getSpentAmount();
            }

            response.put("success", true);
            response.put("budgets", list);
            response.put("month", monthYear);
            response.put("friendlyMonth", DateUtil.formatMonthFriendly(monthYear));
            response.put("totalBudgeted", Math.round(totalBudgeted * 100.0) / 100.0);
            response.put("totalSpentInBudgets", Math.round(totalSpentInBudgets * 100.0) / 100.0);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error loading budgets: " + e.getMessage());
        }
        return JsonUtil.toJson(response);
    }

    public String handleListProgress(User user, Map<String, String> params) {
        return handleListProgress(params);
    }

    public String handleSetBudget(Map<String, String> params) {
        Map<String, Object> response = new HashMap<>();
        try {
            String category = params.get("category");
            String limitStr = params.get("amountLimit");
            String monthYear = params.get("monthYear");

            double limit = ValidationUtil.parseAndValidateAmount(limitStr);
            if (monthYear == null || monthYear.isBlank()) {
                monthYear = DateUtil.getCurrentMonthYear();
            }

            Budget b = budgetService.setBudget(category, limit, monthYear);

            response.put("success", true);
            response.put("message", "Monthly budget for " + b.getCategory() + " set to ₹" + String.format("%.2f", b.getAmountLimit()) + ".");
            response.put("budget", b);
        } catch (ValidationException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to set budget: " + e.getMessage());
        }
        return JsonUtil.toJson(response);
    }

    public String handleSetBudget(User user, Map<String, String> params) {
        return handleSetBudget(params);
    }

    public String handleDeleteBudget(Map<String, String> params) {
        Map<String, Object> response = new HashMap<>();
        try {
            String idStr = params.get("id");
            if (idStr == null || idStr.isBlank()) {
                throw new ValidationException("Budget ID is required.");
            }
            int id = Integer.parseInt(idStr.trim());

            boolean deleted = budgetService.deleteBudget(id);
            if (deleted) {
                response.put("success", true);
                response.put("message", "Budget envelope removed.");
            } else {
                response.put("success", false);
                response.put("message", "Budget not found.");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to delete budget: " + e.getMessage());
        }
        return JsonUtil.toJson(response);
    }

    public String handleDeleteBudget(User user, Map<String, String> params) {
        return handleDeleteBudget(params);
    }
}
