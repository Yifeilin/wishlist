package com.aripio.wishlist;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

/**
 * Displays a custom map which shows our current location and the location where
 * the photo was taken.
 */
public class WishListNewMap extends MapActivity {
	private MapView mMapView;

	private MyLocationOverlay mMyLocationOverlay;

	private WishListOverlay mWishListOverlay;

	private Location myLocation;

	private GeoPoint myCurrentPoint;

	private Drawable mMarker;

	private int mMarkerXOffset;

	private int mMarkerYOffset;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		FrameLayout frame = new FrameLayout(this);
		mMapView = new MapView(this, "0f-k8vBkc4Y8OELOk1fFXUmKHOlpDPr9WxJNdqw");
		frame.addView(mMapView, new FrameLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		setContentView(frame);

		mMyLocationOverlay = new MyLocationOverlay(this, mMapView);
		mMyLocationOverlay.enableMyLocation();
		mMyLocationOverlay.enableCompass();

		mMarker = getResources().getDrawable(R.drawable.map_pin);

		// Make sure to give mMarker bounds so it will draw in the overlay
		final int intrinsicWidth = mMarker.getIntrinsicWidth();
		final int intrinsicHeight = mMarker.getIntrinsicHeight();
		mMarker.setBounds(0, 0, intrinsicWidth, intrinsicHeight);

		mMarkerXOffset = -(intrinsicWidth / 2);
		mMarkerYOffset = -intrinsicHeight;

		// Read the item we are displaying from the intent, along with the
		// parameters used to set up the map
		Intent i = getIntent();

		myLocation = getCurrentLocation((LocationManager) getSystemService(Context.LOCATION_SERVICE));
		myCurrentPoint = new GeoPoint(
				(int) (myLocation.getLatitude() * 1000000), (int) (myLocation
						.getLongitude() * 1000000));

		// int mapZoom = 15;
		// int mapLatitudeE6 = (int)(myLocation.getLatitude()*1000000);
		// int mapLongitudeE6 = (int)(myLocation.getLongitude()*1000000);

		final List<Overlay> overlays = mMapView.getOverlays();
		overlays.add(mMyLocationOverlay);
		mWishListOverlay = new WishListOverlay(mMarker);
		overlays.add(mWishListOverlay);
		overlays.add(new MarkerOverlay());

		final MapController controller = mMapView.getController();
		// if (mapZoom != Integer.MIN_VALUE && mapLatitudeE6 !=
		// Integer.MIN_VALUE
		// && mapLongitudeE6 != Integer.MIN_VALUE) {
		// controller.setZoom(mapZoom);
		// controller.setCenter(new GeoPoint(mapLatitudeE6, mapLongitudeE6));
		// controller.setCenter(mMyLocationOverlay.getMyLocation());
		// } else
		{
			controller.setZoom(15);
			mMyLocationOverlay.runOnFirstFix(new Runnable() {
				public void run() {
					controller.animateTo(mMyLocationOverlay.getMyLocation());
				}
			});
		}

		mMapView.setClickable(true);
		mMapView.setEnabled(true);
		mMapView.setSatellite(false);
		mMapView.setTraffic(false);
		mMapView.setStreetView(false);
		addZoomControls(frame);

		new NetworkThread(myCurrentPoint, mWishListOverlay).start();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mMyLocationOverlay.enableMyLocation();
	}

	@Override
	protected void onStop() {
		mMyLocationOverlay.disableMyLocation();
		super.onStop();
	}

	/**
	 * Get the zoom controls and add them to the bottom of the map
	 */
	private void addZoomControls(FrameLayout frame) {
		View zoomControls = mMapView.getZoomControls();

		FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				Gravity.BOTTOM + Gravity.CENTER_HORIZONTAL);
		frame.addView(zoomControls, p);
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	/**
	 * @return the current location
	 */
	private Location getCurrentLocation(LocationManager lm) {
		Location l = lm.getLastKnownLocation("gps");
		if (null != l) {
			return l;
		}

		// getLastKnownLocation returns null if loc provider is not enabled
		l = new Location("gps");
		l.setLatitude(40.88301);
		l.setLatitude(-72.9795);
		return l;
	}

