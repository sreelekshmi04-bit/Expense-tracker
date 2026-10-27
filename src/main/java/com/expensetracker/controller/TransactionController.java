package com.expensetracker.controller;

import com.expensetracker.exception.ValidationException;
import com.expensetracker.model.Alert;
import com.expensetracker.model.Transaction;
import com.expensetracker.model.User;
import com.expensetracker.service.AlertService;
import com.expensetracker.service.AlertServiceImpl;
import com.expensetracker.service.TransactionService;
import com.expensetracker.service.TransactionServiceImpl;
import com.expensetracker.util.DateUtil;
import com.expensetracker.util.JsonUtil;
import com.expensetracker.util.ValidationUtil;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller handling Transaction management requests.
 * Supports direct single-user operations and payment methods.
 */
public class TransactionController {

    private final TransactionService transactionService;
    private final AlertService alertService;

    public TransactionController() {
        this(new TransactionServiceImpl(), new AlertServiceImpl());
    }

    public TransactionController(TransactionService transactionService) {
        this(transactionService, new AlertServiceImpl());
    }

    public TransactionController(TransactionService transactionService, AlertService alertService) {
        this.transactionService = transactionService;
        this.alertService = alertService;
    }

    public String handleList(Map<String, String> params) {
        Map<String, Object> response = new HashMap<>();
        try {
            String type = params.get("type");
            String category = params.get("category");
            String startStr = params.get("startDate");
            String endStr = params.get("endDate");

            LocalDate startDate = (startStr != null && !startStr.isBlank()) ? LocalDate.parse(startStr.trim()) : null;
            LocalDate endDate = (endStr != null && !endStr.isBlank()) ? LocalDate.parse(endStr.trim()) : null;

            List<Transaction> list = transactionService.getTransactions(type, category, startDate, endDate);
            double netBalance = transactionService.calculateNetBalance(list);

            response.put("success", true);
            response.put("transactions", list);
            response.put("totalCount", list.size());
            response.put("netBalance", netBalance);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error loading transactions: " + e.getMessage());
        }
        return JsonUtil.toJson(response);
    }

    public String handleList(User user, Map<String, String> params) {
        return handleList(params);
    }

    public String handleAdd(Map<String, String> params) {
        Map<String, Object> response = new HashMap<>();
        try {
            String type = params.get("type");
            String amountStr = params.get("amount");
            String dateStr = params.get("date");
            String description = params.get("description");
            String categoryOrSource = params.get("categoryOrSource");
            String paymentMethod = params.getOrDefault("paymentMethod", "Cash");

            double amount = ValidationUtil.parseAndValidateAmount(amountStr);
            LocalDate date = ValidationUtil.parseAndValidateDate(dateStr);

            Transaction tx = transactionService.addTransaction(
                type, amount, date, description, categoryOrSource, paymentMethod
            );

            response.put("success", true);
            response.put("message", tx.getTransactionType() + " of ₹" + String.format("%.2f", tx.getAmount()) + " added successfully.");
            response.put("transaction", tx);

            // If expense, check if an alert was triggered or escalated
            if ("EXPENSE".equalsIgnoreCase(tx.getTransactionType())) {
                String my = DateUtil.getMonthYear(date);
                Optional<Alert> alertOpt = alertService.evaluateSpendingAlert(categoryOrSource, my);
                alertOpt.ifPresent(alert -> response.put("alert", alert));
            }
        } catch (ValidationException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to add transaction: " + e.getMessage());
        }
        return JsonUtil.toJson(response);
    }

    public String handleAdd(User user, Map<String, String> params) {
        return handleAdd(params);
    }

    public String handleUpdate(Map<String, String> params) {
        Map<String, Object> response = new HashMap<>();
        try {
            String idStr = params.get("id");
            if (idStr == null || idStr.isBlank()) {
                throw new ValidationException("Transaction ID is required for update.");
            }
            int id = Integer.parseInt(idStr.trim());

            String type = params.get("type");
            String amountStr = params.get("amount");
            String dateStr = params.get("date");
            String description = params.get("description");
            String categoryOrSource = params.get("categoryOrSource");
            String paymentMethod = params.getOrDefault("paymentMethod", "Cash");

            double amount = ValidationUtil.parseAndValidateAmount(amountStr);
            LocalDate date = ValidationUtil.parseAndValidateDate(dateStr);

            boolean updated = transactionService.updateTransaction(
                id, type, amount, date, description, categoryOrSource, paymentMethod
            );

            if (updated) {
                response.put("success", true);
                response.put("message", "Transaction updated successfully.");

                if ("EXPENSE".equalsIgnoreCase(type)) {
                    String my = DateUtil.getMonthYear(date);
                    Optional<Alert> alertOpt = alertService.evaluateSpendingAlert(categoryOrSource, my);
                    alertOpt.ifPresent(alert -> response.put("alert", alert));
                }
            } else {
                response.put("success", false);
                response.put("message", "Transaction not found.");
            }
        } catch (ValidationException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to update transaction: " + e.getMessage());
        }
        return JsonUtil.toJson(response);
    }

    public String handleUpdate(User user, Map<String, String> params) {
        return handleUpdate(params);
    }

    public String handleDelete(Map<String, String> params) {
        Map<String, Object> response = new HashMap<>();
        try {
            String idStr = params.get("id");
            if (idStr == null || idStr.isBlank()) {
                throw new ValidationException("Transaction ID is required for deletion.");
            }
            int id = Integer.parseInt(idStr.trim());

            Optional<Transaction> txOpt = transactionService.getTransactionById(id);
            boolean deleted = transactionService.deleteTransaction(id);
            if (deleted) {
                response.put("success", true);
                response.put("message", "Transaction deleted successfully.");

                // If expense was deleted, re-evaluate category budget to clear/downgrade alert if under threshold
                if (txOpt.isPresent() && "EXPENSE".equalsIgnoreCase(txOpt.get().getTransactionType())) {
                    String my = DateUtil.getMonthYear(txOpt.get().getTransactionDate());
                    alertService.evaluateSpendingAlert(txOpt.get().getCategoryOrSource(), my);
                }
            } else {
                response.put("success", false);
                response.put("message", "Transaction not found.");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to delete transaction: " + e.getMessage());
        }
        return JsonUtil.toJson(response);
    }

    public String handleDelete(User user, Map<String, String> params) {
        return handleDelete(params);
    }

    public String handleGetById(Map<String, String> params) {
        Map<String, Object> response = new HashMap<>();
        try {
            int id = Integer.parseInt(params.get("id"));
            Optional<Transaction> txOpt = transactionService.getTransactionById(id);
            if (txOpt.isPresent()) {
                response.put("success", true);
                response.put("transaction", txOpt.get());
            } else {
                response.put("success", false);
                response.put("message", "Transaction not found.");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
        }
        return JsonUtil.toJson(response);
    }

    public String handleGetById(User user, Map<String, String> params) {
        return handleGetById(params);
    }
}
