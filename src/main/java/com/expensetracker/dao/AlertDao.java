package com.expensetracker.dao;

import com.expensetracker.exception.DatabaseException;
import com.expensetracker.model.Alert;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Smart Spending Alerts.
 * 
 * OOP Concepts Demonstrated:
 * 1. Abstraction: Defines contract for alert persistence.
 * 2. Method Overloading (Compile-time Polymorphism): Provides both
 *    single-user and user-scoped methods.
 */
public interface AlertDao {

    int create(Alert alert) throws DatabaseException;

    boolean update(Alert alert) throws DatabaseException;

    boolean markAsRead(int alertId) throws DatabaseException;
    boolean markAsRead(int alertId, int userId) throws DatabaseException;

    boolean markAllAsRead() throws DatabaseException;
    boolean markAllAsRead(int userId) throws DatabaseException;

    List<Alert> findAll(boolean unreadOnly) throws DatabaseException;
    List<Alert> findByUser(int userId, boolean unreadOnly) throws DatabaseException;

    Optional<Alert> findLatestByCategoryAndMonth(String category, String monthYear) throws DatabaseException;
    Optional<Alert> findLatestByCategoryAndMonth(int userId, String category, String monthYear) throws DatabaseException;

    boolean delete(int alertId) throws DatabaseException;
    boolean delete(int alertId, int userId) throws DatabaseException;

    boolean deleteByCategoryAndMonth(String category, String monthYear) throws DatabaseException;
    boolean deleteByCategoryAndMonth(int userId, String category, String monthYear) throws DatabaseException;
}
