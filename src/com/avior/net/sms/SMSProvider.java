package com.avior.net.sms;

public interface SMSProvider {
	/**
	 * Envia un mensaje SMS, el mensaje puede ser personalizable especificando
	 * el template en el archivo sms.properties, bajo la llave sms.message
	 * @param destinatary El telefono celular del destinatario
	 * @param message El mensaje a incluir en el template. Este parametro será
	 *                reemplazado en cada ocurrencia de la cadena "%v" en el template.
	 * @return La respuesta del servicio de mensajeria:
	 *         1 Si ha sido enviadoel mensaje con exito
	 *         2 Si se ha presentado un error temporal que permita reintentarlo
     *    
	 */
	public Integer sendSMS(String destinatary, String message);
	public Integer sendUnicodeSMS(String dest, String message);
}
