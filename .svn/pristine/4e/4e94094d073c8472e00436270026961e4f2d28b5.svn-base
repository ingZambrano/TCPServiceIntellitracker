package com.avior.net.messages;

import org.apache.log4j.Logger;

import com.avior.utils.BCD;

public class AviorMessage implements UpdateMessage{
	
	private AviorPacket theMessage;
	private Logger logger = Logger.getLogger(getClass());

	@Override
	public void parseData(Object o, int offset) throws IllegalArgumentException {
		
		byte[] data = (byte[])o;
		
		byte[] hdrData = new byte[14];
		System.arraycopy(data, 0, hdrData, 0, 14);
		Header h;
		try{
			h = new Header3042(hdrData);
		}catch(IllegalArgumentException e){
			logger.info("Error al procesar encabezado de paquete");
			throw new IllegalArgumentException("Bad header");
		}//El header no se pudo parsear, se lanzó una excepcion desde el constructor
		
		byte calculatedLRC=0;
		calculatedLRC=BCD.lrc(data, 4, data.length - 2);
		
		//logger.debug("LRC: " + calculatedLRC);
		//logger.debug("LRC diferentes? " + (data[data.length - 1] != calculatedLRC));
		//logger.debug("Longitud válida? " + h.isLengthValid(data.length));
		//logger.debug("Destino incorrecto? " + h.getDest());
		
		if (data[data.length - 1] != calculatedLRC || //Es diferente el LRC calculado que el recibido
				h.isLengthValid(data.length) || //tiene que coincidir con lo declarado en el encabezado
				h.getDest() != 1) //Este no es el destino
		{
			logger.error("Paquete malformado (LRC o longitud)");
			logger.debug("LRC " + BCD.lrc(data, 2, data.length - 2));

			//El socket debe cerrarse en la capa superior (Request) en caso de haber excepcion
			throw new IllegalArgumentException("Bad packet (LRC or size)");
		}
		
		byte[] msg = new byte[(int) h.getLength()];
		
		System.arraycopy(data, 14, msg, 0, (int) h.getLength());
		switch (h.getMsgType()) {

		case 0x3041:// Actualizacion de ubicación
			// debugHexBuffer(msg);
			this.theMessage = new AviorSingleMessage();	
			this.theMessage.setHeader(h);
			break;
		case 0x3042:// Actualización en lote
			// debugHexBuffer(msg);
			this.theMessage = new AviorBatchUpdate();
			this.theMessage.setHeader(h);
			break;
		default:// No identificado
			break;
		}
		
		try{
			this.theMessage.parseData(msg, 0);
		}catch(Throwable e){
			logger.error("Error al parsear datos del paquete/guardarlos: " , e);
			throw new IllegalArgumentException(e.getMessage());
		}
		
	}

	@Override
	public void persist() {
		this.theMessage.persist();		
	}
	
	public int NumPaq(Object o){
		return 0;
	}

}
