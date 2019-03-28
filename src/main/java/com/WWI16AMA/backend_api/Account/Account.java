package com.WWI16AMA.backend_api.Account;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Account {

    @Id
    @GenericGenerator(name = "5-digit-Id", strategy = "com.WWI16AMA.backend_api.Account.CustomGenerator.AccountIdGenerator")
    @GeneratedValue(generator = "5-digit-Id")
    private int id;

    @NotNull
    private double balance;

    @OneToMany(mappedBy = "sender")
    private List<Transaction> transactions = new ArrayList<>();
    @OneToMany(mappedBy = "receiver")
    private List<Transaction> transactionsInc = new ArrayList<>();

    public Account() {
        balance = 0;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void add2Balance(double amount) {
        this.balance = this.balance + amount;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }
}
