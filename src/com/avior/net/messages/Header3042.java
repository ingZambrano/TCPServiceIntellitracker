package com.avior.net.messages;

public class Header3042 implements Header {
	private long length;
	private int orig=0;
	private int dest=0;
	private int msgType;
	
	//0000001500000002000000013041
	public Header3042(byte []data) throws IllegalArgumentException{
		if(data.length != 14 )
			throw new IllegalArgumentException("Longitud Inválida");
		
		//00 00 00 15
		//Se hace el and como workaround en caso de longitudes muy grandes y que no salga negativas
		this.length  = ((0x000000FF & data[0]) << 24);
		this.length += ((0x000000FF & data[1]) << 16);
		this.length += ((0x000000FF & data[2]) <<  8);
		this.length +=  (0x000000FF & data[3]);
		
		this.orig = ((data[4] & 0xFF)<<24) + ((data[5] & 0xFF)<<16) + ((data[6] & 0xFF)<<8) + (data[7] & 0xFF); 
		
		this.dest = ((data[8] & 0xFF)<<24) + ((data[9] & 0xFF)<<16) + ((data[10] & 0xFF)<<8) + (data[11] & 0xFF);
				
		this.msgType = (data[12] << 8)+ data[13];
	}
	
	
	public long getLength() {
		return length;
	}
	public void setLength(long length) {
		this.length = length;
	}
	public int getOrig() {
		return orig;
	}
	public void setOrig(int orig) {
		this.orig = orig;
	}
	public int getDest() {
		return dest;
	}
	public void setDest(int dest) {
		this.dest = dest;
	}
	public int getMsgType() {
		return msgType;
	}
	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}


	@Override
	public boolean isLengthValid(int dataLength) {
		//El tamaño declarado en el encabezado debe ser igual al contenido en el mensaje.
		//14 bytes de encabezado + 1 de LRC
		return (this.length != (dataLength-15));
	}
}




