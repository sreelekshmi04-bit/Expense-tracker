package com.expensetracker.factory;

import com.expensetracker.exception.ValidationException;
import com.expensetracker.model.Expense;
import com.expensetracker.model.Income;
import com.expensetracker.model.Transaction;
import java.sql.Timestamp;
import java.time.LocalDate;

/**
 * Factory Pattern Implementation for Transactions.
 * 
 * OOP Concepts Demonstrated:
 * 1. Factory Pattern: Decouples object instantiation from business logic.
 * 2. Polymorphic Return: Returns an abstract Transaction reference pointing
 *    to either an Income or Expense instance based on runtime parameters.
 * 3. Method Overloading (Compile-time Polymorphism): Multiple overloaded
 *    factory methods accommodate different parameter sets.
 */
public class TransactionFactory {

    /**
     * Creates a new unpersisted Transaction instance (single-user convenience).
     */
    public static Transaction createTransaction(String type, double amount, 
                                                LocalDate date, String description, 
                                                String categoryOrSource) throws ValidationException {
        return createTransaction(type, 1, amount, date, description, categoryOrSource, "Cash");
    }

    /**
     * Creates a new unpersisted Transaction instance with payment method.
     */
    public static Transaction createTransaction(String type, double amount, 
                                                LocalDate date, String description, 
                                                String categoryOrSource, String paymentMethod) 
                                                throws ValidationException {
        return createTransaction(type, 1, amount, date, description, categoryOrSource, paymentMethod);
    }

    /**
     * Creates a new unpersisted Transaction instance with userId.
     */
    public static Transaction createTransaction(String type, int userId, double amount, 
                                                LocalDate date, String description, 
                                                String categoryOrSource) throws ValidationException {
        return createTransaction(type, userId, amount, date, description, categoryOrSource, "Cash");
    }

    /**
     * Creates a new unpersisted Transaction instance with userId and paymentMethod.
     */
    public static Transaction createTransaction(String type, int userId, double amount, 
                                                LocalDate date, String description, 
                                                String categoryOrSource, String paymentMethod) 
                                                throws ValidationException {
        if (type == null) {
            throw new ValidationException("Transaction type cannot be null.");
        }

        String normalizedType = type.trim().toUpperCase();
        switch (normalizedType) {
            case "INCOME":
                return new Income(userId, amount, date, description, categoryOrSource);
            case "EXPENSE":
                return new Expense(userId, amount, date, description, categoryOrSource, paymentMethod);
            default:
                throw new ValidationException("Unknown transaction type: '" + type + "'. Must be 'INCOME' or 'EXPENSE'.");
        }
    }

    /**
     * Creates a populated Transaction instance (loaded from Database).
     */
    public static Transaction createTransaction(int id, int userId, String type, double amount, 
                                                LocalDate date, String description, 
                                                String categoryOrSource, Timestamp createdAt) 
                                                throws ValidationException {
        return createTransaction(id, userId, type, amount, date, description, categoryOrSource, "Cash", createdAt);
    }

    /**
     * Creates a populated Transaction instance with payment method (loaded from Database).
     */
    public static Transaction createTransaction(int id, int userId, String type, double amount, 
                                                LocalDate date, String description, 
                                                String categoryOrSource, String paymentMethod,
                                                Timestamp createdAt) throws ValidationException {
        if (type == null) {
            throw new ValidationException("Transaction type cannot be null.");
        }

        String normalizedType = type.trim().toUpperCase();
        switch (normalizedType) {
            case "INCOME":
                return new Income(id, userId, amount, date, description, categoryOrSource, createdAt);
            case "EXPENSE":
                return new Expense(id, userId, amount, date, description, categoryOrSource, paymentMethod, createdAt);
            default:
                throw new ValidationException("Unknown transaction type: '" + type + "'. Must be 'INCOME' or 'EXPENSE'.");
        }
    }
}
