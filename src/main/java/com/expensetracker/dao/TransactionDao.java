package com.expensetracker.dao;

import com.expensetracker.exception.DatabaseException;
import com.expensetracker.model.CategoryTotal;
import com.expensetracker.model.Transaction;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Data Access Object interface for Transactions.
 * 
 * OOP Concepts Demonstrated:
 * 1. Abstraction: Defines database operations without exposing JDBC or SQL details.
 * 2. Polymorphic Operations: Methods accept and return the abstract Transaction base class,
 *    allowing transparent manipulation of Income and Expense instances at runtime.
 * 3. Method Overloading (Compile-time Polymorphism): Provides both single-user and
 *    user-scoped method variants.
 */
public interface TransactionDao {

    int create(Transaction transaction) throws DatabaseException;

    boolean update(Transaction transaction) throws DatabaseException;

    boolean delete(int id) throws DatabaseException;
    boolean delete(int id, int userId) throws DatabaseException;

    Optional<Transaction> findById(int id) throws DatabaseException;
    Optional<Transaction> findById(int id, int userId) throws DatabaseException;

    List<Transaction> findAll() throws DatabaseException;
    List<Transaction> findByUserId(int userId) throws DatabaseException;

    List<Transaction> findRecent(int limit) throws DatabaseException;
    List<Transaction> findRecent(int userId, int limit) throws DatabaseException;

    List<Transaction> findByFilters(String type, String categoryOrSource, 
                                    LocalDate startDate, LocalDate endDate) throws DatabaseException;
    List<Transaction> findByFilters(int userId, String type, String categoryOrSource, 
                                    LocalDate startDate, LocalDate endDate) throws DatabaseException;

    double getTotalIncome() throws DatabaseException;
    double getTotalIncome(int userId) throws DatabaseException;

    double getTotalExpense() throws DatabaseException;
    double getTotalExpense(int userId) throws DatabaseException;

    double getMonthlyIncome(String monthYear) throws DatabaseException;
    double getMonthlyIncome(int userId, String monthYear) throws DatabaseException;

    double getMonthlyExpense(String monthYear) throws DatabaseException;
    double getMonthlyExpense(int userId, String monthYear) throws DatabaseException;

    double getCategorySpent(String category, String monthYear) throws DatabaseException;
    double getCategorySpent(int userId, String category, String monthYear) throws DatabaseException;

    List<CategoryTotal> getCategoryBreakdown(String monthYear) throws DatabaseException;
    List<CategoryTotal> getCategoryBreakdown(int userId, String monthYear) throws DatabaseException;

    Map<String, double[]> getMonthlyComparison(int monthsCount) throws DatabaseException;
    Map<String, double[]> getMonthlyComparison(int userId, int monthsCount) throws DatabaseException;
}
