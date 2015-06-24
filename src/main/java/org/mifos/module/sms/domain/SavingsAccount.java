package org.mifos.module.sms.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gson.JsonArray;

public class SavingsAccount {

	private long id;
	private String clientName;
	private List<JsonArray> transactions = new ArrayList<JsonArray>();
	private BigDecimal amount;
	
	public SavingsAccount() {
		super();
	}

	public long getSavingsId() {
		return id;
	}

	public void setSavingsId(long savingId) {
		this.id = savingId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getClientName() {
		return clientName;
	}
	
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public List<JsonArray> getTransactions() {
		return transactions;
	}

	public void setTransactions(
			List<JsonArray> transactions) {
		this.transactions = transactions;
	}
	
}
