package com.avior.net.sms;

import java.util.Properties;

import org.apache.log4j.Logger;

import com.nexmo.messaging.sdk.NexmoSmsClient;
import com.nexmo.messaging.sdk.SmsSubmissionResult;
import com.nexmo.messaging.sdk.messages.TextMessage;

public class NexmoClient implements SMSProvider {
	
	private static NexmoClient instance;
	
	private Logger logger = Logger.getLogger(getClass());
	
	private String apiKey;
	private String apiSecret;
	private String smsFrom;
	private String msgTemplate;
	
	
	public NexmoClient() {
		super();
		Properties prop = new Properties();
		try{
			prop.load(BulkSMSProvider.class.getClassLoader().getResourceAsStream("sms.properties"));
			this.apiKey = prop.getProperty("sms.nexmo.apikey");
			this.apiSecret = prop.getProperty("sms.nexmo.apisec");
			this.msgTemplate = prop.getProperty("sms.message");
			this.smsFrom = prop.getProperty("sms.nexmo.from");
		}catch(Exception e){
			logger.error("Error al cargar archivo de propiedades de SMS", e);
		}
	}

	@Override
	public Integer sendSMS(String destinatary, String parameter) {
		Integer result=1;
		
		// Create a client for submitting to Nexmo

        NexmoSmsClient client = null;
        try {
            client = new NexmoSmsClient(this.apiKey, this.apiSecret);
        } catch (Exception e) {
        	logger.error("Failed to instanciate a Nexmo Client");
        	logger.error(e.getMessage());
            throw new RuntimeException("Failed to instanciate a Nexmo Client");
        }

        // Create a Text SMS Message request object ...
        String toReplace = parameter.length()>106?parameter.substring(0, 105):parameter;
        String smsMsg = this.msgTemplate.replace("%v", toReplace);

        TextMessage message = new TextMessage(this.smsFrom, destinatary, smsMsg);

        // Use the Nexmo client to submit the Text Message ...

        SmsSubmissionResult[] results = null;
        try {
            results = client.submitMessage(message);
        } catch (Exception e) {
            logger.error("Failed to communicate with the Nexmo Client");
            logger.error(e.getMessage());
            throw new RuntimeException("Failed to communicate with the Nexmo Client");
        }

        // Evaluate the results of the submission attempt ...
        logger.info("SMS Message submitted in [ " + results.length + " ] parts");
        for (int i=0;i<results.length;i++) {
        	logger.debug("--------- part [ " + (i + 1) + " ] ------------");
        	logger.debug("Status [ " + results[i].getStatus() + " ] ...");
            if (results[i].getStatus() == SmsSubmissionResult.STATUS_OK){           	
                logger.debug("SUCCESS");
            }else if (results[i].getTemporaryError()){
            	logger.debug("TEMPORARY FAILURE - PLEASE RETRY");
                result = 2;
            }else{
            	logger.debug("SUBMISSION FAILED!");
                result=3;
            }
            logger.debug("Message-Id [ " + results[i].getMessageId() + " ] ...");
            logger.debug("Error-Text [ " + results[i].getErrorText() + " ] ...");

            if (results[i].getMessagePrice() != null)
            	logger.debug("Message-Price [ " + results[i].getMessagePrice() + " ] ...");
            if (results[i].getRemainingBalance() != null)
            	logger.debug("Remaining-Balance [ " + results[i].getRemainingBalance() + " ] ...");
        }
		return result;
	}

	@Override
	public Integer sendUnicodeSMS(String dest, String message) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static SMSProvider getInstance(){
		if(NexmoClient.instance == null){
			NexmoClient.instance = new NexmoClient();
		}
		return NexmoClient.instance;
	}
}
