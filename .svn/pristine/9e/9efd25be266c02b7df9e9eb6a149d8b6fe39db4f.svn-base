package com.avior.utils;

public class BCD {
	
	public static byte lrc(byte[] data, int start, int end){
		byte lrc = 0;
		for(int pos=start; pos<=end;pos++)
			lrc ^= data[pos];
		return lrc;
	}
	
	public static String asString(byte[] array){
		StringBuffer buf = new StringBuffer(array.length * 2);
		for(int i = 0; i<array.length ; ++i){
			buf.append((char) (((array[i] & 0xf0) >> 4) + '0'));
			if((i != array.length) && ((array[i] & 0xf) != 0x0A)){
				buf.append((char) ((array[i] & 0x0f) + '0'));
			}
		}
		return buf.toString();
	}
	
	public static int asInt(byte[] array){
		String s = asString(array);
		return Integer.parseInt(s);
	}
	
	public static String asString(byte b){
		String r = new String();
		r += (char)(((b&0xf0)>>4)+0x30);
		r += (char)((b&0x0f) + 0x30);
		return r;		
	}
	
	public static int asInt(byte b){
		return Integer.parseInt(asString(b));
	}
	
	public static String debugHexBuffer(byte[] data) {

		StringBuilder sb = new StringBuilder();
		for (byte b : data) {
			String hex = Integer.toHexString(b & 0xFF);
			if (hex.length() == 1)
				sb.append('0');
			sb.append(hex);
		}
		return sb.toString();
	}
}
