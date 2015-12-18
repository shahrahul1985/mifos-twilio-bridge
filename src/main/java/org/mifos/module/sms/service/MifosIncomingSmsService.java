package org.mifos.module.sms.service;


import java.util.ArrayList;
import java.util.Collection;

import org.json.JSONArray;
import org.mifos.module.sms.domain.IncomingSms;
import org.mifos.module.sms.domain.IncomingSmsLoanAndSavingAccoutData;
import org.mifos.module.sms.domain.LoanAccountData;
import org.mifos.module.sms.domain.MiniStatementDetails;
import org.mifos.module.sms.domain.SavingAccountTransactionData;
import org.mifos.module.sms.domain.SavingsAccountSummaryData;
import org.mifos.module.sms.domain.SmsEnabledBranch;

import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Path;
import retrofit.http.Query;


public interface MifosIncomingSmsService {
	
	 @GET("/search")
	    public  Collection<IncomingSms> findClientId(@Header("Authorization") String authorization,
	                         @Header("X-Mifos-Platform-TenantId") String tenantIdentifier,
	                         @Query("query") final String query,@Query("resource") final String resource);
	    
	    
	    
	    @GET("/clients/{clientId}/accounts")
	    public IncomingSmsLoanAndSavingAccoutData findDetails(@Header("Authorization") String authorization,
	            @Header("X-Mifos-Platform-TenantId") String tenantIdentifier, @Path("clientId") final Long clientId);
	            

	    @GET("/loans/{loanId}")    
	    public LoanAccountData findLoanTransactionsDetails(@Header("Authorization") String authorization,
	            @Header("X-Mifos-Platform-TenantId") String tenantIdentifier, @Path("loanId") final Long loanId,
	            @Query("associations") final String associations,@Query("exclude") final String exclude);
	         
	    
	    @GET("/savingsaccounts/{accountId}")    
	    public SavingAccountTransactionData findSavingsTransactionsDetails(@Header("Authorization") String authorization,
	            @Header("X-Mifos-Platform-TenantId") String tenantIdentifier, @Path("accountId") final Long accountId,
	            @Query("associations") final String associations);
	    
	    @GET("/clients/{clientId}/incomingSmsDetail")
	    public ArrayList<MiniStatementDetails> findMiniStatementDetails(@Header("Authorization") String authorization,
	            @Header("X-Mifos-Platform-TenantId") String tenantIdentifier, @Path("clientId") final Long clientId);
	   
	    @GET("/clients/{clientId}/sharesAccount")
	    public ArrayList<SavingsAccountSummaryData> findsharesBalance(@Header("Authorization") String authorization,
	            @Header("X-Mifos-Platform-TenantId") String tenantIdentifier, @Path("clientId") final Long clientId);
	               
	    
	    @GET("/datatables/{datatable}/{apptableId}")
	    public  ArrayList<SmsEnabledBranch> findSmsEnabledOffice(@Header("Authorization") String authorization,
	                         @Header("X-Mifos-Platform-TenantId") String tenantIdentifier,
	                         @Path("datatable") final String datatable,@Path("apptableId") final long apptableId);
	    
	           


}
