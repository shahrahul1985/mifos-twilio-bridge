package org.mifos.module.sms.domain;

import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class ClientPayments {

	private String clientName;
	private String officeName;
	private String receiptNo;
	private String mobileNo;
	private Double totalAmount;
	private ArrayList<JsonObject> data = new ArrayList<>();
	
	public ClientPayments() {
		super();
	}

	public ArrayList<JsonObject> getData() {
		return data;
	}

	public void setData(ArrayList<JsonObject> data) {
		this.data = data;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getOfficeName() {
		return officeName;
	}

	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}

	public String getReceiptNo() {
		return receiptNo;
	}

	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public Double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public void getDataValues(ArrayList<JsonObject> data) {
		
		for(int i=0; i<data.size(); i++) {
			JsonObject element = data.get(i);
			JsonArray rowData = element.get("row").getAsJsonArray();
			for(int j=0; j<rowData.size(); j++) {
				if (j == 0) {
					setReceiptNo(rowData.get(j).getAsString());
				}else if (j == 1) {
					setOfficeName(rowData.get(j).getAsString());
				}else if (j == 2) {
					setClientName(rowData.get(j).getAsString());
				}else if (j == 3) {
					Boolean mobileNum = rowData.get(j).isJsonNull();
					if (mobileNum) {
						System.out.println("Mobile Number can not be null...");
					}
					setMobileNo(rowData.get(j).getAsString());
				}else if (j == 6) {
					setTotalAmount(rowData.get(j).getAsDouble());
				}
			}
		}
	}
}
