package org.mifos.module.sms.provider;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.mifos.module.sms.domain.SMSBridgeConfig;
import org.mifos.module.sms.exception.SMSGatewayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AfricasRestClientProvider implements SMSGateway {
	
	private static final Logger logger = LoggerFactory.getLogger(AfricasRestClientProvider.class);

	AfricasRestClientProvider() {
		super();
    }
	@Override
	public JSONArray sendMessage(final SMSBridgeConfig smsBridgeConfig, final String mobileNo, final String message)
	        throws SMSGatewayException { 
		JSONArray results=null;
             String username = smsBridgeConfig.getSmsProviderToken();
             String apiKey   = smsBridgeConfig.getSmsProviderAccountId();
             
             AfricasTalkingGateway gateway  = new AfricasTalkingGateway(username, apiKey);
    
            try {
             results = gateway.sendMessage(mobileNo, message);
            //logger.debug("Loan Repayment Reminder Sms  to event processed!");
                   for( int i = 0; i < results.length(); ++i ) {
                  JSONObject result = results.getJSONObject(i);
                  logger.info(result.getString("status") + ","); 
                  logger.info(result.getString("number") + ",");
                  logger.info(result.getString("messageId") + ",");
                  logger.info(result.getString("cost"));
        }
       }
       
       catch (Exception e) {
    	   logger.info("Encountered an error while sending " + e.getMessage());
        }  
            return results;
   }	
}
