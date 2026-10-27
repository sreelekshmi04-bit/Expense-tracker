package com.expensetracker.service;

import com.expensetracker.exception.DatabaseException;
import com.expensetracker.model.Alert;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for the Smart Spending Alert system (Star Feature).
 * 
 * OOP Concepts Demonstrated:
 * 1. Abstraction: Hides the complex spending analysis and budget comparison behind
 *    a concise business service interface.
 * 2. Method Overloading: Provides both single-user and user-scoped methods.
 */
public interface AlertService {

    /**
     * Evaluates category spending against the monthly budget limit.
     * Generates a WARNING at >=80% or OVERSPENT at >100%.
     * 
     * @param category expense category (e.g. Food, Transport)
     * @param monthYear month string (e.g. "2026-10")
     * @return Optional containing the triggered Alert, or empty if within safe bounds (<80%)
     */
    Optional<Alert> evaluateSpendingAlert(String category, String monthYear) throws DatabaseException;
    Optional<Alert> evaluateSpendingAlert(int userId, String category, String monthYear) throws DatabaseException;

    /**
     * Fetches unread or all alerts.
     */
    List<Alert> getAlerts(boolean unreadOnly) throws DatabaseException;
    List<Alert> getUserAlerts(int userId, boolean unreadOnly) throws DatabaseException;

    /**
     * Marks an alert as acknowledged / read.
     */
    boolean acknowledgeAlert(int alertId) throws DatabaseException;
    boolean acknowledgeAlert(int alertId, int userId) throws DatabaseException;

    /**
     * Marks all unread alerts as read.
     */
    boolean acknowledgeAllAlerts() throws DatabaseException;
    boolean acknowledgeAllAlerts(int userId) throws DatabaseException;

    /**
     * Deletes an alert notification.
     */
    boolean deleteAlert(int alertId) throws DatabaseException;
    boolean deleteAlert(int alertId, int userId) throws DatabaseException;
}
