package com.expensetracker.model;

/**
 * Data Transfer Object representing the live spending progress
 * against an envelope budget for a category.
 */
public class BudgetProgress {
    private int budgetId;
    private String category;
    private double amountLimit;
    private double spentAmount;
    private double remainingAmount;
    private double percentageUsed;
    private String status; // "SAFE", "WARNING", "EXCEEDED"
    private String monthYear;

    public BudgetProgress() {
    }

    public BudgetProgress(int budgetId, String category, double amountLimit, 
                          double spentAmount, String monthYear) {
        this.budgetId = budgetId;
        this.category = category;
        this.amountLimit = Math.round(amountLimit * 100.0) / 100.0;
        this.spentAmount = Math.round(spentAmount * 100.0) / 100.0;
        this.remainingAmount = Math.round((amountLimit - spentAmount) * 100.0) / 100.0;
        this.monthYear = monthYear;

        if (amountLimit > 0) {
            this.percentageUsed = Math.round(((spentAmount / amountLimit) * 100.0) * 10.0) / 10.0;
        } else {
            this.percentageUsed = 0.0;
        }

        if (this.percentageUsed > 100.0) {
            this.status = "EXCEEDED";
        } else if (this.percentageUsed >= 80.0) {
            this.status = "WARNING";
        } else {
            this.status = "SAFE";
        }
    }

    // Getters and Setters

    public int getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(int budgetId) {
        this.budgetId = budgetId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getAmountLimit() {
        return amountLimit;
    }

    public void setAmountLimit(double amountLimit) {
        this.amountLimit = amountLimit;
    }

    public double getSpentAmount() {
        return spentAmount;
    }

    public void setSpentAmount(double spentAmount) {
        this.spentAmount = spentAmount;
    }

    public double getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(double remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public double getPercentageUsed() {
        return percentageUsed;
    }

    public void setPercentageUsed(double percentageUsed) {
        this.percentageUsed = percentageUsed;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMonthYear() {
        return monthYear;
    }

    public void setMonthYear(String monthYear) {
        this.monthYear = monthYear;
    }
}
