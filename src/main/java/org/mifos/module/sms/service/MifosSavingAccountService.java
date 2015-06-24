package org.mifos.module.sms.service;

import org.mifos.module.sms.domain.SavingsAccount;

import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Path;


public interface MifosSavingAccountService {

    @GET("/savingsaccounts/{id}?associations=all")
    public SavingsAccount findSavingAccount(@Header("Authorization") String authorization,
                             @Header("X-Mifos-Platform-TenantId") String tenantIdentifier,
                             @Path("id") final long id);
}
