package org.mifos.module.sms.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "officedetails")
public class SmsEnabledBranch {
	
	@Id
	@GeneratedValue
    private Long id;
	
	@Column(name = "office_id")
	private Long officeId;
	
    @Column(name = "sms_enabled")
    private boolean sms_enabled;

	public Long getOffice_id() {
		return officeId;
	}

	public void setOffice_id(Long officeId) {
		this.officeId = officeId;
	}

	public boolean isSms_enabled() {
		return sms_enabled;
	}

	public void setSms_enabled(boolean sms_enabled) {
		this.sms_enabled = sms_enabled;
	}
    
    
    
    
}
