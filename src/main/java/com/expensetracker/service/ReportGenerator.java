package com.expensetracker.service;

import com.expensetracker.model.Transaction;
import java.util.List;

/**
 * ReportGenerator interface.
 * 
 * OOP Concepts Demonstrated:
 * 1. Abstraction: Hides the formatting engine (CSV, Text, JSON) behind a unified contract.
 * 2. Polymorphic Strategy: Callers can substitute any ReportGenerator implementation at runtime.
 */
public interface ReportGenerator {

    /**
     * Generates a formatted report for a list of transactions.
     * @param transactions collection of Income and Expense transactions
     * @return Formatted report as String
     */
    String generateReport(List<Transaction> transactions);

    /**
     * Returns the MIME content-type of the report (e.g. text/csv, text/plain).
     */
    String getContentType();

    /**
     * Suggested file extension (e.g. .csv, .txt).
     */
    String getFileExtension();
}
