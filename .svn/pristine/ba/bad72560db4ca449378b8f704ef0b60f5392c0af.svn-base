package com.avior.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;

import com.avior.net.messages.AviorMessage;
import com.avior.net.messages.MaestroMessage;
import com.avior.net.messages.TK102Message;
import com.avior.net.messages.UpdateMessage;
import com.avior.utils.Properties;

/*
 * java -Xms8M -Xmx128M -ea -Dservertype=AVIOR -jar AviorTCP.jar 9000
 * 
 * */


public class Server {
	private static Logger logger = Logger.getLogger(Server.class);
	
	
	
	
	private static void usage(){
		System.out.println("Error en parametros, uso:");
		System.out.println("java -jar AviorTCP.jar [puerto]");
		System.out.println("   El puerto es númerico y está entre 1 - 65535"); 
		System.out.println("   Adicionalmente se necesita la variable de java \"servertype\"");
		System.out.println("   intente agregando el parametro -Dservertype=XXXXXXX");
		System.out.println("   El tipo de servidor es uno de los siguientes:");
		System.out.println("      XEXUN   Para atender mensajes de Xexun");
		System.out.println("      MAESTRO Atiende mensajes de Maestro");
		System.out.println("      AVIOR   Nuestros mensajes");
		System.out.println("");
	}
	

	public static void main(String[] args) {
		Integer hostPort = 9000;
		String serverType = "";		try{
			assert args.length == 1;
			serverType = System.getProperty("servertype");
			assert serverType != null;
			assert serverType.equals("AVIOR") || serverType.equals("XEXUN") || serverType.equals("MAESTRO");
			hostPort = Integer.parseInt(args[0]);
			serverType = System.getProperty("servertype");
		}catch(Throwable e){
			usage();
			System.exit(-1);
		}		
				
		Properties.loadProperties();
		
		
		try {	        
			ServerSocket serverSocket = new ServerSocket(hostPort);
	        logger.debug("Iniciando servidor en el puerto " + hostPort);
    		logger.debug("Este servidor atender� peticiones de " + serverType);

	        while (true) {
	            Socket connectionSocket = serverSocket.accept();
	            logger.debug("Threads activos: " + Thread.activeCount());
	            UpdateMessage msg = null;
	            if (connectionSocket != null) {
	            	if(serverType.equals("XEXUN")){
	            		msg = new TK102Message();
	            	}else if(serverType.equals("AVIOR")){
	            		msg = new AviorMessage();
	            	}else if(serverType.equals("MAESTRO")){
	            		msg = new MaestroMessage();
	            	}
	            	
	                Request request = new Request(connectionSocket, msg);
	                Thread thread = new Thread(request);
	                thread.start();
	            }
	        }
	    } catch (IOException ioe) {
	    	
	    	logger.error("IOException en el socket servidor: " + ioe);
	        logger.error(ioe.getMessage());
	    } 
		
	}	
}
