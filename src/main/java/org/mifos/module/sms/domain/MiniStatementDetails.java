package org.mifos.module.sms.domain;

import java.math.BigDecimal;

public class MiniStatementDetails {

    private final BigDecimal amount;
    private final String TransactionDate;
    private final String receiptNumber;
    private final String firstname;
    private final String branch;

    public MiniStatementDetails(BigDecimal amount, String transactionDate, String receiptNumber, String firstname, String branch) {
        super();
        this.amount = amount;
        this.TransactionDate = transactionDate;
        this.receiptNumber = receiptNumber;
        this.firstname = firstname;
        this.branch = branch;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public String getTransactionDate() {
        return this.TransactionDate;
    }

    public String getReceiptNumber() {
        return this.receiptNumber;
    }

    public String getFirstname() {
        return this.firstname;
    }

    public String getBranch() {
        return this.branch;
    }

}
