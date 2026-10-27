package com.expensetracker.service;

import com.expensetracker.exception.DatabaseException;
import com.expensetracker.exception.ValidationException;
import com.expensetracker.model.Budget;
import com.expensetracker.model.BudgetProgress;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for Monthly Budget Management.
 * Demonstrates Abstraction and Method Overloading.
 */
public interface BudgetService {

    Budget setBudget(String category, double amountLimit, String monthYear) 
            throws ValidationException, DatabaseException;
    Budget setBudget(int userId, String category, double amountLimit, String monthYear) 
            throws ValidationException, DatabaseException;

    boolean deleteBudget(int budgetId) throws DatabaseException;
    boolean deleteBudget(int budgetId, int userId) throws DatabaseException;

    Optional<Budget> getBudget(String category, String monthYear) throws DatabaseException;
    Optional<Budget> getBudget(int userId, String category, String monthYear) throws DatabaseException;

    // Method Overloading (Compile-time polymorphism)
    List<BudgetProgress> getBudgetProgress() throws DatabaseException;
    List<BudgetProgress> getBudgetProgress(String monthYear) throws DatabaseException;
    List<BudgetProgress> getBudgetProgress(int userId) throws DatabaseException;
    List<BudgetProgress> getBudgetProgress(int userId, String monthYear) throws DatabaseException;
}
