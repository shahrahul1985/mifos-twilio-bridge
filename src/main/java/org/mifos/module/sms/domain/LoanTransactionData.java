package org.mifos.module.sms.domain;

import java.math.BigDecimal;
public class LoanTransactionData {

    private final Long id;
    private final Long officeId;
    private final String officeName;

   // private final LocaleType date;

    private final BigDecimal amount;
    private final BigDecimal principalPortion;
    private final BigDecimal interestPortion;
    private final BigDecimal feeChargesPortion;
    private final BigDecimal penaltyChargesPortion;
    private final BigDecimal overpaymentPortion;
    private final BigDecimal unrecognizedIncomePortion;
    private final String externalId;

    private final BigDecimal fixedEmiAmount;
    private final BigDecimal outstandingLoanBalance;

    private final boolean manuallyReversed;

    public LoanTransactionData(Long id, Long officeId, String officeName,  BigDecimal amount, BigDecimal principalPortion,
            BigDecimal interestPortion, BigDecimal feeChargesPortion, BigDecimal penaltyChargesPortion, BigDecimal overpaymentPortion,
            BigDecimal unrecognizedIncomePortion, String externalId, BigDecimal fixedEmiAmount, BigDecimal outstandingLoanBalance,
            boolean manuallyReversed) {
        super();
        this.id = id;
        this.officeId = officeId;
        this.officeName = officeName;        
        this.amount = amount;
        this.principalPortion = principalPortion;
        this.interestPortion = interestPortion;
        this.feeChargesPortion = feeChargesPortion;
        this.penaltyChargesPortion = penaltyChargesPortion;
        this.overpaymentPortion = overpaymentPortion;
        this.unrecognizedIncomePortion = unrecognizedIncomePortion;
        this.externalId = externalId;
        this.fixedEmiAmount = fixedEmiAmount;
        this.outstandingLoanBalance = outstandingLoanBalance;
        this.manuallyReversed = manuallyReversed;
    }

    public Long getId() {
        return this.id;
    }

    
    public BigDecimal getAmount() {
        return this.amount;
    }

    public BigDecimal getOutstandingLoanBalance() {
        return this.outstandingLoanBalance;
    }

}
