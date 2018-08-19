package com.avior.net.messages;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class AviorBatchUpdate implements AviorPacket {
	private List<AviorSingleMessage> packets;
	private Header header;
	private Logger logger = Logger.getLogger(getClass());
	
	
	public AviorBatchUpdate() {
		super();		
	}
	
	@Override
	public void persist(){
		try{
			for(AviorSingleMessage m : this.packets){
				m.persist();
			}
		}catch(Exception e){
			logger.debug(e.getMessage());
		}		
	}
	
	public int NumPaq(Object o){
		return 0;
	}
	
	public List<AviorSingleMessage> getPackets() {
		return packets;
	}
	public void setPackets(List<AviorSingleMessage> packets) {
		this.packets = packets;
	}
	public Header getHeader() {
		return header;
	}
	
	
	@Override
	public void setHeader(Header header) {
		this.header = header;
	}

	@Override
	public void parseData(Object o, int offset) throws IllegalArgumentException {
		byte[] data = (byte[])o;
		
		if((this.header.getLength()%21) != 0){
			throw new IllegalArgumentException("Longitud incorrecta");
		}
		byte[] tmp = new byte[21];
		this.packets = new ArrayList<AviorSingleMessage>();
		for(int i=0; i<(this.header.getLength()/21); i++){
			System.arraycopy(data, 21*i, tmp, 0, 21);
			this.packets.add(new AviorSingleMessage(this.header, tmp));
		}
	}
	
	
}
