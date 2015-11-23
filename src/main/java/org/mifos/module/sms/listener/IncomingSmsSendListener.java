package org.mifos.module.sms.listener;

import java.io.StringWriter;
import java.util.ArrayList;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.mifos.module.sms.domain.EventSource;
import org.mifos.module.sms.domain.IncomingSms;
import org.mifos.module.sms.domain.IncomingSmsClientId;
import org.mifos.module.sms.domain.IncomingSmsLoanAndSavingAccoutData;
import org.mifos.module.sms.domain.LoanAccountSummaryData;
import org.mifos.module.sms.domain.MiniStatementDetails;
import org.mifos.module.sms.domain.SMSBridgeConfig;
import org.mifos.module.sms.domain.SavingsAccountSummaryData;
import org.mifos.module.sms.event.IncomingSmsEvent;
import org.mifos.module.sms.exception.SMSGatewayException;
import org.mifos.module.sms.parser.JsonParser;
import org.mifos.module.sms.provider.RestAdapterProvider;
import org.mifos.module.sms.provider.SMSGateway;
import org.mifos.module.sms.provider.SMSGatewayProvider;
import org.mifos.module.sms.repository.EventSourceRepository;
import org.mifos.module.sms.repository.SMSBridgeConfigRepository;
import org.mifos.module.sms.service.MifosIncomingSmsService;
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
public class IncomingSmsSendListener implements ApplicationListener<IncomingSmsEvent> {

    @Value("${message.template.unregisteredmobilenumbers}")
    private String unregisteredmobilenumbers;
    
    @Value("${message.template.loanAndSavingsBalance}")
    private String loanAndSavingBalance;
    
    @Value("${message.template.invalidText}")
    private String invalidText;
    
    @Value("${message.template.miniStatement}")
    private String miniStatement;
    
    @Value("${message.template.balance}")
    private String balance;
    
    @Value("${message.template.loanAndSavingsTransaction}")
    private String loanAndSavingsTransaction;

    private static final Logger logger = LoggerFactory.getLogger(IncomingSmsSendListener.class);

    private final SMSBridgeConfigRepository smsBridgeConfigRepository;
    private final EventSourceRepository eventSourceRepository;
    private final RestAdapterProvider restAdapterProvider;
    private final SMSGatewayProvider smsGatewayProvider;
    private final JsonParser jsonParser;

    @Autowired
    public IncomingSmsSendListener(final SMSBridgeConfigRepository smsBridgeConfigRepository,
            final EventSourceRepository eventSourceRepository, final RestAdapterProvider restAdapterProvider,
            final SMSGatewayProvider smsGatewayProvider, final JsonParser jsonParser) {
        super();
        this.smsBridgeConfigRepository = smsBridgeConfigRepository;
        this.eventSourceRepository = eventSourceRepository;
        this.restAdapterProvider = restAdapterProvider;
        this.smsGatewayProvider = smsGatewayProvider;
        this.jsonParser = jsonParser;
    }

