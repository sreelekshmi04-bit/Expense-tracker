package com.expensetracker.model;

/**
 * Data Transfer Object (DTO) summarizing high-level financial health.
 * Calculates net balance, monthly savings, and savings rate.
 */
public class SpendingSummary {
    private double totalIncome;
    private double totalExpense;
    private double currentBalance;
    private double monthlyIncome;
    private double monthlyExpense;
    private double monthlySavings;
    private double savingsRate; // percentage

    public SpendingSummary() {
    }

    public SpendingSummary(double totalIncome, double totalExpense, 
                           double monthlyIncome, double monthlyExpense) {
        this.totalIncome = Math.round(totalIncome * 100.0) / 100.0;
        this.totalExpense = Math.round(totalExpense * 100.0) / 100.0;
        this.currentBalance = Math.round((totalIncome - totalExpense) * 100.0) / 100.0;
        this.monthlyIncome = Math.round(monthlyIncome * 100.0) / 100.0;
        this.monthlyExpense = Math.round(monthlyExpense * 100.0) / 100.0;
        this.monthlySavings = Math.round((monthlyIncome - monthlyExpense) * 100.0) / 100.0;
        
        if (monthlyIncome > 0) {
            this.savingsRate = Math.round(((monthlySavings / monthlyIncome) * 100.0) * 10.0) / 10.0;
        } else {
            this.savingsRate = 0.0;
        }
    }

    // Getters and Setters

    public double getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(double totalIncome) {
        this.totalIncome = totalIncome;
    }

    public double getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(double totalExpense) {
        this.totalExpense = totalExpense;
    }

    public double getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(double currentBalance) {
        this.currentBalance = currentBalance;
    }

    public double getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(double monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public double getMonthlyExpense() {
        return monthlyExpense;
    }

    public void setMonthlyExpense(double monthlyExpense) {
        this.monthlyExpense = monthlyExpense;
    }

    public double getMonthlySavings() {
        return monthlySavings;
    }

    public void setMonthlySavings(double monthlySavings) {
        this.monthlySavings = monthlySavings;
    }

    public double getSavingsRate() {
        return savingsRate;
    }

    public void setSavingsRate(double savingsRate) {
        this.savingsRate = savingsRate;
    }
}
