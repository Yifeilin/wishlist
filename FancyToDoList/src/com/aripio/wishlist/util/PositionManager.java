package com.aripio.wishlist.util;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.Toast;
import android.os.Bundle;

public class PositionManager {
	private Context context;
	private LocationManager locationManager;
	private Location _currentBestLocation;
	private double longitude;
	private double latitude;
	private String addressString = "unknown";
	private Criteria criteria;
	private LocationListener _locationListener;
	private static final int LISTEN_INTERVAL = 1000 * 60;//1min
	
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
	
	public void startLocationUpdates(){
		// Define a listener that responds to location updates
		_locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				// Called when a new location is found by the network location provider.
				//				      makeUseOfNewLocation(location);
			}

			public void onStatusChanged(String provider, int status, Bundle extras) {}

			public void onProviderEnabled(String provider) {}

			public void onProviderDisabled(String provider) {}
		};

		// Register the listener with the Location Manager to receive location updates
		String provider = locationManager.getBestProvider(criteria, true);
		locationManager.requestLocationUpdates(provider, 0, 0, _locationListener);
		_currentBestLocation = locationManager.getLastKnownLocation(provider);
	}
	
	public void stopLocationUpdates(){
		// Remove the listener you previously added
		locationManager.removeUpdates(_locationListener);
	}
	
	public Location getCurrentLocation() {
		String provider = locationManager.getBestProvider(criteria, true);
		Location currentLocation = null;
		try {
			currentLocation = locationManager.getLastKnownLocation(provider);
		}
		
		catch(SecurityException e) {
			Toast.makeText(context, "no suitable permission is present for the provider.", Toast.LENGTH_LONG);
//			return null;
			currentLocation = null;
		}
		
		if (isBetterLocation(currentLocation, _currentBestLocation)) {
			_currentBestLocation = currentLocation;
		}
		
		if (_currentBestLocation != null){
			latitude = _currentBestLocation.getLatitude();
			longitude = _currentBestLocation.getLongitude();
			return _currentBestLocation;
		}
		
		else {
			return null;
		}
	}

	/** Determines whether one Location reading is better than the current Location fix
	  * @param location  The new Location that you want to evaluate
	  * @param currentBestLocation  The current Location fix, to which you want to compare the new one
	  */
	protected boolean isBetterLocation(Location location, Location currentBestLocation) {
	    if (currentBestLocation == null) {
	        // A new location is always better than no location
	        return true;
	    }

	    // Check whether the new location fix is newer or older
	    long timeDelta = location.getTime() - currentBestLocation.getTime();
	    boolean isSignificantlyNewer = timeDelta > LISTEN_INTERVAL;
	    boolean isSignificantlyOlder = timeDelta < -LISTEN_INTERVAL;
	    boolean isNewer = timeDelta > 0;

	    // If it's been more than two minutes since the current location, use the new location
	    // because the user has likely moved
	    if (isSignificantlyNewer) {
	        return true;
	    // If the new location is more than two minutes older, it must be worse
	    } else if (isSignificantlyOlder) {
	        return false;
	    }

	    // Check whether the new location fix is more or less accurate
	    int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
	    boolean isLessAccurate = accuracyDelta > 0;
	    boolean isMoreAccurate = accuracyDelta < 0;
	    boolean isSignificantlyLessAccurate = accuracyDelta > 200;

	    // Check if the old and new location are from the same provider
	    boolean isFromSameProvider = isSameProvider(location.getProvider(),
	            currentBestLocation.getProvider());

	    // Determine location quality using a combination of timeliness and accuracy
	    if (isMoreAccurate) {
	        return true;
	    } else if (isNewer && !isLessAccurate) {
	        return true;
	    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
	        return true;
	    }
	    return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
	    if (provider1 == null) {
	      return provider2 == null;
	    }
	    return provider1.equals(provider2);
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
			addressString = "unknown";
			return addressString;			
		}
	}
}
