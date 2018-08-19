package com.avior.net.messages;

public class Header3041 implements Header {
	private long length;
	private int orig=0;
	private int dest=0;
	private int msgType;
	
	public Header3041(byte []data) throws IllegalArgumentException{
		if(data.length != 12 )
			throw new IllegalArgumentException("Longitud Inválida");
		//Se hace el and como workaround en caso de lonsgitudes muy grandes y que no salga negativas
		this.length = ((0x000000FF & data[0])<<8 )+ data[1];
		
		if(this.length <= 0 ) throw new IllegalArgumentException("Longitud negativa");
		
		for(int i=2; i<6;i++){
			this.orig += ((int)data[i]&0xFF)<<((6-1-i)*8);			
		}	
		for(int i =6; i<10;i++){
			this.dest += ((int)data[i]&0xFF)<<((10-1-i)*8);			
		}	
		
		this.msgType = (data[10] << 8)+ data[11];
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
		//12 bytes de encbezado + 1 de LRC
		return (this.getLength() != (dataLength -13));
	}	
}
