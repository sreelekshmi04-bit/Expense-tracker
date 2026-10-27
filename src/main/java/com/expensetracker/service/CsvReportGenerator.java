package com.expensetracker.service;

import com.expensetracker.model.Expense;
import com.expensetracker.model.Transaction;
import java.util.List;

/**
 * Concrete implementation of ReportGenerator producing RFC-4180 compliant CSV exports.
 * Demonstrates Polymorphism: Interacts strictly with abstract Transaction references.
 */
public class CsvReportGenerator implements ReportGenerator {

    @Override
    public String generateReport(List<Transaction> transactions) {
        StringBuilder sb = new StringBuilder();

        // CSV Header
        sb.append("Transaction ID,Date,Type,Category / Source,Payment Method,Description,Amount (INR),Signed Effect (INR)\r\n");

        double totalIncome = 0.0;
        double totalExpense = 0.0;
        double netBalance = 0.0;

        if (transactions != null) {
            for (Transaction tx : transactions) {
                // Polymorphic calls:
                String type = tx.getTransactionType();
                String catOrSrc = tx.getCategoryOrSource();
                double signed = tx.getSignedAmount();
                double amount = tx.getAmount();
                String paymentMethod = (tx instanceof Expense) ? ((Expense) tx).getPaymentMethod() : "Bank Transfer";

                if ("INCOME".equalsIgnoreCase(type)) {
                    totalIncome += amount;
                } else {
                    totalExpense += amount;
                }
                netBalance += signed;

                sb.append(tx.getId()).append(",")
                  .append(tx.getTransactionDate()).append(",")
                  .append(type).append(",")
                  .append(escapeCsv(catOrSrc)).append(",")
                  .append(escapeCsv(paymentMethod)).append(",")
                  .append(escapeCsv(tx.getDescription())).append(",")
                  .append(String.format("%.2f", amount)).append(",")
                  .append(String.format("%.2f", signed)).append("\r\n");
            }
        }

        // Summary footer rows
        sb.append("\r\n");
        sb.append("--- SUMMARY ---,,,,,\r\n");
        sb.append(",,,,,Total Income (INR),").append(String.format("%.2f", totalIncome)).append("\r\n");
        sb.append(",,,,,Total Expense (INR),").append(String.format("%.2f", totalExpense)).append("\r\n");
        sb.append(",,,,,Net Ledger Balance (INR),").append(String.format("%.2f", netBalance)).append("\r\n");

        return sb.toString();
    }

    @Override
    public String getContentType() {
        return "text/csv; charset=UTF-8";
    }

    @Override
    public String getFileExtension() {
        return ".csv";
    }

    private String escapeCsv(String value) {
        if (value == null) return "\"\"";
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return "\"" + value + "\"";
    }
}
