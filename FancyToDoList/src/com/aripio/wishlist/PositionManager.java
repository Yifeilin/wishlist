package com.aripio.wishlist;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.widget.Toast;

public class PositionManager {
	private Context context;
	private LocationManager locationManager;
	private Location currentLocation;
	private double longitude;
	private double latitude;
	private String addressString = "No address found";
	private Criteria criteria;
	
	//constructor
	public PositionManager(Context Ct){
		context = Ct;
		locationManager = (LocationManager)Ct.getSystemService(Context.LOCATION_SERVICE);
		
		criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
		
	}
	
	public Location getCurrentLocation(){
		String provider = locationManager.getBestProvider(criteria, true);
		try{
			currentLocation = locationManager.getLastKnownLocation(provider);
		}
		
		catch(SecurityException e){
			Toast.makeText(context, "no suitable permission is present for the provider.", Toast.LENGTH_LONG);
			return null;
		}
		
		if (currentLocation != null){
			latitude = currentLocation.getLatitude();
			longitude = currentLocation.getLongitude();
			return currentLocation;
		}
		
		else{
			return null;
		}
		


		
	}
	
	public String getCuttentAddStr(){
		Geocoder gc = new Geocoder(context, Locale.getDefault());
		try {
			List<Address> addresses = gc.getFromLocation(latitude, longitude, 1);
			StringBuilder sb = new StringBuilder();
			if (addresses.size() > 0) {
				Address address = addresses.get(0);
				for (int i = 0; i < address.getMaxAddressLineIndex()+1; i++)
					sb.append(address.getAddressLine(i)).append("\n");
				//sb.append(address.getLocality()).append("\n");
				//sb.append(address.getPostalCode()).append("\n");
				//sb.append(address.getCountryName());
			}
			addressString = sb.toString();
			return addressString;
		} catch (IOException e) {
			addressString = "No address found";
			return addressString;			
		}
		
	}
	

}
