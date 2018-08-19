package com.avior.net.messages;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import com.avior.net.sms.NexmoClient;
import com.avior.net.sms.SMSProvider;
import com.avior.utils.EstadoEstacionamiento;
import com.avior.utils.MySQLDBDAO;
import com.avior.utils.Properties;
import com.avior.utils.TrackingDAO;

public class TK102Message implements UpdateMessage {
	private Logger logger = Logger.getLogger(getClass());
	
	private Date hora;
	private Double latitud;
	private Double longitud;
	private Double velocidad;
	private Double curso;
	private Long imei;
	private Double altitud;
	private String voltaje;
	private int cargando;
	private String MCC;
	private String MNC;
	private String LAC;
	private String cellID;
	private String [] values;
	private int offset;
	private int noSatelites;
	private String fechaString;
	
	
	
	public TK102Message() {
		super();
	}	
	

	@Override
	public void parseData(Object o, int i) throws IllegalArgumentException{
		offset = i*27;
		String message = new String((byte[]) o);
		values = message.split(",");
		if(values.length <= 1) throw new IllegalArgumentException();
		
		//detect the number of extra symbols on date
		int ne = values[offset].length() - 12;
		
		int year,month,day,hour,minute,second;
		year = Integer.parseInt(values[0+offset].substring(0+ne, 2+ne)) + 2000;
		month = Integer.parseInt(values[0+offset].substring(2+ne, 4+ne)) - 1;
		day = Integer.parseInt(values[0+offset].substring(4+ne, 6+ne));
		hour = Integer.parseInt(values[0+offset].substring(6+ne, 8+ne));
		minute = Integer.parseInt(values[0+offset].substring(8+ne,10+ne));
		second = Integer.parseInt(values[0+offset].substring(10+ne));		
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		
		
		
		cal.clear();
		cal.set(year, month, day, hour, minute, second);
		this.hora = cal.getTime();
		
		//Latitud
		this.latitud = Double.parseDouble(values[5+offset].substring(0, 2)) + (Double.parseDouble(values[5+offset].substring(2))/60.0);
		this.latitud = values[6+offset].contains("N")? this.latitud : -this.latitud;
		
		this.longitud = Double.parseDouble(values[7+offset].substring(0, 3)) + (Double.parseDouble(values[7+offset].substring(3))/60.0);
		this.longitud = values[8+offset].contains("E")? this.longitud : -this.longitud;
		
		//Al parecer la velocidad que envia esta en nudos, asi que lo pasamos a km/h
		this.velocidad = Double.parseDouble(values[9+offset])*1.852;
		
		this.curso = Double.parseDouble(values[10+offset]);
		
		this.imei = Long.parseLong(values[17+offset].substring(7));
		
		this.noSatelites = Integer.parseInt(values[18+offset]);
		
		this.altitud = Double.parseDouble(values[19+offset]);
		
		this.voltaje = values[20+offset];
		
		this.cargando = Integer.parseInt(values[21+offset]);
		
		this.MCC = values[24+offset];
		this.MNC = values[25+offset];
		this.LAC = values[26+offset];
		this.cellID = values[27+offset];
		
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HHmmss");		
		
		this.fechaString = sdf.format(this.hora);
		
		
		
		logger.info("Fecha Date: "+this.hora);
		logger.info("Fecha String: "+ this.fechaString);
		
	}
	
	public int NumPaq(Object o){
		String message = new String((byte[]) o);
		values = message.split(",");
		
		return values.length / 27;
	}
	
	@Override
	public void persist() {	
		
		String user = Properties.getProperty("user");
		String pass = Properties.getProperty("passwd");
		String proceso = Properties.getProperty("proceso");
		String urlConexion = Properties.getProperty("url_conexion");
		String callProceso = Properties.getProperty("call_proceso");
		
		TrackingDAO dao = new MySQLDBDAO();
		logger.debug("Guardando paquete recibido TK103");
		
		//Envio de datos a produccion
		//dao.configDAO(user, pass, proceso, urlConexion, callProceso);
		//dao.insertaRegistro(this.imei, this.latitud, this.longitud, this.velocidad,this.altitud, this.hora, this.noSatelites, this.voltaje, this.cargando, "");
		
		//Envio de datos a Hiperion
		dao.configDAO(user, pass, proceso, urlConexion, callProceso);
		dao.insertaRegistro(this.imei, this.latitud, this.longitud, this.velocidad,this.altitud, this.hora, this.noSatelites, this.voltaje, this.cargando, this.fechaString);
		logger.debug("Comprobando estado de estacionamiento");
		checkParking(dao);
	}
	
