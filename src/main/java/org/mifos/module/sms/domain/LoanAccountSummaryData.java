package org.mifos.module.sms.domain;

import java.math.BigDecimal;

public class LoanAccountSummaryData {

    private final Long id;
    private final String accountNo;
    private final String externalId;
    private final Long productId;
    private final String productName;
    private final String shortProductName;
    // private final LoanStatusEnumData status;
    // private final EnumOptionData loanType;
    private final Integer loanCycle;
    // private final LoanApplicationTimelineData timeline;
    private final Boolean inArrears;
    private final BigDecimal originalLoan;
    private final BigDecimal loanBalance;
    private final BigDecimal amountPaid;

    public LoanAccountSummaryData(final Long id, final String accountNo, final String externalId, final Long productId,
            final String loanProductName, final String shortLoanProductName, final Integer loanCycle, final Boolean inArrears,
            final BigDecimal originalLoan, final BigDecimal loanBalance, final BigDecimal amountPaid) {
        this.id = id;
        this.accountNo = accountNo;
        this.externalId = externalId;
        this.productId = productId;
        this.productName = loanProductName;
        this.shortProductName = shortLoanProductName;
        this.loanCycle = loanCycle;
        this.inArrears = inArrears;
        this.loanBalance = loanBalance;
        this.originalLoan = originalLoan;
        this.amountPaid = amountPaid;

    }

    public Long getId() {
        return this.id;
    }

    public BigDecimal getLoanBalance() {
        return this.loanBalance;
    }

}