	/**
	 * Custom overlay to display the Panoramio pushpin
	 */
	public class WishListOverlay extends ItemizedOverlay<OverlayItem> {
		private List<OverlayItem> items = new ArrayList<OverlayItem>();
		private Drawable marker = null;

		public WishListOverlay(Drawable marker) {
			super(marker);
			this.marker = marker;
			Location myLocation = getCurrentLocation((LocationManager) getSystemService(Context.LOCATION_SERVICE));
			GeoPoint myCurrentPoint = new GeoPoint((int) (myLocation
					.getLatitude() * 1000000),
					(int) (myLocation.getLongitude() * 1000000));

			addOverlay(new OverlayItem(myCurrentPoint, "A", "B"));
			boundCenterBottom(marker);
			populate();
		}

		public void addOverlay(OverlayItem overlay) {
			items.add(overlay);
			populate();
		}

		@Override
		protected OverlayItem createItem(int i) {
			return (items.get(i));
		}

		@Override
		protected boolean onTap(int i) {
			return (true);
		}

		@Override
		public int size() {
			return (items.size());
		}
	}

	public class MarkerOverlay extends Overlay {
		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			if (!shadow) {
				Point point = new Point();
				Projection p = mapView.getProjection();
				p.toPixels(myCurrentPoint, point);
				super.draw(canvas, mapView, shadow);
				drawAt(canvas, mMarker, point.x + mMarkerXOffset, point.y
						+ mMarkerYOffset, shadow);
			}
		}
	}

	/**
	 * This thread does the actual work of downloading and parsing data.
	 * 
	 */
	private class NetworkThread extends Thread {

		private final String query_URL = "//maps.googleapis.com/maps/api/place/search/json?location=%f,%f&radius=500&types=food&name=store"
				+ "&sensor=true&key=AIzaSyDFtnjfv8bJ8uCU-02J1x8XQ-jFWzhKICE";
		private GeoPoint searchPoint;
		private WishListOverlay mWishListOverlay;

		NetworkThread(GeoPoint point, WishListOverlay wishListOverlay) {
			searchPoint = point;
			mWishListOverlay = wishListOverlay;
		}

		@Override
		public void run() {

			String url = query_URL;
			url = String.format(url,
					(double) searchPoint.getLatitudeE6() / 1000000,
					(double) searchPoint.getLongitudeE6() / 1000000);
			try {
				URI uri = new URI("https", url, null);
				HttpGet get = new HttpGet(uri);

				HttpClient client = new DefaultHttpClient();
				HttpResponse response = client.execute(get);
				HttpEntity entity = response.getEntity();
				String str = convertStreamToString(entity.getContent());
				JSONObject json = new JSONObject(str);
				parse(json);
			} catch (Exception e) {
				Log.e(WishList.LOG_TAG, e.toString());
			}
		}

		private void parse(JSONObject json) {
			try {
				JSONArray array = json.getJSONArray("results");
				int count = array.length();
				for (int i = 0; i < 5; i++) {
					JSONObject obj = array.getJSONObject(i);
					JSONObject gobj = obj.getJSONObject("geometry");
					JSONObject lobj = gobj.getJSONObject("location");
					double latitude = lobj.getDouble("lat");
					double longitude = lobj.getDouble("lng");
					GeoPoint point = new GeoPoint((int) (latitude * 1000000),
							(int) (longitude * 1000000));
					mWishListOverlay
							.addOverlay(new OverlayItem(point, "A", "B"));
				}
			} catch (JSONException e) {
				Log.e(WishList.LOG_TAG, e.toString());
			}
		}

		private String convertStreamToString(InputStream is) {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is), 8 * 1024);
			StringBuilder sb = new StringBuilder();

			String line = null;
			try {
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return sb.toString();
		}

	}

}
