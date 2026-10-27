package com.expensetracker.service;

import com.expensetracker.dao.BudgetDao;
import com.expensetracker.dao.BudgetDaoImpl;
import com.expensetracker.dao.TransactionDao;
import com.expensetracker.dao.TransactionDaoImpl;
import com.expensetracker.exception.DatabaseException;
import com.expensetracker.exception.ValidationException;
import com.expensetracker.model.Budget;
import com.expensetracker.model.BudgetProgress;
import com.expensetracker.util.DateUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for Budget management.
 * 
 * OOP Concepts Demonstrated:
 * 1. Composition: Composes BudgetDao, TransactionDao, and AlertService.
 * 2. Method Overloading: getBudgetProgress() variants.
 */
public class BudgetServiceImpl implements BudgetService {

    private final BudgetDao budgetDao;
    private final TransactionDao transactionDao;
    private final AlertService alertService;

    public BudgetServiceImpl() {
        this(new BudgetDaoImpl(), new TransactionDaoImpl(), new AlertServiceImpl());
    }

    public BudgetServiceImpl(BudgetDao budgetDao, TransactionDao transactionDao, AlertService alertService) {
        this.budgetDao = budgetDao;
        this.transactionDao = transactionDao;
        this.alertService = alertService;
    }

    @Override
    public Budget setBudget(String category, double amountLimit, String monthYear) 
            throws ValidationException, DatabaseException {
        if (monthYear == null || monthYear.isBlank()) {
            monthYear = DateUtil.getCurrentMonthYear();
        }

        Budget budget = new Budget(1, category, amountLimit, monthYear);
        int id = budgetDao.saveOrUpdate(budget);
        budget.setId(id);

        // Immediately evaluate spending against the new budget
        alertService.evaluateSpendingAlert(category, monthYear);

        return budget;
    }

    @Override
    public Budget setBudget(int userId, String category, double amountLimit, String monthYear) 
            throws ValidationException, DatabaseException {
        return setBudget(category, amountLimit, monthYear);
    }

    @Override
    public boolean deleteBudget(int budgetId) throws DatabaseException {
        Optional<Budget> budgetOpt = budgetDao.findById(budgetId);
        if (budgetOpt.isEmpty()) {
            return false;
        }

        Budget b = budgetOpt.get();
        boolean deleted = budgetDao.delete(budgetId);
        if (deleted) {
            // Delete any existing alerts for this budget category/month
            alertService.deleteAlert(budgetId);
        }
        return deleted;
    }

    @Override
    public boolean deleteBudget(int budgetId, int userId) throws DatabaseException {
        return deleteBudget(budgetId);
    }

    @Override
    public Optional<Budget> getBudget(String category, String monthYear) throws DatabaseException {
        return budgetDao.findByCategoryAndMonth(category, monthYear);
    }

    @Override
    public Optional<Budget> getBudget(int userId, String category, String monthYear) throws DatabaseException {
        return getBudget(category, monthYear);
    }

    @Override
    public List<BudgetProgress> getBudgetProgress() throws DatabaseException {
        return getBudgetProgress(DateUtil.getCurrentMonthYear());
    }

    @Override
    public List<BudgetProgress> getBudgetProgress(String monthYear) throws DatabaseException {
        if (monthYear == null || monthYear.isBlank()) {
            monthYear = DateUtil.getCurrentMonthYear();
        }

        List<Budget> budgets = budgetDao.findByMonth(monthYear);
        List<BudgetProgress> progressList = new ArrayList<>();

        for (Budget b : budgets) {
            double spent = transactionDao.getCategorySpent(b.getCategory(), monthYear);
            BudgetProgress bp = new BudgetProgress(
                b.getId(),
                b.getCategory(),
                b.getAmountLimit(),
                spent,
                monthYear
            );
            progressList.add(bp);
        }

        return progressList;
    }

    @Override
    public List<BudgetProgress> getBudgetProgress(int userId) throws DatabaseException {
        return getBudgetProgress();
    }

    @Override
    public List<BudgetProgress> getBudgetProgress(int userId, String monthYear) throws DatabaseException {
        return getBudgetProgress(monthYear);
    }
}
