package com.WWI16AMA.backend_api.Account;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Enumerated(EnumType.STRING)
    @NotNull
    private FeeType type;
    private final LocalDateTime timestamp;
    @NotNull
    private double amount;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private Account sender;
    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private Account receiver;

    public Transaction() {
        this.timestamp = LocalDateTime.now();
    }

    public Transaction(double amount, FeeType feeType, Account sender, Account receiver) {
        this.timestamp = LocalDateTime.now();
        this.amount = amount;
        this.type = feeType;
        this.sender = sender;
        this.receiver = receiver;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public FeeType getType() {
        return type;
    }

    public void setType(FeeType type) {
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public enum FeeType {
        MITGLIEDSBEITRAG,
        GEBÜHRFLUGZEUG,
        GUTSCHRIFTAMT,
        GUTSCHRIFTLEISTUNG,
        EINZAHLUNG
    }

}
