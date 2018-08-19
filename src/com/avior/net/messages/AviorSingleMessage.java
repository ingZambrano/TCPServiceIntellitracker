package com.avior.net.messages;

import java.sql.Date;
import java.util.Calendar;

import org.apache.log4j.Logger;

import com.avior.utils.BCD;
import com.avior.utils.MySQLDBDAO;
import com.avior.utils.TrackingDAO;

public class AviorSingleMessage implements AviorPacket {

	private Header header;
	private double latitud;
	private double longitud;
	private char hlat;
	private char hlong;
	private double altura;
	private double velocidad;
	private java.util.Date fecha;
	private char status;
	private Logger logger = Logger.getLogger(getClass());

	
	
	public AviorSingleMessage() {
		super();
	}

	public AviorSingleMessage(Header h2, byte[] msg) {
		super();
		this.header = h2;
		parseSingleMessage(msg);
	}

	public void parseSingleMessage(byte[] msg){
		int grados, minutos, decimal;
		byte[] dec = new byte[2];
		// Parsear la latitud msg[0-3]
		grados = BCD.asInt(msg[0]);
		minutos = BCD.asInt(msg[1]);
		System.arraycopy(msg, 2, dec, 0, 2);
		decimal = BCD.asInt(dec);
		this.latitud = grados + ((minutos + (decimal / 10000.0)) / 60.0);

		this.hlat = (char) msg[4];

		// Parsear la longitud msg[5-9]
		byte[] tmp = new byte[5];
		System.arraycopy(msg, 5, tmp, 0, 5);
		String lon = BCD.asString(tmp);
		try {
			grados = Integer.parseInt(lon.substring(0, 3));
			minutos = Integer.parseInt(lon.substring(3, 5));
			decimal = Integer.parseInt(lon.substring(5));
			this.longitud = grados + ((minutos + (decimal / 100000.0)) / 60.0);
		} catch (NumberFormatException e) {
			logger.error(e.getMessage());
			throw new IllegalArgumentException("Error in longitude format");
		}

		this.hlong = (char) msg[10];

		// Parsear la velocidad 3 bytes en BCD con 2 decimales msg[11-13]
		tmp = new byte[3];
		System.arraycopy(msg, 11, tmp, 0, 3);
		String vel = BCD.asString(tmp);
		this.velocidad = Double.parseDouble(vel) / 100.0;

		// Parsear la altitud 3 bytes en BCD dos decimales msg[14-16]
		//System.arraycopy(msg, 14, tmp, 0, 3);
		//this.altura = Double.parseDouble(BCD.asString(tmp)) / 100.0;
		
		//Parsear la hora y fecha
		Integer dia,mes,anio,h,min,seg;
		tmp = new byte[1];
		System.arraycopy(msg, 14, tmp, 0, 1);
		dia = Integer.parseInt(BCD.asString(tmp));
		System.arraycopy(msg, 15, tmp, 0, 1);
		mes = Integer.parseInt(BCD.asString(tmp)) - 1; //Basado en cero
		System.arraycopy(msg, 16, tmp, 0, 1);
		anio = Integer.parseInt(BCD.asString(tmp)) + 2000;
		System.arraycopy(msg, 17, tmp, 0, 1);
		h = Integer.parseInt(BCD.asString(tmp));
		System.arraycopy(msg, 18, tmp, 0, 1);
		min = Integer.parseInt(BCD.asString(tmp));
		System.arraycopy(msg, 19, tmp, 0, 1);
		seg = Integer.parseInt(BCD.asString(tmp));
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(anio, mes, dia, h, min, seg);
		this.fecha = cal.getTime();
	}

	public void persist() {
		logger.debug("Guardando registro");
		double lat = 0, lon = 0;
		Long serial = new Long(this.getHeader().getOrig());
		lat = this.hlat == 'S' ? -this.latitud : this.latitud;
		lon = this.hlong == 'W' ? -this.longitud : this.longitud;
		TrackingDAO dao = new MySQLDBDAO();

		dao.insertaRegistro(serial, lat, lon, this.velocidad, this.altura, this.fecha, 0, null, 0,"");
		
	}
	
	public int NumPaq(Object o){
		return 0;
	}

	public Header getHeader() {
		return header;
	}

	
	@Override
	public void setHeader(Header header) {
		this.header = header;
	}

	public double getLatitud() {
		return latitud;
	}

	public void setLatitud(double latitud) {
		this.latitud = latitud;
	}

	public double getLongitud() {
		return longitud;
	}

	public void setLongitud(double longitud) {
		this.longitud = longitud;
	}

	public char getHlat() {
		return hlat;
	}

	public void setHlat(char hlat) {
		this.hlat = hlat;
	}

	public char getHlong() {
		return hlong;
	}

	public void setHlong(char hlong) {
		this.hlong = hlong;
	}

	public double getAltura() {
		return altura;
	}

	public void setAltura(double altura) {
		this.altura = altura;
	}

	public double getVelocidad() {
		return velocidad;
	}

	public void setVelocidad(double velocidad) {
		this.velocidad = velocidad;
	}

	public java.util.Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public char getStatus() {
		return status;
	}

	public void setStatus(char status) {
		this.status = status;
	}

	@Override
	public void parseData(Object o, int offset) throws IllegalArgumentException {
		this.parseSingleMessage((byte[])o);
		
	}

}
