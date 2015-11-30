package org.mifos.module.sms.listener;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifos.module.sms.domain.Client;
import org.mifos.module.sms.domain.EventSource;
import org.mifos.module.sms.domain.SMSBridgeConfig;
import org.mifos.module.sms.domain.SavingsAccount;
import org.mifos.module.sms.domain.SavingsAccountCloseResponse;
import org.mifos.module.sms.event.SavingsAccountCloseEvent;
import org.mifos.module.sms.exception.SMSGatewayException;
import org.mifos.module.sms.parser.JsonParser;
import org.mifos.module.sms.provider.RestAdapterProvider;
import org.mifos.module.sms.provider.SMSGateway;
import org.mifos.module.sms.provider.SMSGatewayProvider;
import org.mifos.module.sms.repository.EventSourceRepository;
import org.mifos.module.sms.repository.SMSBridgeConfigRepository;
import org.mifos.module.sms.service.MifosClientService;
import org.mifos.module.sms.service.MifosSavingAccountService;
import org.mifos.module.sms.util.AuthorizationTokenBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonObject;

import retrofit.RestAdapter;
import retrofit.RetrofitError;

@Component
public class SavingAccountCloseEventListener implements ApplicationListener<SavingsAccountCloseEvent> {

    @Value("${message.template.savingsaccountclose}")
    private String messageTemplate;

    private static final Logger logger = LoggerFactory.getLogger(SavingsAccountCloseEvent.class);

    private final SMSBridgeConfigRepository smsBridgeConfigRepository;
    private final EventSourceRepository eventSourceRepository;
    private final RestAdapterProvider restAdapterProvider;
    private final SMSGatewayProvider smsGatewayProvider;
    private final JsonParser jsonParser;

    @Autowired
    public SavingAccountCloseEventListener(final SMSBridgeConfigRepository smsBridgeConfigRepository,
            final EventSourceRepository eventSourceRepository, final RestAdapterProvider restAdapterProvider,
            final SMSGatewayProvider smsGatewayProvider, final JsonParser jsonParser) {
        this.smsBridgeConfigRepository = smsBridgeConfigRepository;
        this.eventSourceRepository = eventSourceRepository;
        this.restAdapterProvider = restAdapterProvider;
        this.smsGatewayProvider = smsGatewayProvider;
        this.jsonParser = jsonParser;
    }

    @Transactional
    @Override
    public void onApplicationEvent(SavingsAccountCloseEvent savingsAccountCloseEvent) {
        logger.info("Savings account Close event received, trying to process ...");
        final EventSource eventSource = this.eventSourceRepository.findOne(savingsAccountCloseEvent.getEventId());
        final SMSBridgeConfig smsBridgeConfig = this.smsBridgeConfigRepository.findByTenantId(eventSource.getTenantId());
        if (smsBridgeConfig == null) {
            logger.error("Unknown tenant " + eventSource.getTenantId() + "!");
            return;
        }
        SavingsAccountCloseResponse savingsAccountCloseResponse = this.jsonParser.parse(eventSource.getPayload(),
                SavingsAccountCloseResponse.class);
        final Long clientId = savingsAccountCloseResponse.getClientId();
        final Long savingAccountId = savingsAccountCloseResponse.getSavingsId();
        final RestAdapter restAdapter = this.restAdapterProvider.get(smsBridgeConfig);
        try {
            final String authToken = AuthorizationTokenBuilder.token(smsBridgeConfig.getMifosToken()).build();
            final MifosSavingAccountService mifosSavingAccountService = restAdapter.create(MifosSavingAccountService.class);
            final SavingsAccount savingsAccount = mifosSavingAccountService.findSavingAccount(authToken, smsBridgeConfig.getTenantId(),
                    savingAccountId);
            final Double lastTransactionAmount=savingsAccount.getLastTransactionAmount(savingsAccount.getTransactions());
            final MifosClientService mifosClientService = restAdapter.create(MifosClientService.class);
            final Client client=mifosClientService.findClient(authToken, smsBridgeConfig.getTenantId(), clientId);
            final String mobileNo = client.getMobileNo();
            if (mobileNo != null) {
                
                logger.info("Mobile number found, sending message!");
                final VelocityContext velocityContext = new VelocityContext();
                velocityContext.put("name", client.getDisplayName());
                velocityContext.put("amount", lastTransactionAmount);
                velocityContext.put("branch", client.getOfficeName());
                final StringWriter stringWriter = new StringWriter();
                Velocity.evaluate(velocityContext, stringWriter, "SavingAccountClosureMessage", this.messageTemplate);

                final SMSGateway smsGateway = this.smsGatewayProvider.get(smsBridgeConfig.getSmsProvider());
                JSONArray response=  smsGateway.sendMessage(smsBridgeConfig, mobileNo, stringWriter.toString());
                JSONObject result = response.getJSONObject(0);
                if(result.getString("status").equals("success")||result.getString("status").equalsIgnoreCase("success"))
                {
                	eventSource.setProcessed(Boolean.TRUE);
                }
            }
            logger.info("savings Account Closure event processed!");
        } catch (RetrofitError rer) {
            if (rer.getResponse().getStatus() == 404) {
                logger.info("Savings Account not found!");
            }
            eventSource.setProcessed(Boolean.FALSE);
            eventSource.setErrorMessage(rer.getMessage());
        } catch (SMSGatewayException sgex) {
            eventSource.setProcessed(Boolean.FALSE);
            eventSource.setErrorMessage(sgex.getMessage());
        } catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        eventSource.setLastModifiedOn(new Date());
        this.eventSourceRepository.save(eventSource);
    }
}
