package com.avior.utils;

public class SphericalMercator {
	private static final double radius = 6378137;
	private static double D2R = Math.PI / 180;
	private static double HALF_PI = Math.PI / 2;
	
	public static Point fromLonLat(double lon, double lat){
		double lonRadians = (D2R * lon);
        double latRadians = (D2R * lat);

        double x = radius * lonRadians;        
        double y = radius * Math.log(Math.tan(Math.PI * 0.25 + latRadians * 0.5));

        return new Point(x, y);
	} 
	
	public static Point toLonLat(double x, double y){
		double ts;		
        ts = Math.exp(-y / (radius));
        double latRadians = HALF_PI - 2 * Math.atan(ts);

        double lonRadians = x / (radius);

        double lon = (lonRadians / D2R);
        double lat = (latRadians / D2R);

        return new Point(lon, lat);
	}
}
