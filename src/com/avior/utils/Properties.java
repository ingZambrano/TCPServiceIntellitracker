package com.avior.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

public class Properties {
	private static Logger logger = Logger.getLogger("CargaPropiedades");
	private static Map<String, String> props = new HashMap<String, String>();
	
	
	public static void loadProperties(){
		try{			
			ResourceBundle rb = ResourceBundle.getBundle("mysql");			
			for(String s : rb.keySet()){				
				props.put(s, rb.getString(s));
			}
		}catch(NullPointerException ex){
			ex.printStackTrace();
			logger.error(ex.getMessage());
		}
	}
	
	public static String getProperty(String name){		
		return props.get(name);
	}

	public static Map<String, String> getProps() {
		return props;
	}
}
