package com.avior.net.messages;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;

import com.avior.utils.MySQLDBDAO;
import com.avior.utils.TrackingDAO;

public class MaestroMessage implements UpdateMessage {

	private Long imei;
	private Long sequence;
	private Double batteryVoltage;
	private Integer gsmSignalStrenght;
	private Integer starter;
	private Integer ignitionMode;
	private Date date;
	private Double latitude;
	private Double longitude;
	private Double altitude;
	private Double velocity;
	private Double direction;
	private Double accuracy;
	private Double milleage;
	private Double analog;

	Logger logger = Logger.getLogger(getClass());
	@Override
	public void parseData(Object o, int offset) throws IllegalArgumentException {
		try {
			String msg = new String((byte[])o);
			String[] data = msg.split(",");
			
			if(data.length != 19) throw new IllegalArgumentException();
			
			imei = Long.parseLong(data[0].substring(1));
			sequence = Long.parseLong(data[1]);
			batteryVoltage = Double.parseDouble(data[4]);
			this.gsmSignalStrenght = Integer.parseInt(data[5]);
			this.starter = Integer.parseInt(data[6]);
			this.ignitionMode = Integer.parseInt(data[7]);
			// this.date = new Date(data[8] + data[9]);
			SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
			try{
				this.date = sdf.parse(data[8] + " " + data[9]);
			}catch(ParseException ex){
				throw new IllegalArgumentException();
			}
			
			this.latitude = Double.parseDouble(data[10]);
			this.longitude = Double.parseDouble(data[11]);
			this.altitude = Double.parseDouble(data[12]);
			this.velocity = Double.parseDouble(data[13]);
			this.direction = Double.parseDouble(data[14]);
			this.accuracy = Double.parseDouble(data[16]);
			this.milleage = Double.parseDouble(data[17]);
			this.analog = Double.parseDouble(data[18].substring(0,data[18].indexOf('!')));
		} catch (Throwable e) {
			logger.error("Error al procesar mensaje de Maestro", e);
			throw new IllegalArgumentException("Malformed Maestro packet.");
		}
	}

	@Override
	public void persist() {
		logger.debug("Guardando registro");
		TrackingDAO dao = new MySQLDBDAO();

		dao.insertaRegistro(this.imei, this.latitude, this.longitude, this.velocity, this.altitude, this.date, 0, null, 0, "");
		
	}
	
	public int NumPaq(Object o){
		return 0;
	}

	/*-----------------Getters and Setters ------------------------------*/
	public Long getImei() {
		return imei;
	}

	public void setImei(Long imei) {
		this.imei = imei;
	}

	public Long getSequence() {
		return sequence;
	}

	public void setSequence(Long sequence) {
		this.sequence = sequence;
	}

	public Double getBatteryVoltage() {
		return batteryVoltage;
	}

	public void setBatteryVoltage(Double batteryVoltage) {
		this.batteryVoltage = batteryVoltage;
	}

	public Integer getGsmSignalStrenght() {
		return gsmSignalStrenght;
	}

	public void setGsmSignalStrenght(Integer gsmSignalStrenght) {
		this.gsmSignalStrenght = gsmSignalStrenght;
	}

	public Integer getStarter() {
		return starter;
	}

	public void setStarter(Integer starter) {
		this.starter = starter;
	}

	public Integer getIgnitionMode() {
		return ignitionMode;
	}

	public void setIgnitionMode(Integer ignitionMode) {
		this.ignitionMode = ignitionMode;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getAltitude() {
		return altitude;
	}

	public void setAltitude(Double altitude) {
		this.altitude = altitude;
	}

	public Double getVelocity() {
		return velocity;
	}

	public void setVelocity(Double velocity) {
		this.velocity = velocity;
	}

	public Double getDirection() {
		return direction;
	}

	public void setDirection(Double direction) {
		this.direction = direction;
	}

	public Double getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(Double accuracy) {
		this.accuracy = accuracy;
	}

	public Double getMilleage() {
		return milleage;
	}

	public void setMilleage(Double milleage) {
		this.milleage = milleage;
	}

	public Double getAnalog() {
		return analog;
	}

	public void setAnalog(Double analog) {
		this.analog = analog;
	}

}
