package org.mifos.module.sms.domain;

public class IncomingSmsClientId {

    private String mobileNo;
    private String message;

    public IncomingSmsClientId() {
        super();
    }

    public String getMobileNo() {
        return this.mobileNo;
    }

    public IncomingSmsClientId(String mobileNo, String message) {
        super();
        this.mobileNo = mobileNo;
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

}
