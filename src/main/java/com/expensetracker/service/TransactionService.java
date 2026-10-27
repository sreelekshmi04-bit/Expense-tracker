package com.expensetracker.service;

import com.expensetracker.exception.DatabaseException;
import com.expensetracker.exception.ValidationException;
import com.expensetracker.model.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service interface for Transaction management.
 * Demonstrates:
 * 1. Abstraction: Contract for financial operations.
 * 2. Method Overloading: Multiple getTransactions() variants demonstrating compile-time polymorphism.
 * 3. Polymorphic Methods: calculateNetBalance() processing abstract Transaction types.
 */
public interface TransactionService {

    Transaction addTransaction(String type, double amount, 
                               LocalDate date, String description, 
                               String categoryOrSource, String paymentMethod) 
            throws ValidationException, DatabaseException;

    Transaction addTransaction(String type, int userId, double amount, 
                               LocalDate date, String description, String categoryOrSource) 
            throws ValidationException, DatabaseException;

    boolean updateTransaction(int id, String type, double amount, 
                              LocalDate date, String description, 
                              String categoryOrSource, String paymentMethod) 
            throws ValidationException, DatabaseException;

    boolean updateTransaction(int id, int userId, String type, double amount, 
                              LocalDate date, String description, String categoryOrSource) 
            throws ValidationException, DatabaseException;

    boolean deleteTransaction(int id) throws DatabaseException;
    boolean deleteTransaction(int id, int userId) throws DatabaseException;

    Optional<Transaction> getTransactionById(int id) throws DatabaseException;
    Optional<Transaction> getTransactionById(int id, int userId) throws DatabaseException;

    // =========================================================================
    // METHOD OVERLOADING (Compile-Time Polymorphism)
    // =========================================================================

    List<Transaction> getTransactions() throws DatabaseException;
    List<Transaction> getTransactions(int userId) throws DatabaseException;

    List<Transaction> getTransactions(String type) throws DatabaseException;
    List<Transaction> getTransactions(int userId, String type) throws DatabaseException;

    List<Transaction> getTransactions(LocalDate startDate, LocalDate endDate) throws DatabaseException;
    List<Transaction> getTransactions(int userId, LocalDate startDate, LocalDate endDate) throws DatabaseException;

    List<Transaction> getTransactions(String type, String categoryOrSource, 
                                      LocalDate startDate, LocalDate endDate) throws DatabaseException;
    List<Transaction> getTransactions(int userId, String type, String categoryOrSource, 
                                      LocalDate startDate, LocalDate endDate) throws DatabaseException;

    List<Transaction> getRecentTransactions(int limit) throws DatabaseException;
    List<Transaction> getRecentTransactions(int userId, int limit) throws DatabaseException;

    /**
     * Polymorphic calculation: Computes net balance across a list of Transactions.
     * Runtime polymorphism invokes getSignedAmount() on either Income (+amount)
     * or Expense (-amount) without type checks.
     */
    double calculateNetBalance(List<Transaction> transactions);

    SpendingSummary getDashboardSummary() throws DatabaseException;
    SpendingSummary getDashboardSummary(int userId) throws DatabaseException;

    List<CategoryTotal> getCategoryBreakdown(String monthYear) throws DatabaseException;
    List<CategoryTotal> getCategoryBreakdown(int userId, String monthYear) throws DatabaseException;

    Map<String, double[]> getMonthlyComparison(int monthsCount) throws DatabaseException;
    Map<String, double[]> getMonthlyComparison(int userId, int monthsCount) throws DatabaseException;
}
