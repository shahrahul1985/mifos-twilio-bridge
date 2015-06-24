package org.mifos.module.sms.listener;

import java.io.StringWriter;
import java.util.Date;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.mifos.module.sms.domain.Client;
import org.mifos.module.sms.domain.EventSource;
import org.mifos.module.sms.domain.LoanDisbursementResponse;
import org.mifos.module.sms.domain.SMSBridgeConfig;
import org.mifos.module.sms.event.LoanDisbursementEvent;
import org.mifos.module.sms.exception.SMSGatewayException;
import org.mifos.module.sms.parser.JsonParser;
import org.mifos.module.sms.provider.RestAdapterProvider;
import org.mifos.module.sms.provider.SMSGateway;
import org.mifos.module.sms.provider.SMSGatewayProvider;
import org.mifos.module.sms.repository.EventSourceRepository;
import org.mifos.module.sms.repository.SMSBridgeConfigRepository;
import org.mifos.module.sms.service.MifosClientService;
import org.mifos.module.sms.util.AuthorizationTokenBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import retrofit.RestAdapter;
import retrofit.RetrofitError;

@Component
public class LoanDisbursementEventListener implements ApplicationListener<LoanDisbursementEvent> {

	@Value("${message.template.loandisbursed}")
	private String messageTemplate;
	
	private static final Logger logger = LoggerFactory.getLogger(LoanDisbursementEvent.class);
	
	private final SMSBridgeConfigRepository smsBridgeConfigRepository;
	private final EventSourceRepository eventSourceRepository;
	private final RestAdapterProvider restAdapterProvider;
	private final SMSGatewayProvider smsGatewayProvider;
	private final JsonParser jsonParser;
	
	@Autowired
	public LoanDisbursementEventListener(SMSBridgeConfigRepository smsBridgeConfigRepository,
											EventSourceRepository eventSourceRepository,
											RestAdapterProvider restAdapterProvider,
											SMSGatewayProvider smsGatewayProvider, JsonParser jsonParser) {
		super();
		this.smsBridgeConfigRepository = smsBridgeConfigRepository;
		this.eventSourceRepository = eventSourceRepository;
		this.restAdapterProvider = restAdapterProvider;
		this.smsGatewayProvider = smsGatewayProvider;
		this.jsonParser = jsonParser;
	}

	@Transactional
	@Override
	public void onApplicationEvent(LoanDisbursementEvent loanDisbursementEvent) {
		
		logger.info("Loan disbursement event received, trying to process ...");
		
		final EventSource eventSource = this.eventSourceRepository.findOne(loanDisbursementEvent.getEventId());
		
		final SMSBridgeConfig smsBridgeConfig = this.smsBridgeConfigRepository.findByTenantId(eventSource.getTenantId());
		if (smsBridgeConfig == null) {
			logger.error("Unknown tenant " + eventSource.getTenantId() + "!");
			return;
		}
		
		LoanDisbursementResponse loanDisbursementResponse = this.jsonParser.parse(eventSource.getPayload(), LoanDisbursementResponse.class);
		
		final long clientId = loanDisbursementResponse.getClientId();
		//final long loanId = loanDisbursementResponse.getLoanId();
		
		final RestAdapter restAdapter = this.restAdapterProvider.get(smsBridgeConfig);
		
		try{
			final String authToken = AuthorizationTokenBuilder.token(smsBridgeConfig.getMifosToken()).build();
			/*final MifosLoanService loanService = restAdapter.create(MifosLoanService.class);
			final Loan loan = loanService.findLoan(authToken, smsBridgeConfig.getTenantId(), loanId);*/
			
			final MifosClientService clientService = restAdapter.create(MifosClientService.class);
			final Client client = clientService.findClient(authToken, smsBridgeConfig.getTenantId(), clientId);
			
			final String mobileNo = client.getMobileNo();
			if (mobileNo != null) {
				logger.info("Mobile number found, sending message!");
				
				final VelocityContext velocityContext = new VelocityContext();
				velocityContext.put("clientName", client.getDisplayName());
				velocityContext.put("branch", client.getOfficeName());
				
				final StringWriter stringWriter = new StringWriter();
				Velocity.evaluate(velocityContext, stringWriter, "LoanDisbursementMessage", this.messageTemplate);
				
				final SMSGateway smsGateway = this.smsGatewayProvider.get(smsBridgeConfig.getSmsProvider());
				smsGateway.sendMessage(smsBridgeConfig, mobileNo, stringWriter.toString());
			}
			eventSource.setProcessed(Boolean.TRUE);
			logger.info("Loan disbursement event processed!");
		} catch(RetrofitError ret) {
			if (ret.getResponse().getStatus() == 404) {
				logger.info("Loan not found!");
			}
			eventSource.setProcessed(Boolean.FALSE);
			eventSource.setErrorMessage(ret.getMessage());
		} catch (SMSGatewayException sgex) {
			eventSource.setProcessed(Boolean.FALSE);
			eventSource.setErrorMessage(sgex.getMessage());
		}
		eventSource.setLastModifiedOn(new Date());
		this.eventSourceRepository.save(eventSource);
	}
}
