package org.mifos.module.sms.domain;


public class IncomingSms {

    private final Long entityId;

    private final String entityMobileNo;
    
    private final String entityName;
    
    private final String parentName;
    
    private final Long parentId; 

    public IncomingSms(Long entityId, String entityMobileNo, String entityName, String parentName,Long parentId ) {
        super();
        this.entityId = entityId;
        this.entityMobileNo = entityMobileNo;
        this.entityName = entityName;
        this.parentName = parentName;
        this.parentId =parentId;
    }

    
    public Long getEntityId() {
        return this.entityId;
    }

    
    public String getEntityMobileNo() {
        return this.entityMobileNo;
    }

    
    public String getEntityName() {
        return this.entityName;
    }

    
    public String getParentName() {
        return this.parentName;
    }


	public Long getParentId() {
		return parentId;
	}

   
}
