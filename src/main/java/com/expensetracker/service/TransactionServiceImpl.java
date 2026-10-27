package com.expensetracker.service;

import com.expensetracker.dao.TransactionDao;
import com.expensetracker.dao.TransactionDaoImpl;
import com.expensetracker.exception.DatabaseException;
import com.expensetracker.exception.ValidationException;
import com.expensetracker.factory.TransactionFactory;
import com.expensetracker.model.*;
import com.expensetracker.util.DateUtil;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service Implementation for Transaction Management.
 * 
 * OOP Concepts Demonstrated:
 * 1. Composition: Composes TransactionDao and AlertService.
 * 2. Runtime Polymorphism: calculateNetBalance() iterates through Transaction references,
 *    invoking getSignedAmount() which executes Income.getSignedAmount() or Expense.getSignedAmount().
 * 3. Method Overloading: Provides multiple getTransactions() variants.
 */
public class TransactionServiceImpl implements TransactionService {

    private final TransactionDao transactionDao;
    private final AlertService alertService;

    public TransactionServiceImpl() {
        this(new TransactionDaoImpl(), new AlertServiceImpl());
    }

    public TransactionServiceImpl(TransactionDao transactionDao, AlertService alertService) {
        this.transactionDao = transactionDao;
        this.alertService = alertService;
    }

    @Override
    public Transaction addTransaction(String type, double amount, 
                                      LocalDate date, String description, 
                                      String categoryOrSource, String paymentMethod) 
            throws ValidationException, DatabaseException {
        // Factory pattern creates either Income or Expense instance
        Transaction tx = TransactionFactory.createTransaction(
            type, 1, amount, date, description, categoryOrSource, paymentMethod
        );

        int id = transactionDao.create(tx);
        tx.setId(id);

        // Smart Spending Alert Trigger: If Expense, evaluate budget immediately
        if ("EXPENSE".equalsIgnoreCase(tx.getTransactionType())) {
            String monthYear = DateUtil.getMonthYear(tx.getTransactionDate());
            alertService.evaluateSpendingAlert(tx.getCategoryOrSource(), monthYear);
        }

        return tx;
    }

    @Override
    public Transaction addTransaction(String type, int userId, double amount, 
                                      LocalDate date, String description, String categoryOrSource) 
            throws ValidationException, DatabaseException {
        return addTransaction(type, amount, date, description, categoryOrSource, "Cash");
    }

    @Override
    public boolean updateTransaction(int id, String type, double amount, 
                                     LocalDate date, String description, 
                                     String categoryOrSource, String paymentMethod) 
            throws ValidationException, DatabaseException {
        Optional<Transaction> oldTxOpt = transactionDao.findById(id);
        if (oldTxOpt.isEmpty()) {
            return false;
        }

        Transaction oldTx = oldTxOpt.get();
        String oldCategory = oldTx.getCategoryOrSource();
        String oldMonthYear = DateUtil.getMonthYear(oldTx.getTransactionDate());
        String oldType = oldTx.getTransactionType();

        Transaction updatedTx = TransactionFactory.createTransaction(
            type, 1, amount, date, description, categoryOrSource, paymentMethod
        );
        updatedTx.setId(id);

        boolean success = transactionDao.update(updatedTx);
        if (success) {
            // Re-evaluate alerts for the new values
            if ("EXPENSE".equalsIgnoreCase(updatedTx.getTransactionType())) {
                String newMonthYear = DateUtil.getMonthYear(updatedTx.getTransactionDate());
                alertService.evaluateSpendingAlert(updatedTx.getCategoryOrSource(), newMonthYear);
            }

            // If category or month changed, re-evaluate old category budget as well
            if ("EXPENSE".equalsIgnoreCase(oldType) && 
                (!oldCategory.equalsIgnoreCase(updatedTx.getCategoryOrSource()) || 
                 !oldMonthYear.equals(DateUtil.getMonthYear(updatedTx.getTransactionDate())))) {
                alertService.evaluateSpendingAlert(oldCategory, oldMonthYear);
            }
        }

        return success;
    }

    @Override
    public boolean updateTransaction(int id, int userId, String type, double amount, 
                                     LocalDate date, String description, String categoryOrSource) 
            throws ValidationException, DatabaseException {
        return updateTransaction(id, type, amount, date, description, categoryOrSource, "Cash");
    }

