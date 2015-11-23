package org.mifos.module.sms.domain;

import java.util.Collection;


public class SavingAccountTransactionData {
    private final Collection<SavingsAccountTransactionData> transactions;

    public SavingAccountTransactionData(Collection<SavingsAccountTransactionData> transactions) {
        super();
        this.transactions = transactions;
    }

    
    public Collection<SavingsAccountTransactionData> getTransactions() {
        return this.transactions;
    }
    


}
