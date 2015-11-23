/**
 * Copyright 2014 Markus Geiss
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mifos.module.sms.domain;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


public class Loan {
    private String accountNo;
    private long clientId;
    private String officeName;
    private ArrayList<JsonObject> guarantors;
    private List<Long> guarantorsId = new ArrayList<Long>();
    private List<Double> amount = new ArrayList<Double>();

	public Loan() {
        super();
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

	public long getClientId() {
		return clientId;
	}

	public void setClientId(long clientId) {
		this.clientId = clientId;
	}

	public String getOfficeName() {
		return officeName;
	}

	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}

	public ArrayList<JsonObject> getGuarantors() {
		return guarantors;
	}

	public void setGuarantors(ArrayList<JsonObject> guarantors) {
		this.guarantors = guarantors;
	}

	public List<Double> getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount.add(amount);
	}

	public List<Long> getGuarantorsId() {
		return guarantorsId;
	}

	public void setGuarantorsId(Long guarantorsId) {
		this.guarantorsId.add(guarantorsId);
	}

	/*public Double getLastTransactionAmount(ArrayList<JsonObject>transaciton){
        JsonObject lastTransaction=transaciton.get(0);
        final Double lastTransacionAmount=Double.parseDouble(lastTransaction.get("amount").toString());
        return lastTransacionAmount;
    }*/
	public void guarantorsData(ArrayList<JsonObject> guarantorsData) {
		
		for(int i= 0; i<guarantorsData.size(); i++) {
				
			JsonObject guarantor = guarantorsData.get(i);
			if(guarantor.get("entityId").getAsBigInteger() != null) {
				Long garantorsId = Long.parseLong(guarantor.get("entityId").getAsString());
				setGuarantorsId(garantorsId);
			}
			
			if(guarantor.get("guarantorFundingDetails").getAsJsonArray() != null) {
				JsonArray guarantorsCommitedAmount  = guarantor.get("guarantorFundingDetails").getAsJsonArray();
				
				for(int j=0; j<guarantorsCommitedAmount.size(); j++) {
					if (guarantorsCommitedAmount.get(j) != null ) {
						JsonObject commitedAmount = guarantorsCommitedAmount.get(j).getAsJsonObject();
						if(commitedAmount.get("amount") != null) {
							Double amount = Double.parseDouble(commitedAmount.get("amount").toString());
							setAmount(amount);
						}
					}
				}
			}
		}
		return;
	}
	
}