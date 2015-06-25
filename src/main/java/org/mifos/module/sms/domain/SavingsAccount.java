package org.mifos.module.sms.domain;

import java.util.ArrayList;
import com.google.gson.JsonObject;
import java.math.BigDecimal;
import java.util.List;

import com.google.gson.JsonArray;

public class SavingsAccount {

    private long id;
    private String clientName;
    private BigDecimal amount;
    private ArrayList<JsonObject> transactions;
    public SavingsAccount() {
        super();
    }

    public Double getLastTransactionAmount(ArrayList<JsonObject> objectTransaciton) {
        JsonObject lastTransaction = objectTransaciton.get(0);
        final Double lastTransacionAmount = Double.parseDouble(lastTransaction.get("amount").toString());
        return lastTransacionAmount;
    }

    public long getSavingsId() {
        return id;
    }

    public void setSavingsId(long savingId) {
        this.id = savingId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    
    public ArrayList<JsonObject> getTransactions() {
        return this.transactions;
    }

    
    public void setTransactions(ArrayList<JsonObject> transactions) {
        this.transactions = transactions;
    }
}
