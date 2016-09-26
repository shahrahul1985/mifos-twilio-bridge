package org.mifos.module.sms.listener;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifos.module.sms.domain.ClientPayments;
import org.mifos.module.sms.domain.ClientPaymentsResponse;
import org.mifos.module.sms.domain.EventSource;
import org.mifos.module.sms.domain.EventSourceDetail;
import org.mifos.module.sms.domain.SMSBridgeConfig;
import org.mifos.module.sms.event.ClientPaymentsEvent;
import org.mifos.module.sms.exception.SMSGatewayException;
import org.mifos.module.sms.parser.JsonParser;
import org.mifos.module.sms.provider.RestAdapterProvider;
import org.mifos.module.sms.provider.SMSGateway;
import org.mifos.module.sms.provider.SMSGatewayProvider;
import org.mifos.module.sms.repository.EventSourceDetailsRepository;
import org.mifos.module.sms.repository.EventSourceRepository;
import org.mifos.module.sms.repository.SMSBridgeConfigRepository;
import org.mifos.module.sms.service.MifosClientPaymentsService;
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
public class ClientPaymentsEventListener implements
		ApplicationListener<ClientPaymentsEvent> {

	@Value("${message.template.clientPayments}")
	private String messageTemplate;

	private static final Logger logger = LoggerFactory
			.getLogger(ClientPaymentsEventListener.class);

	private final SMSBridgeConfigRepository smsBridgeConfigRepository;
	private final EventSourceRepository eventSourceRepository;
	private final EventSourceDetailsRepository eventSourceDetailsRepository;
	private final RestAdapterProvider restAdapterProvider;
	private final SMSGatewayProvider smsGatewayProvider;
	private final JsonParser jsonParser;

	@Autowired
	public ClientPaymentsEventListener(
			SMSBridgeConfigRepository smsBridgeConfigRepository,
			EventSourceRepository eventSourceRepository,
			EventSourceDetailsRepository eventSourceDetailsRepository,
			RestAdapterProvider restAdapterProvider,
			SMSGatewayProvider smsGatewayProvider, JsonParser jsonParser) {
		super();
		this.smsBridgeConfigRepository = smsBridgeConfigRepository;
		this.eventSourceRepository = eventSourceRepository;
		this.eventSourceDetailsRepository = eventSourceDetailsRepository;
		this.restAdapterProvider = restAdapterProvider;
		this.smsGatewayProvider = smsGatewayProvider;
		this.jsonParser = jsonParser;
	}

	List<String> receiptNumbers = new ArrayList<String>();

	@Transactional
	@Override
	public void onApplicationEvent(ClientPaymentsEvent clientPaymentsEvent) {

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		logger.info("Client payments event received, trying to process ...");

		final EventSource eventSource = this.eventSourceRepository
				.findOne(clientPaymentsEvent.getEventId());

		final SMSBridgeConfig smsBridgeConfig = this.smsBridgeConfigRepository
				.findByTenantId(eventSource.getTenantId());
		if (smsBridgeConfig == null) {
			logger.error("Unknown tenant " + eventSource.getTenantId() + "!");
			return;
		}

		ClientPaymentsResponse clientPaymentsResponse = this.jsonParser.parse(
				eventSource.getPayload(), ClientPaymentsResponse.class);

		final long clientId = clientPaymentsResponse.getClientId();

		String receiptNo = null;
		String report_name = "Client Payments";
		if (clientPaymentsResponse.getChanges() != null) {
			if (clientPaymentsResponse.getChanges().get("receiptNumber") != null) {
				receiptNo = clientPaymentsResponse.getChanges()
						.get("receiptNumber").toString();
			} else {
				logger.info("Receipt Number can not be null...");
			}
		}
		// receiptNumber is used here to check weather receitNo is already
		// present or not!!
		if (!receiptNumbers.contains(receiptNo)) {

			receiptNumbers.add(receiptNo);

			// this is to optimize the size of list as it reaches fixed number
			// of size!!
			if (receiptNumbers.size() == 10) {
				receiptNumbers.remove(0);
			}
			final RestAdapter restAdapter = this.restAdapterProvider
					.get(smsBridgeConfig);

			final String authToken = AuthorizationTokenBuilder.token(
					smsBridgeConfig.getMifosToken()).build();

			MifosClientPaymentsService clientSavingsPaymentsService = restAdapter
					.create(MifosClientPaymentsService.class);

			ClientPayments paymentsData = clientSavingsPaymentsService
					.findClientPayments(authToken,
							smsBridgeConfig.getTenantId(), report_name,
							receiptNo, clientId);
			paymentsData.getDataValues(paymentsData.getData());

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			List<EventSourceDetail> eventSourceDetailsList = eventSourceDetailsRepository
					.findByEntityIdandMobileNumberandProcessed(receiptNo,
							paymentsData.getMobileNo(), Boolean.TRUE);
			if (eventSourceDetailsList.size() == 0) {

				EventSourceDetail eventSourceDetails = new EventSourceDetail();

				try {

					eventSourceDetails.setEventId(eventSource.getId());
					eventSourceDetails.setEntity(eventSource.getEntity());
					eventSourceDetails.setAction(eventSource.getAction());
					eventSourceDetails.setPayload(eventSource.getPayload());
					eventSourceDetails.setTenantId(eventSource.getTenantId());
					eventSourceDetails.setEntityId(receiptNo);
					eventSourceDetails.setProcessed(Boolean.FALSE);
					eventSourceDetails.setEntityName(paymentsData
							.getClientName());
					eventSourceDetails.setEntity_Mobile_No(paymentsData
							.getMobileNo());
					eventSourceDetails.setEntitydescription("clientId:"
							+ clientId + " " + "receiptNo:"
							+ paymentsData.getReceiptNo());
					final Date now = new Date();
					eventSourceDetails.setCreatedOn(now);

					final String mobileNo = paymentsData.getMobileNo();
					if (mobileNo != null) {
						logger.info("Mobile number found, sending message!");

						final VelocityContext velocityContext = new VelocityContext();
				
						velocityContext.put("clientName",
								paymentsData.getClientName());
						velocityContext.put("firstName",
								paymentsData.getFirstName());
						velocityContext.put("totalAmount",
								paymentsData.getTotalAmount());
						velocityContext.put("branch",
								paymentsData.getOfficeName());
						velocityContext.put("billNumber",
								paymentsData.getReceiptNo());

						final StringWriter stringWriter = new StringWriter();
						Velocity.evaluate(velocityContext, stringWriter,
								"LoanApprovalToGuarantorsMessage",
								this.messageTemplate);

						final SMSGateway smsGateway = this.smsGatewayProvider
								.get(smsBridgeConfig.getSmsProvider());
						JSONArray response=smsGateway.sendMessage(smsBridgeConfig, mobileNo,
								stringWriter.toString());
						JSONObject result = response.getJSONObject(0);
		                if(result.getString("status").equals("success")||result.getString("status").equalsIgnoreCase("success"))
		                {
		                	eventSource.setProcessed(Boolean.TRUE);
							eventSourceDetails.setProcessed(Boolean.TRUE);
		                }		                
						logger.info("Message is: " + stringWriter);
					}

					logger.info("Client payments event processed!");
				} catch (RetrofitError ret) {
					if (ret.getResponse().getStatus() == 404) {
						logger.info("Client payments not found!");
					}
					eventSource.setProcessed(Boolean.FALSE);
					eventSource.setErrorMessage(ret.getMessage());
					eventSourceDetails.setProcessed(Boolean.FALSE);
					eventSourceDetails.setErrorMessage(ret.getMessage());
				} catch (SMSGatewayException sgex) {
					eventSource.setProcessed(Boolean.FALSE);
					eventSource.setErrorMessage(sgex.getMessage());
					eventSourceDetails.setProcessed(Boolean.FALSE);
					eventSourceDetails.setErrorMessage(sgex.getMessage());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				eventSource.setLastModifiedOn(new Date());
				eventSourceDetails.setLastModifiedOn(new Date());
				this.eventSourceRepository.save(eventSource);
				this.eventSourceDetailsRepository.save(eventSourceDetails);
			}
		}
	}
}