	/**
	 * Comprueba el estado del vehículo con respecto al modo estacionamiento
	 * y actúa en consecuencia al estado.  
	 */
	private void checkParking(TrackingDAO dao){
		EstadoEstacionamiento st = dao.leeEstadoEstacionamiento(imei);
		
		switch(st.getStatus()){
		case 0: return;
		case 1: //Revisa la posicion actual y la fijada
			logger.debug("Latitud recibida: " + this.latitud);
			logger.debug("Latitud fijada  : " + st.getLatitudFijada());
			logger.debug("Long recibida: " + this.longitud);
			logger.debug("Long fijada  : " + st.getLongitudFijada());
			if( (Math.abs(this.latitud  - st.getLatitudFijada())  > 0.002) && 
			    (Math.abs(this.longitud - st.getLongitudFijada()) > 0.002)){
				logger.info("Vehiculo fuera de los limites");
				//Informa al usuario
				SMSProvider smssvc = NexmoClient.getInstance();
				//No se reemplazara algo en el template, por eso la cadena vacia
				Integer result = smssvc.sendSMS("+52"+st.getTelefonoContacto(), "");
				
				//Actualiza el estado en la base de datos
				if (result == 1){
					dao.actualizaEstadoEstacionamiento(imei, 2);
				}else if(result == 2){
					dao.actualizaEstadoEstacionamiento(imei, 3);
				}else{
					logger.error("Error crítico al enviar SMS");
				}				
			}
			break;
		case 2://Ya se ha informado al usuario del incidente, no se hace nada
			break;
		case 3: //Intenta nuevamente y espera el resultado de Nexmo
			//Informa al usuario
			SMSProvider smssvc = NexmoClient.getInstance();
			Integer result = smssvc.sendSMS("521" + st.getTelefonoContacto(), "[Reintento]");
			
			//Actualiza el estado en la base de datos
			if (result == 1){
				dao.actualizaEstadoEstacionamiento(imei, 2);
			}else if(result == 2){
				dao.actualizaEstadoEstacionamiento(imei, 3);
			}else{
				logger.error("Error crítico al enviar SMS");
			}
			break;
		}
	} 
	
	
	
	
	public Date getHora() {
		return hora;
	}
	public void setHora(Date hora) {
		this.hora = hora;
	}
	public Double getLatitud() {
		return latitud;
	}
	public void setLatitud(Double latitud) {
		this.latitud = latitud;
	}
	public Double getLongitud() {
		return longitud;
	}
	public void setLongitud(Double longitud) {
		this.longitud = longitud;
	}
	public Double getVelocidad() {
		return velocidad;
	}
	public void setVelocidad(Double velocidad) {
		this.velocidad = velocidad;
	}
	public Double getCurso() {
		return curso;
	}
	public void setCurso(Double curso) {
		this.curso = curso;
	}
	public Long getImei() {
		return imei;
	}
	public void setImei(Long imei) {
		this.imei = imei;
	}
	public Double getAltitud() {
		return altitud;
	}
	public void setAltitud(Double altitud) {
		this.altitud = altitud;
	}
	public String getVoltaje() {
		return voltaje;
	}
	public void setVoltaje(String voltaje) {
		this.voltaje = voltaje;
	}
	
	
	
	public int getCargando() {
		return cargando;
	}


	public void setCargando(int cargando) {
		this.cargando = cargando;
	}


	public String[] getValues() {
		return values;
	}


	public void setValues(String[] values) {
		this.values = values;
	}


	public int getOffset() {
		return offset;
	}


	public void setOffset(int offset) {
		this.offset = offset;
	}


	public String getMCC() {
		return MCC;
	}
	public void setMCC(String mCC) {
		MCC = mCC;
	}
	public String getMNC() {
		return MNC;
	}
	public void setMNC(String mNC) {
		MNC = mNC;
	}
	public String getLAC() {
		return LAC;
	}
	public void setLAC(String lAC) {
		LAC = lAC;
	}
	public String getCellID() {
		return cellID;
	}
	public void setCellID(String cellID) {
		this.cellID = cellID;
	}


	public int getNoSatelites() {
		return noSatelites;
	}


	public void setNoSatelites(int noSatelites) {
		this.noSatelites = noSatelites;
	}


	public String getFechaString() {
		return fechaString;
	}


	public void setFechaString(String fechaString) {
		this.fechaString = fechaString;
	}	
	
	
	
	
}
