package org.mifos.module.sms.repository;

import java.util.ArrayList;
import java.util.List;

import org.mifos.module.sms.domain.EventSourceDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventSourceDetailsRepository extends JpaRepository<EventSourceDetail, Long>,JpaSpecificationExecutor<EventSourceDetail> {

	public EventSourceDetail findByeventId(final Long eventId);
	public List<EventSourceDetail> findByEntityId(final String entityId);

    @Query(" from EventSourceDetail esd where esd.entityId=:entity_idparam and esd.entityMobileNo=:mobilenoparam and esd.processed=:processedparam")
    public ArrayList<EventSourceDetail> findByEntityIdandMobileNumberandProcessed(@Param("entity_idparam") String entity_id,
            @Param("mobilenoparam") String mobile_no,
            @Param("processedparam") Boolean processed);
}
