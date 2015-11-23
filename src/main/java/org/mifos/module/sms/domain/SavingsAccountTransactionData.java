package org.mifos.module.sms.domain;

import java.math.BigDecimal;

public class SavingsAccountTransactionData {

    private final Long id;
    private final Long accountId;
    private final String accountNo;
    // private final LocalDate date;
    private final BigDecimal amount;
    private final BigDecimal runningBalance;
    private final boolean reversed;

    public SavingsAccountTransactionData(Long id, Long accountId, String accountNo, BigDecimal amount, BigDecimal runningBalance,
            boolean reversed) {
        super();
        this.id = id;
        this.accountId = accountId;
        this.accountNo = accountNo;
        this.amount = amount;
        this.runningBalance = runningBalance;
        this.reversed = reversed;
    }

    public Long getId() {
        return this.id;
    }

    public Long getAccountId() {
        return this.accountId;
    }

    public String getAccountNo() {
        return this.accountNo;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public BigDecimal getRunningBalance() {
        return this.runningBalance;
    }

    public boolean isReversed() {
        return this.reversed;
    }

}
