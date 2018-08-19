package com.avior.utils;

import java.util.Date;


public interface TrackingDAO {
	public int insertaRegistro(Long serial, double Lat, double Long, double Vel, double altitud, Date fecha, int noSatelite, String voltaje, int batConectada, String fechaString);
	public void configDAO(String user, String pass, String proceso, String urlConexion, String callProceso);
	public EstadoEstacionamiento leeEstadoEstacionamiento(Long serial);
	public void actualizaEstadoEstacionamiento(Long serial, Integer status);
	
	//public void actualizaRegistro(Long serial);
	//public boolean esMismaUbicacion(Long serial, double Lat, double Long);
	//public boolean isOutOfFence(Long serial, Double Lat, Double Lon);
}
