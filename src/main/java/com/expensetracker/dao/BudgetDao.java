package com.expensetracker.dao;

import com.expensetracker.exception.DatabaseException;
import com.expensetracker.model.Budget;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Budgets.
 * 
 * OOP Concepts Demonstrated:
 * 1. Abstraction: Contract defining budget persistence operations.
 * 2. Method Overloading (Compile-time Polymorphism): Both single-user
 *    and user-scoped method signatures are supported.
 */
public interface BudgetDao {

    int saveOrUpdate(Budget budget) throws DatabaseException;

    boolean delete(int id) throws DatabaseException;
    boolean delete(int id, int userId) throws DatabaseException;

    Optional<Budget> findById(int id) throws DatabaseException;
    Optional<Budget> findById(int id, int userId) throws DatabaseException;

    Optional<Budget> findByCategoryAndMonth(String category, String monthYear) throws DatabaseException;
    Optional<Budget> findByCategoryAndMonth(int userId, String category, String monthYear) throws DatabaseException;

    List<Budget> findByMonth(String monthYear) throws DatabaseException;
    List<Budget> findByUserAndMonth(int userId, String monthYear) throws DatabaseException;

    List<Budget> findAll() throws DatabaseException;
    List<Budget> findAllByUser(int userId) throws DatabaseException;
}