    @Transactional
    @Override
    public void onApplicationEvent(IncomingSmsEvent incomingSmsEvent) {
        final EventSource eventSource = this.eventSourceRepository.findOne(incomingSmsEvent.getEventId());

        final SMSBridgeConfig smsBridgeConfig = this.smsBridgeConfigRepository.findByTenantId(eventSource.getTenantId());
        if (smsBridgeConfig == null) {
            logger.error("Unknown tenant " + eventSource.getTenantId() + "!");
            return;
        }
        final IncomingSmsClientId incomingSmsClientId = this.jsonParser.parse(eventSource.getPayload(), IncomingSmsClientId.class);

        String MobileNo = incomingSmsClientId.getMobileNo();
       
        int size = MobileNo.length();
        String incomingMobileNo = MobileNo.substring(1,size);
        //String Mobilenumber=MobileNo.substring(3,size);
        MobileNo=MobileNo.substring(4,size);    
        int count=0;
        final RestAdapter restAdapter = this.restAdapterProvider.get(smsBridgeConfig);
        try {
            String message = "";
            final StringWriter stringWriter = new StringWriter();
            final VelocityContext velocityContext = new VelocityContext();
            final MifosIncomingSmsService IncomingSmsService = restAdapter.create(MifosIncomingSmsService.class);
            final ArrayList<IncomingSms> incomingSms = (ArrayList<IncomingSms>) IncomingSmsService.findClientId(AuthorizationTokenBuilder
                    .token(smsBridgeConfig.getMifosToken()).build(), smsBridgeConfig.getTenantId(), MobileNo, "clients");
            if (incomingSms.size() > 0) {
                for(int i=0;i<incomingSms.size();i++){
                IncomingSms incomingSmsID = incomingSms.get(i);
                String mobileNo ="";
                if(incomingSmsID.getEntityMobileNo()!=null){
                    mobileNo=incomingSmsID.getEntityMobileNo();
                }
                                 
                    /**
                     * method for getting loans And Savings AccountBalance
                     */
                    if (MobileNo.equalsIgnoreCase(mobileNo)||mobileNo.equalsIgnoreCase(0+MobileNo)) {
                        IncomingSmsLoanAndSavingAccoutData incomingSmsLoanAndSavingAccoutData = IncomingSmsService.findDetails(
                                AuthorizationTokenBuilder.token(smsBridgeConfig.getMifosToken()).build(),
                                smsBridgeConfig.getTenantId(), incomingSmsID.getEntityId());                        
                        ArrayList<LoanAccountSummaryData> loanAccounts = (ArrayList<LoanAccountSummaryData>) incomingSmsLoanAndSavingAccoutData
                                .getLoanAccounts(); 
                        ArrayList<SavingsAccountSummaryData> sharesBalance = IncomingSmsService.findsharesBalance(
                                AuthorizationTokenBuilder.token(smsBridgeConfig.getMifosToken()).build(),
                                smsBridgeConfig.getTenantId(), incomingSmsID.getEntityId());
                        //checking 
                           logger.info("Inital "+incomingSmsID.getEntityName());
                           logger.info("Inital "+incomingSmsID.getParentName());
                        if (incomingSmsClientId.getMessage().equalsIgnoreCase("Balance")) {
                                  if ( loanAccounts!=null) {
                                      String[]str=incomingSmsID.getEntityName().split(" ");
                                      velocityContext.put("name", str[0]);                                      
                                      velocityContext.put("branch", incomingSmsID.getParentName());
                                      logger.info("loan Account customer Name"+str[0]);
                                      logger.info("loan Account office Address",incomingSmsID.getParentName());
                                  for (LoanAccountSummaryData loanaccountsId : loanAccounts) {
                                      if(loanaccountsId.getLoanBalance()!=null){
                                        message = message + " Loan Bal(ACCNO:" + loanaccountsId.getId() + ")- "
                                                + loanaccountsId.getLoanBalance().setScale(2) + "";
                                       }
                            }
                            }   
                                  
                                if (sharesBalance!=null&&sharesBalance.size()>0) {
                                	String[]str1=incomingSmsID.getEntityName().split(" ");
                                    velocityContext.put("name", str1[0]);                                      
                                    velocityContext.put("branch", incomingSmsID.getParentName());                                  
                                for (SavingsAccountSummaryData sharesAccountDetails : sharesBalance) {                                    
                                    if(sharesAccountDetails.getAccountBalance()!=null){
                                    message = message + " Saving Bal(ACCNO:" + sharesAccountDetails.getAccountNo() + ")- "
                                            + sharesAccountDetails.getAccountBalance().setScale(2) + "";
                                    }
                                    else{
                                        message = message + " Saving Bal(ACCNO:" + sharesAccountDetails.getAccountNo() + ")- "
                                                + "0.00" + " ";                               
                                    }
                                }
                            }
                            final SMSGateway smsGateway = this.smsGatewayProvider.get(smsBridgeConfig.getSmsProvider());
                            if(message!=null&& message!=""){
                                velocityContext.put("balancemessage", message); 
                                Velocity.evaluate(velocityContext, stringWriter, "balance", this.balance);                    
                             
                            smsGateway.sendMessage(smsBridgeConfig, incomingMobileNo, stringWriter.toString());
                            logger.info(stringWriter.toString());
                            }
                            else{
                                String[]str=incomingSmsID.getEntityName().split(" ");
                                velocityContext.put("name", str[0]);
                                velocityContext.put("branch", incomingSmsID.getParentName());                     
                                Velocity.evaluate(velocityContext, stringWriter, "loanAndSavingBalance", this.loanAndSavingBalance);                             
                                smsGateway.sendMessage(smsBridgeConfig, incomingMobileNo, stringWriter.toString()); 
                                logger.info(stringWriter.toString());
                            }
                        }
                        /**
                         * method for getting miniStatement by passing clientId
                         * as parameter
                         */
                        else if (incomingSmsClientId.getMessage().equalsIgnoreCase("Mini")) {
                            ArrayList<MiniStatementDetails> miniStatementDetail = IncomingSmsService.findMiniStatementDetails(
                                    AuthorizationTokenBuilder.token(smsBridgeConfig.getMifosToken()).build(),
                                    smsBridgeConfig.getTenantId(), incomingSmsID.getEntityId());
                            for (MiniStatementDetails miniStatementDetails : miniStatementDetail) {
                                String recno = miniStatementDetails.getReceiptNumber();
                                if (recno.contains("dummy")) {
                                    recno = "RPT: ";
                                }
                                message = message + "Rpt-"+recno+":"+" "+ miniStatementDetails.getTransactionDate() + " "
                                        + miniStatementDetails.getAmount().setScale(2) + "  ";
                                String[]str2=incomingSmsID.getEntityName().split(" ");
                                velocityContext.put("name", str2[0]);                                      
                                velocityContext.put("branch", incomingSmsID.getParentName());                                
                                                          
                            }
                            
                              final SMSGateway smsGateway = this.smsGatewayProvider.get(smsBridgeConfig.getSmsProvider());
                            if(message!=null && message!=""){
                                velocityContext.put("meassage", message); 
                                Velocity.evaluate(velocityContext, stringWriter, "miniStatement", this.miniStatement);                         
                            smsGateway.sendMessage(smsBridgeConfig, incomingMobileNo, stringWriter.toString());
                            logger.info(stringWriter.toString());
                            }
                            else{
                                 String[]str=incomingSmsID.getEntityName().split(" ");
                                velocityContext.put("name", str[0]);
                                velocityContext.put("branch", incomingSmsID.getParentName());                         
                                if(loanAccounts!=null||sharesBalance.size()>0){
                                Velocity.evaluate(velocityContext, stringWriter, "loanAndSavingsTransaction", this.loanAndSavingsTransaction);                             
                                smsGateway.sendMessage(smsBridgeConfig, incomingMobileNo, stringWriter.toString());
                                logger.info(stringWriter.toString());}
                                else{
                                    Velocity.evaluate(velocityContext, stringWriter, "loanAndSavingBalance", this.loanAndSavingBalance);                            
                                    smsGateway.sendMessage(smsBridgeConfig, incomingMobileNo, stringWriter.toString());
                                    logger.info(stringWriter.toString());
                                }
                            }
                            
                        } else {
                            final SMSGateway smsGateway = this.smsGatewayProvider.get(smsBridgeConfig.getSmsProvider());
                            String[]str=incomingSmsID.getEntityName().split(" ");
                            velocityContext.put("name", str[0]);
                            velocityContext.put("branch", incomingSmsID.getParentName());                     
                            Velocity.evaluate(velocityContext, stringWriter, "invalidText", this.invalidText);                            
                            logger.info(stringWriter.toString());
                            smsGateway.sendMessage(smsBridgeConfig, incomingMobileNo, stringWriter.toString());
                        }
                    }
                    else{
                        count++;
                        if(count==incomingSms.size()){
                            count=0;
                            Velocity.evaluate(null, stringWriter, "unregisteredmobilenumbers", this.unregisteredmobilenumbers);
                            logger.info(stringWriter.toString());
                            final SMSGateway smsGateway = this.smsGatewayProvider.get(smsBridgeConfig.getSmsProvider());
                            smsGateway.sendMessage(smsBridgeConfig,incomingMobileNo, stringWriter.toString());                    
                        }
                    }
                }
            }
                    else {
                        Velocity.evaluate(null, stringWriter, "unregisteredmobilenumbers", this.unregisteredmobilenumbers);
                        logger.info(stringWriter.toString());
                        final SMSGateway smsGateway = this.smsGatewayProvider.get(smsBridgeConfig.getSmsProvider());
                        smsGateway.sendMessage(smsBridgeConfig, incomingMobileNo, stringWriter.toString());
                    }
                
            
        } catch (RetrofitError rer) {
            if (rer.getResponse().getStatus() == 404) {
                logger.info("Client not found!");
                rer.printStackTrace();
            }
        } catch (SMSGatewayException sgex) {
            eventSource.setProcessed(Boolean.FALSE);
            eventSource.setErrorMessage(sgex.getMessage());
            sgex.printStackTrace();
        }

    }

}
