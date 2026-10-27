package com.expensetracker.controller;

import com.expensetracker.model.Transaction;
import com.expensetracker.model.User;
import com.expensetracker.service.*;
import com.expensetracker.util.JsonUtil;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller handling financial reports, filtering, and CSV export.
 * Demonstrates Strategy Pattern: swaps ReportGenerator implementations based on requested format.
 */
public class ReportController {

    private final TransactionService transactionService;
    private final ReportGenerator csvReportGenerator;
    private final ReportGenerator textReportGenerator;

    public ReportController() {
        this(new TransactionServiceImpl(), new CsvReportGenerator(), new SummaryReportGenerator());
    }

    public ReportController(TransactionService transactionService, 
                            ReportGenerator csvReportGenerator, 
                            ReportGenerator textReportGenerator) {
        this.transactionService = transactionService;
        this.csvReportGenerator = csvReportGenerator;
        this.textReportGenerator = textReportGenerator;
    }

    /**
     * Filters transactions and returns detailed summary JSON.
     */
    public String handleGetFilteredReport(Map<String, String> params) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Transaction> transactions = getFilteredList(params);

            double totalIncome = 0.0;
            double totalExpense = 0.0;
            for (Transaction tx : transactions) {
                if ("INCOME".equalsIgnoreCase(tx.getTransactionType())) {
                    totalIncome += tx.getAmount();
                } else {
                    totalExpense += tx.getAmount();
                }
            }
            double netSavings = totalIncome - totalExpense;

            response.put("success", true);
            response.put("transactions", transactions);
            response.put("count", transactions.size());
            response.put("totalIncome", Math.round(totalIncome * 100.0) / 100.0);
            response.put("totalExpense", Math.round(totalExpense * 100.0) / 100.0);
            response.put("netSavings", Math.round(netSavings * 100.0) / 100.0);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error compiling report: " + e.getMessage());
        }
        return JsonUtil.toJson(response);
    }

    public String handleGetFilteredReport(User user, Map<String, String> params) {
        return handleGetFilteredReport(params);
    }

    /**
     * Generates CSV or Plain Text export content.
     */
    public ExportResult handleExport(Map<String, String> params, String format) {
        try {
            List<Transaction> transactions = getFilteredList(params);
            ReportGenerator generator = "txt".equalsIgnoreCase(format) ? textReportGenerator : csvReportGenerator;
            String content = generator.generateReport(transactions);
            return new ExportResult(content, generator.getContentType(), "expense_ledger_export" + generator.getFileExtension());
        } catch (Exception e) {
            return new ExportResult("Error exporting: " + e.getMessage(), "text/plain", "error.txt");
        }
    }

    public ExportResult handleExport(User user, Map<String, String> params, String format) {
        return handleExport(params, format);
    }

    private List<Transaction> getFilteredList(Map<String, String> params) throws Exception {
        String type = params.get("type");
        String category = params.get("category");
        String month = params.get("month");
        String startStr = params.get("startDate");
        String endStr = params.get("endDate");

        LocalDate startDate = null;
        LocalDate endDate = null;

        if (month != null && !month.isBlank() && !month.equalsIgnoreCase("ALL")) {
            YearMonth ym = YearMonth.parse(month.trim());
            startDate = ym.atDay(1);
            endDate = ym.atEndOfMonth();
        } else {
            if (startStr != null && !startStr.isBlank()) startDate = LocalDate.parse(startStr.trim());
            if (endStr != null && !endStr.isBlank()) endDate = LocalDate.parse(endStr.trim());
        }

        return transactionService.getTransactions(type, category, startDate, endDate);
    }

    public static class ExportResult {
        public final String content;
        public final String contentType;
        public final String filename;

        public ExportResult(String content, String contentType, String filename) {
            this.content = content;
            this.contentType = contentType;
            this.filename = filename;
        }
    }
}
