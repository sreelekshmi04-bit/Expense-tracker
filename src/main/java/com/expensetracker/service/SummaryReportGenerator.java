package com.expensetracker.service;

import com.expensetracker.model.Transaction;
import java.util.List;

/**
 * Concrete implementation of ReportGenerator producing an editorial plain-text ledger summary.
 */
public class SummaryReportGenerator implements ReportGenerator {

    @Override
    public String generateReport(List<Transaction> transactions) {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================================================\n");
        sb.append("                   EXPENSE TRACKER - FINANCIAL LEDGER                   \n");
        sb.append("========================================================================\n");
        sb.append(String.format("%-12s | %-8s | %-16s | %-20s | %10s\n",
            "DATE", "TYPE", "CATEGORY/SOURCE", "DESCRIPTION", "AMOUNT (₹)"));
        sb.append("------------------------------------------------------------------------\n");

        double totalIn = 0.0;
        double totalOut = 0.0;

        if (transactions != null) {
            for (Transaction tx : transactions) {
                String type = tx.getTransactionType();
                String desc = tx.getDescription();
                if (desc.length() > 20) desc = desc.substring(0, 17) + "...";

                sb.append(String.format("%-12s | %-8s | %-16s | %-20s | %10.2f\n",
                    tx.getTransactionDate(),
                    type,
                    tx.getCategoryOrSource(),
                    desc,
                    tx.getSignedAmount()
                ));

                if ("INCOME".equalsIgnoreCase(type)) {
                    totalIn += tx.getAmount();
                } else {
                    totalOut += tx.getAmount();
                }
            }
        }

        sb.append("========================================================================\n");
        sb.append(String.format("TOTAL INCOME   : ₹%12.2f\n", totalIn));
        sb.append(String.format("TOTAL EXPENSES : ₹%12.2f\n", totalOut));
        sb.append(String.format("NET BALANCE    : ₹%12.2f\n", (totalIn - totalOut)));
        sb.append("========================================================================\n");

        return sb.toString();
    }

    @Override
    public String getContentType() {
        return "text/plain; charset=UTF-8";
    }

    @Override
    public String getFileExtension() {
        return ".txt";
    }
}
