package com.expensetracker.service;

import com.expensetracker.dao.*;
import com.expensetracker.exception.DatabaseException;
import com.expensetracker.model.Alert;
import com.expensetracker.model.Budget;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of AlertService (Star Feature).
 * 
 * OOP Concepts Demonstrated:
 * 1. Composition: Composes AlertDao, BudgetDao, and TransactionDao to execute
 *    complex business domain calculations.
 * 2. Separation of Concerns: Database queries and alert rules are segregated.
 * 3. Idempotency & Duplicate Prevention: Updates existing alert record for the same
 *    category and month rather than spamming duplicate alerts.
 * 4. Method Overloading: Provides both single-user and user-scoped methods.
 */
public class AlertServiceImpl implements AlertService {

    private final AlertDao alertDao;
    private final BudgetDao budgetDao;
    private final TransactionDao transactionDao;

    public AlertServiceImpl() {
        this(new AlertDaoImpl(), new BudgetDaoImpl(), new TransactionDaoImpl());
    }

    public AlertServiceImpl(AlertDao alertDao, BudgetDao budgetDao, TransactionDao transactionDao) {
        this.alertDao = alertDao;
        this.budgetDao = budgetDao;
        this.transactionDao = transactionDao;
    }

    @Override
    public Optional<Alert> evaluateSpendingAlert(String category, String monthYear) throws DatabaseException {
        if (category == null || category.isBlank() || monthYear == null || monthYear.isBlank()) {
            return Optional.empty();
        }

        // 1. Check if user configured a budget for this category & month
        Optional<Budget> budgetOpt = budgetDao.findByCategoryAndMonth(category, monthYear);
        if (budgetOpt.isEmpty()) {
            // No budget set for this category in this month, so no alert needed
            return Optional.empty();
        }

        Budget budget = budgetOpt.get();
        double limit = budget.getAmountLimit();
        if (limit <= 0.0) {
            return Optional.empty();
        }

        // 2. Calculate actual category spending for this month
        double spent = transactionDao.getCategorySpent(category, monthYear);
        spent = Math.round(spent * 100.0) / 100.0;
        double percentage = Math.round(((spent / limit) * 100.0) * 10.0) / 10.0;
        double difference = Math.round((spent - limit) * 100.0) / 100.0;

        // 3. Determine alert state
        String targetAlertType = null;
        String message = null;

        if (spent > limit) {
            // OVERSPENT state (> 100%)
            targetAlertType = "OVERSPENT";
            double overAmount = Math.abs(difference);
            message = String.format(
                "%s Budget Exceeded! You have spent ₹%,.2f out of your ₹%,.2f budget. You are ₹%,.2f over budget. Consider reducing your remaining %s expenses this month.",
                category, spent, limit, overAmount, category
            );
        } else if (percentage >= 80.0) {
            // WARNING threshold state (80% - 100%)
            targetAlertType = "WARNING";
            double remaining = Math.round((limit - spent) * 100.0) / 100.0;
            message = String.format(
                "%s Budget Warning! You have spent ₹%,.2f out of your ₹%,.2f budget (%.1f%% used). You have ₹%,.2f remaining for this month.",
                category, spent, limit, percentage, remaining
            );
        } else {
            // SAFE state (< 80%): If expenses were deleted or reduced, clean up prior alert for this category/month
            alertDao.deleteByCategoryAndMonth(category, monthYear);
            return Optional.empty();
        }

        // 4. Duplicate prevention & State Transition
        Optional<Alert> existingAlertOpt = alertDao.findLatestByCategoryAndMonth(category, monthYear);

        if (existingAlertOpt.isPresent()) {
            Alert existing = existingAlertOpt.get();
            existing.setAlertType(targetAlertType);
            existing.setSpentAmount(spent);
            existing.setBudgetLimit(limit);
            existing.setDifferenceAmount(difference);
            existing.setThresholdPercent(percentage);
            existing.setMessage(message);
            existing.setRead(false); // Make sure alert is visible
            alertDao.update(existing);
            return Optional.of(existing);
        }

        // Create new alert if first time crossing threshold
        Alert newAlert = new Alert(
            1, category, monthYear, targetAlertType,
            percentage, spent, limit, difference, message
        );
        int alertId = alertDao.create(newAlert);
        newAlert.setId(alertId);
        return Optional.of(newAlert);
    }

    @Override
    public Optional<Alert> evaluateSpendingAlert(int userId, String category, String monthYear) throws DatabaseException {
        return evaluateSpendingAlert(category, monthYear);
    }

    @Override
    public List<Alert> getAlerts(boolean unreadOnly) throws DatabaseException {
        return alertDao.findAll(unreadOnly);
    }

    @Override
    public List<Alert> getUserAlerts(int userId, boolean unreadOnly) throws DatabaseException {
        return getAlerts(unreadOnly);
    }

    @Override
    public boolean acknowledgeAlert(int alertId) throws DatabaseException {
        return alertDao.markAsRead(alertId);
    }

    @Override
    public boolean acknowledgeAlert(int alertId, int userId) throws DatabaseException {
        return acknowledgeAlert(alertId);
    }

    @Override
    public boolean acknowledgeAllAlerts() throws DatabaseException {
        return alertDao.markAllAsRead();
    }

    @Override
    public boolean acknowledgeAllAlerts(int userId) throws DatabaseException {
        return acknowledgeAllAlerts();
    }

    @Override
    public boolean deleteAlert(int alertId) throws DatabaseException {
        return alertDao.delete(alertId);
    }

    @Override
    public boolean deleteAlert(int alertId, int userId) throws DatabaseException {
        return deleteAlert(alertId);
    }
}
