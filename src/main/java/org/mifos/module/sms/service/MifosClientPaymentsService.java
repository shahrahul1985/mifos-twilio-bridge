package org.mifos.module.sms.service;

import org.mifos.module.sms.domain.ClientPayments;

import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Path;
import retrofit.http.Query;

public interface MifosClientPaymentsService {

	@GET("/runreports/{report_name}")
    public ClientPayments findClientPayments(@Header("Authorization") String authorization,
                         @Header("X-Mifos-Platform-TenantId") String tenantIdentifier,
                         @Path("report_name") final String report_name,
                         @Query("R_reciptNo") final String receiptNo,
                         @Query("R_clientId") final Long clientId);
}
