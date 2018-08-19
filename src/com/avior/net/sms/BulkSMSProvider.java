package com.avior.net.sms;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Properties;

import org.apache.log4j.Logger;

public class BulkSMSProvider implements SMSProvider {
	
	private String username;
	private String password;
	private String msgTemplate;
	private static BulkSMSProvider instance = null;
	private Logger logger = Logger.getLogger(getClass());


	public static final Integer IN_PROGRESS                  = 0;
    public static final Integer SCHEDULED                    = 1;
    public static final Integer INTERNAL_FATAL_ERROR         = 22;
    public static final Integer AUTH_FAILURE                 = 23;
    public static final Integer DATA_VALIDATION_FAILED       = 24;
    public static final Integer INSUFFICIENT_CREDITS         = 25;
    public static final Integer UPSTREAM_CREDITS_UNAVAILABLE = 26;
    public static final Integer DAILY_QUOTA_EXCEEDED         = 27;
    public static final Integer UPSTREAM_QUOTA_EXCEEDED      = 28;
    public static final Integer TEMPORARILY_UNAVAILABLE      = 40;
    public static final Integer MAX_BATCH_SIZE               = 201;
	
	
	public BulkSMSProvider() {
		super();
		Properties prop = new Properties();
		try{
			prop.load(BulkSMSProvider.class.getClassLoader().getResourceAsStream("sms.properties"));
			this.username = prop.getProperty("sms.user");
			this.password = prop.getProperty("sms.password");
			this.msgTemplate = prop.getProperty("sms.message");
		}catch(Exception e){
			logger.error("Error al cargar archivo de propiedades de SMS", e);
		}
	}

	@Override
	public Integer sendSMS(String destinatary, String parameter) {
		try {
            // Construct data
			//El tamaño maximo de parameter es de 126 caracteres, en ASCII
			if(parameter.length() > 126) parameter = parameter.substring(0, 125);
			String message = this.msgTemplate.replace("%v", parameter);
            String data = "";
            
            data += "username=" + URLEncoder.encode(this.username, "ISO-8859-1");
            data += "&password=" + URLEncoder.encode(this.password, "ISO-8859-1");
            data += "&message=" + URLEncoder.encode(message, "ISO-8859-1");
            data += "&want_report=1";
            data += "&routing_group=2"; //1 Low priority, 2 normal, 3 high (influye en el precio)
            data += "&msisdn=" + destinatary;

            // Send data
            URL url = new URL("http://bulksms.vsms.net:5567/eapi/submission/send_sms/2/2.0");

            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            // Get the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                // Print the response output...
                logger.debug("Respuesta de BulkSMS:" + line);
              //TODO: Implementar aqui el parseo de la respuesta de tipo "0|IN_PROGRESS|460065633"
            }
            wr.close();
            rd.close();
        } catch (Exception e) {
            logger.error("Error al enviar SMS", e);
        }
		return 0;
	}

	public static SMSProvider getInstance(){
		if(BulkSMSProvider.instance == null){
			BulkSMSProvider.instance = new BulkSMSProvider();
		}
		return BulkSMSProvider.instance;		
	}

	@Override
	public Integer sendUnicodeSMS(String dest, String message) {
		try {
            // Construct data
            String data = "";
          
            data += "username=" + URLEncoder.encode(username, "ISO-8859-1");
            data += "&password=" + URLEncoder.encode(password, "ISO-8859-1");
            data += "&message=" + stringToHex(message);
            data += "&dca=16bit";
            data += "&want_report=1";
            data += "&msisdn=" + dest;

            // Send data
            URL url = new URL("http://bulksms.vsms.net:5567/eapi/submission/send_sms/2/2.0");
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            // Get the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                // Print the response output...
            	logger.debug("Remover estos mensajes");
                logger.debug(line);
                //TODO: Implementar aqui el parseo de la respuesta de tipo "0|IN_PROGRESS|460065633"
            }
            wr.close();
            rd.close();
        } catch (Exception e) {
            logger.error("Error al enviar SMS Unicode", e);
        }
		return 0;
	}
	
	private String stringToHex(String s) {
        char[] chars = s.toCharArray();
        String next;
        StringBuffer output = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            next = Integer.toHexString((int)chars[i]);
            // Unfortunately, toHexString doesn't pad with zeroes, so we have to.
            for (int j = 0; j < (4-next.length()); j++)  {
                output.append("0");
            }
            output.append(next);
        }
        return output.toString();
    }
}
