package org.mifos.module.sms.service;

import org.mifos.module.sms.domain.Loan;

import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Path;

public interface MifosClientPaymentsService {

	@GET("/savingsaccounts/{id}?associations=transactions")
    public Loan findLoan(@Header("Authorization") String authorization,
                         @Header("X-Mifos-Platform-TenantId") String tenantIdentifier,
                         @Path("id") final long id);
}
