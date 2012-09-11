package com.georges.grape.util;

import com.georges.grape.data.GeoLocation;
import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderStatus;
import com.google.code.geocoder.model.LatLng;

public class GeoCoder {
	
	private static Geocoder geocoder = new Geocoder();
	
	public static GeoLocation getGeoLocation(String address, String language)
	{
		GeocodeResponse center = geocoder
				.geocode(new GeocoderRequestBuilder()
						.setAddress(address).setLanguage(language)
						.getGeocoderRequest());
		
		if(center==null || center.getStatus()!=GeocoderStatus.OK)
		{
			return null;
		}
		
		LatLng geo=center.getResults().get(0).getGeometry().getLocation();
		return new GeoLocation(geo.getLat().doubleValue(),geo.getLng().doubleValue());
	}
}