    @Override
    public boolean deleteTransaction(int id) throws DatabaseException {
        Optional<Transaction> oldTxOpt = transactionDao.findById(id);
        if (oldTxOpt.isEmpty()) {
            return false;
        }

        Transaction oldTx = oldTxOpt.get();
        boolean deleted = transactionDao.delete(id);

        if (deleted && "EXPENSE".equalsIgnoreCase(oldTx.getTransactionType())) {
            String monthYear = DateUtil.getMonthYear(oldTx.getTransactionDate());
            // Re-evaluate alerts because spending decreased
            alertService.evaluateSpendingAlert(oldTx.getCategoryOrSource(), monthYear);
        }

        return deleted;
    }

    @Override
    public boolean deleteTransaction(int id, int userId) throws DatabaseException {
        return deleteTransaction(id);
    }

    @Override
    public Optional<Transaction> getTransactionById(int id) throws DatabaseException {
        return transactionDao.findById(id);
    }

    @Override
    public Optional<Transaction> getTransactionById(int id, int userId) throws DatabaseException {
        return getTransactionById(id);
    }

    // =========================================================================
    // METHOD OVERLOADING (Compile-time Polymorphism)
    // =========================================================================

    @Override
    public List<Transaction> getTransactions() throws DatabaseException {
        return transactionDao.findAll();
    }

    @Override
    public List<Transaction> getTransactions(int userId) throws DatabaseException {
        return getTransactions();
    }

    @Override
    public List<Transaction> getTransactions(String type) throws DatabaseException {
        return transactionDao.findByFilters(type, null, null, null);
    }

    @Override
    public List<Transaction> getTransactions(int userId, String type) throws DatabaseException {
        return getTransactions(type);
    }

    @Override
    public List<Transaction> getTransactions(LocalDate startDate, LocalDate endDate) throws DatabaseException {
        return transactionDao.findByFilters(null, null, startDate, endDate);
    }

    @Override
    public List<Transaction> getTransactions(int userId, LocalDate startDate, LocalDate endDate) throws DatabaseException {
        return getTransactions(startDate, endDate);
    }

    @Override
    public List<Transaction> getTransactions(String type, String categoryOrSource, 
                                            LocalDate startDate, LocalDate endDate) throws DatabaseException {
        return transactionDao.findByFilters(type, categoryOrSource, startDate, endDate);
    }

    @Override
    public List<Transaction> getTransactions(int userId, String type, String categoryOrSource, 
                                            LocalDate startDate, LocalDate endDate) throws DatabaseException {
        return getTransactions(type, categoryOrSource, startDate, endDate);
    }

    @Override
    public List<Transaction> getRecentTransactions(int limit) throws DatabaseException {
        return transactionDao.findRecent(limit);
    }

    @Override
    public List<Transaction> getRecentTransactions(int userId, int limit) throws DatabaseException {
        return getRecentTransactions(limit);
    }

    // =========================================================================
    // RUNTIME POLYMORPHISM DEMONSTRATION
    // =========================================================================

    /**
     * Demonstrates Runtime Polymorphism:
     * Iterates through a collection of abstract Transaction references.
     * At runtime, the JVM invokes the overridden getSignedAmount() method of either
     * Income (+amount) or Expense (-amount), calculating the accurate net balance.
     */
    @Override
    public double calculateNetBalance(List<Transaction> transactions) {
        if (transactions == null) return 0.0;
        double net = 0.0;
        for (Transaction tx : transactions) {
            // Polymorphic method call
            net += tx.getSignedAmount();
        }
        return Math.round(net * 100.0) / 100.0;
    }

    @Override
    public SpendingSummary getDashboardSummary() throws DatabaseException {
        String currentMonthYear = DateUtil.getCurrentMonthYear();

        double totalIncome = transactionDao.getTotalIncome();
        double totalExpense = transactionDao.getTotalExpense();
        double monthlyIncome = transactionDao.getMonthlyIncome(currentMonthYear);
        double monthlyExpense = transactionDao.getMonthlyExpense(currentMonthYear);

        return new SpendingSummary(totalIncome, totalExpense, monthlyIncome, monthlyExpense);
    }

    @Override
    public SpendingSummary getDashboardSummary(int userId) throws DatabaseException {
        return getDashboardSummary();
    }

    @Override
    public List<CategoryTotal> getCategoryBreakdown(String monthYear) throws DatabaseException {
        return transactionDao.getCategoryBreakdown(monthYear);
    }

    @Override
    public List<CategoryTotal> getCategoryBreakdown(int userId, String monthYear) throws DatabaseException {
        return getCategoryBreakdown(monthYear);
    }

    @Override
    public Map<String, double[]> getMonthlyComparison(int monthsCount) throws DatabaseException {
        return transactionDao.getMonthlyComparison(monthsCount > 0 ? monthsCount : 6);
    }

    @Override
    public Map<String, double[]> getMonthlyComparison(int userId, int monthsCount) throws DatabaseException {
        return getMonthlyComparison(monthsCount);
    }
}
