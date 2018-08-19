package com.avior.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import org.apache.log4j.Logger;

import com.avior.net.messages.UpdateMessage;
import com.avior.utils.BCD;

public class Request implements Runnable{
	
	private Logger logger = Logger.getLogger(getClass());
	
	private Socket socket;
	private DataInputStream  inFromClient;
	private DataOutputStream outToClient;
	private UpdateMessage updateMsg;
	
	public Request(Socket socket, UpdateMessage msg) {
		super();
		this.socket = socket;
		this.updateMsg = msg;
	}


	@Override
	public void run() {
		try {
			//Establecemos el timeout de lectura a 30s, si no llega nada en ese tiempo causa una excepcion de Timeout
			this.socket.setSoTimeout(10*1000);

						
	        inFromClient = new DataInputStream(socket.getInputStream()); 
	        outToClient = new DataOutputStream(socket.getOutputStream());
	        
	        try {
				Thread.sleep(10000L);
			} catch (InterruptedException e1) {
				logger.error("Espera en el thread interrumpida", e1);
			}
	        
			Integer bytesToRead = socket.getInputStream().available();
						
			if(bytesToRead == 0){
				throw new IOException("No hay bytes para leer");
			}
			
	        byte[] datos = new byte[bytesToRead];
	        inFromClient.read(datos);
	        logger.debug("HexData:\n" + BCD.debugHexBuffer((byte[]) datos));
	        logger.debug("String:\n" + new String(datos, "ASCII"));
        	
        	int num_paq = this.updateMsg.NumPaq(datos);
        	logger.debug("Numero de paquetes: "+num_paq);;
        	for(int i=0;i<num_paq;i++){
	        try{
	        	this.updateMsg.parseData(datos, i);
	        	logger.debug("Datos parseados...OK");
	        	this.updateMsg.persist();
	        	logger.debug("Datos guardados...OK");
	        	this.inFromClient.close();
	        	this.outToClient.close();
	        	String skt = this.socket.getRemoteSocketAddress().toString();
	        	this.socket.close();
	        	logger.debug("Socket cerrado: " + skt + "->" + this.socket.getRemoteSocketAddress());
	        }catch(Exception e){
	        	logger.error("Error al manejar peticion", e);	        	
	        }finally{
	        	try{
	        		this.inFromClient.close();
	        		this.outToClient.close();
	        		this.socket.close();
	        	}catch(Exception e){
	        		logger.error("Streams ya cerrados");
	        	}
	        }	  
        	}
		} catch (IOException e) {
	        logger.error("Error al recibir datos en Thread: ", e);
	    }
		return;
	}
}
