package com.pao.laboratory11.exercise1;

public class Transaction {
    public final int id;
    public final double amount;
    public final String date;
    public final String country;
    public final String channel;
    public final String accountId;

    public Transaction(int id, double amount, String date, String country, String channel) {
        this(id, amount, date, country, channel, null);
    }

    public Transaction(int id, double amount, String date, String country, String channel, String accountId) {
        this.id = id;
        this.amount = amount;
        this.date = date;
        this.country = country;
        this.channel = channel;
        this.accountId = accountId;
    }
}
