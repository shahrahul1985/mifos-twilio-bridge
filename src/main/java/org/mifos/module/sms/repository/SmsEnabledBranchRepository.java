package org.mifos.module.sms.repository;



import org.mifos.module.sms.domain.SmsEnabledBranch;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SmsEnabledBranchRepository extends CrudRepository<SmsEnabledBranch, Long> {

	public SmsEnabledBranch findSmsEnabledDetailsByOfficeId(final Long officeId);
	
}
