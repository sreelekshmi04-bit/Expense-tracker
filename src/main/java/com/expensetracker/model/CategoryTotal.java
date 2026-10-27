package com.expensetracker.model;

/**
 * Data Transfer Object representing category-wise expense breakdown.
 */
public class CategoryTotal {
    private String category;
    private double totalAmount;
    private int transactionCount;
    private double percentageOfTotal;

    public CategoryTotal() {
    }

    public CategoryTotal(String category, double totalAmount) {
        this(category, totalAmount, 1, 0.0);
    }

    public CategoryTotal(String category, double totalAmount, int transactionCount) {
        this(category, totalAmount, transactionCount, 0.0);
    }

    public CategoryTotal(String category, double totalAmount, int transactionCount, double percentageOfTotal) {
        this.category = category;
        this.totalAmount = Math.round(totalAmount * 100.0) / 100.0;
        this.transactionCount = transactionCount;
        this.percentageOfTotal = Math.round(percentageOfTotal * 10.0) / 10.0;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public double getTotalSpent() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(int transactionCount) {
        this.transactionCount = transactionCount;
    }

    public double getPercentageOfTotal() {
        return percentageOfTotal;
    }

    public void setPercentageOfTotal(double percentageOfTotal) {
        this.percentageOfTotal = percentageOfTotal;
    }
}
