package org.mifos.module.sms.domain;

import java.util.Collection;

public class IncomingSmsLoanAndSavingAccoutData {

    private final Collection<LoanAccountSummaryData> loanAccounts;
    private final Collection<SavingsAccountSummaryData>savingsAccounts;

    public IncomingSmsLoanAndSavingAccoutData(Collection<LoanAccountSummaryData> loanAccounts,
            Collection<SavingsAccountSummaryData>savingsAccounts) {
        super();
        this.loanAccounts = loanAccounts;
        this.savingsAccounts=savingsAccounts;
    }

    public Collection<LoanAccountSummaryData> getLoanAccounts() {
        return this.loanAccounts;
    }

    
    public Collection<SavingsAccountSummaryData> getSavingsAccounts() {
        return this.savingsAccounts;
    }
    

}
