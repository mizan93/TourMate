package com.pro.firebasepro;

public class Expense{
    private String expenseId;
    private String eventId;
    private String expenseTitle;
    private double expenseAmount;

    public Expense(String expenseId, String eventId, String expenseTitle, double expenseAmount) {
        this.expenseId = expenseId;
        this.eventId = eventId;
        this.expenseTitle = expenseTitle;
        this.expenseAmount = expenseAmount;
    }

    public Expense() {
    }

    public String getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(String expenseId) {
        this.expenseId = expenseId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getExpenseTitle() {
        return expenseTitle;
    }

    public void setExpenseTitle(String expenseTitle) {
        this.expenseTitle = expenseTitle;
    }

    public double getExpenseAmount() {
        return expenseAmount;
    }

    public void setExpenseAmount(double expenseAmount) {
        this.expenseAmount = expenseAmount;
    }
}
