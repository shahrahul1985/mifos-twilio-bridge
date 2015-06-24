package org.mifos.module.sms.domain;

public class SavingsAccountCloseResponse {

	private Long savingsId;
	private long officeId;
	private long clientId;
	private long resourceId;

	public SavingsAccountCloseResponse() {
	}

	public Long getSavingsId() {
		return savingsId;
	}

	public void setSavingsId(Long savingsId) {
		this.savingsId = savingsId;
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

	public long getResourceIs() {
		return resourceId;
	}

	public void setResourceIs(long resourceId) {
		this.resourceId = resourceId;
	}

}
