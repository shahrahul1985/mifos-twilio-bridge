package org.mifos.module.sms.domain;

import java.util.ArrayList;

import com.google.gson.JsonObject;

public class SavingsAccount {

    private ArrayList<JsonObject> transactions;

    public SavingsAccount() {
        super();
    }

    
    
    public ArrayList<JsonObject> getTransactions() {
        return this.transactions;
    }


    
    public void setTransactions(ArrayList<JsonObject> transactions) {
        this.transactions = transactions;
    }


    public Double getLastTransactionAmount(ArrayList<JsonObject>transaciton){
        JsonObject lastTransaction=transaciton.get(0);
        final Double lastTransacionAmount=Double.parseDouble(lastTransaction.get("amount").toString());
        return lastTransacionAmount;
    }
}
