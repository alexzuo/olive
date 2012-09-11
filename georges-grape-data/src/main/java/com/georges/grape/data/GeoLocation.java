package com.georges.grape.data;

public class GeoLocation {
	private double latitude;
	private double longitude;
	
	public GeoLocation(double lat, double lng)
	{
		latitude = lat;
		longitude = lng;
	}
	
	public double getLatitude()
	{
		return latitude;
	}
	public double getLongitude()
	{
		return longitude;
	}
	
}
