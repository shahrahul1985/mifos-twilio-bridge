package org.mifos.module.sms.domain;

public class SmsEnabledBranch {
	
	private final Long office_Id;
	
	private final String sms_enabled;

	
	
	public SmsEnabledBranch(Long office_Id, String sms_enabled) {
		super();
		this.office_Id = office_Id;
		this.sms_enabled = sms_enabled;
	}

	public Long getOffice_Id() {
		return office_Id;
	}

	public String getSms_enabled() {
		return sms_enabled;
	}
	
	
	

}
