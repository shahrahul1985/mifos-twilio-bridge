package org.mifos.module.sms.domain;

import java.util.Collection;

public class LoanAccountData {

    private final Collection<LoanTransactionData> transactions;

    public LoanAccountData(Collection<LoanTransactionData> transactions) {
        super();
        this.transactions = transactions;
    }

    public Collection<LoanTransactionData> getTransactions() {
        return this.transactions;
    }

}
