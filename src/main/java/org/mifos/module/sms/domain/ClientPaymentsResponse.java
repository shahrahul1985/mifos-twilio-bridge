package org.mifos.module.sms.domain;

import java.util.Map;

public class ClientPaymentsResponse {

	private long officeId;
    private long clientId;
    private Long resourceId;
    private Long savingsId;
    private Long loanId;
    private String receiptNo;
    private String reportName;
    private final Map<String, Object> changes;
    
	public ClientPaymentsResponse(Map<String, Object> changesOnly) {
		super();
		this.changes = changesOnly;
	}

	public long getOfficeId() {
		return officeId;
	}

	public void setOfficeId(long officeId) {
		this.officeId = officeId;
	}

	public long getClientId() {
		return clientId;
	}

	public void setClientId(long clientId) {
		this.clientId = clientId;
	}

	public Long getResourceId() {
		return resourceId;
	}

	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}

	public Long getSavingsId() {
		return savingsId;
	}

	public void setSavingsId(Long savingsId) {
		this.savingsId = savingsId;
	}

	public Long getLoanId() {
		return loanId;
	}

	public void setLoanId(Long loanId) {
		this.loanId = loanId;
	}

	public Map<String, Object> getChanges() {
		return changes;
	}

	public String getReceiptNo() {
		return receiptNo;
	}

	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}
    
}
