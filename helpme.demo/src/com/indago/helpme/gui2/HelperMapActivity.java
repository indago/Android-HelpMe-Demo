/**
 * 
 */
package com.indago.helpme.gui2;

import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.android.helpme.demo.interfaces.DrawManagerInterface;
import com.android.helpme.demo.interfaces.UserInterface;
import com.android.helpme.demo.manager.HistoryManager;
import com.android.helpme.demo.manager.MessageOrchestrator;
import com.android.helpme.demo.manager.UserManager;
import com.android.helpme.demo.utils.Task;
import com.android.helpme.demo.utils.User;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.indago.helpme.R;

/**
 * @author Andreas Wieland
 * 
 */
public class HelperMapActivity extends MapActivity implements DrawManagerInterface {
	private List<Overlay> mapOverlays;
	private MyItemnizedOverlay overlay;
	private MapController mapController;
	private Handler handler;
	private HashMap<String, OverlayItem> map;
	private Drawable green_marker;
	private Drawable red_marker;
	private Drawable blue_marker;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maps);
		MapView mapView = (MapView) findViewById(R.id.mapview);
		handler = new Handler();

		mapView.setBuiltInZoomControls(true);

		map = new HashMap<String, OverlayItem>();

		mapOverlays = mapView.getOverlays();

		green_marker = this.getResources().getDrawable(R.drawable.androidmarker_green);
		green_marker.setBounds(0, 0, green_marker.getIntrinsicWidth(), green_marker.getIntrinsicHeight());
		red_marker = this.getResources().getDrawable(R.drawable.androidmarker_red);
		red_marker.setBounds(0, 0, red_marker.getIntrinsicWidth(), red_marker.getIntrinsicHeight());
		blue_marker = this.getResources().getDrawable(R.drawable.androidmarker_blue);
		blue_marker.setBounds(0, 0, blue_marker.getIntrinsicWidth(), blue_marker.getIntrinsicHeight());

		overlay = new MyItemnizedOverlay(green_marker, this);

		mapController = mapView.getController();
		mapOverlays.add(overlay);

		MessageOrchestrator.getInstance().addDrawManager(DRAWMANAGER_TYPE.MAP, this);
	}

	private Runnable addMarkerThisUser() {
		return new Runnable() {

			@Override
			public void run() {
				try {
					wait(2000);
					if(UserManager.getInstance().thisUser().getGeoPoint() != null) {
						handler.post(addMarker(UserManager.getInstance().getThisUser()));
					}
				} catch(InterruptedException e) {
					Log.i(HelperMapActivity.class.getSimpleName(), e.toString());
				}

			}
		};
	}

	private Runnable addMarker(final UserInterface userInterface) {
		return new Runnable() {
			@Override
			public void run() {
				OverlayItem overlayitem = map.get(userInterface.getId());
				if(overlayitem != null) {
					overlay.removeItem(overlayitem);
				}

				if(userInterface.getId().equalsIgnoreCase(UserManager.getInstance().thisUser().getId())) {
					overlayitem = new OverlayItem(userInterface.getGeoPoint(), userInterface.getName(), "Sie");
					overlayitem.setMarker(green_marker);

				} else {
					// not us but
					if(userInterface.isHelper()) { // a helper
						overlayitem = new OverlayItem(userInterface.getGeoPoint(), userInterface.getName(), "ein Helfer");
						overlayitem.setMarker(blue_marker);

					} else {// a help seeker
						overlayitem = new OverlayItem(userInterface.getGeoPoint(), userInterface.getName(), "ein Hilfesuchender");
						overlayitem.setMarker(red_marker);

					}
				}

				map.put(userInterface.getId(), overlayitem);
				overlay.addOverlay(overlayitem);
				setZoomLevel();
			}
		};
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public void drawThis(Object object) {
		if(object instanceof User) {
			User user = (User) object;
			handler.post(addMarker(user));
		} else if(object instanceof Task) {
			Task task = (Task) object;
			handler.post(showInRangeMessageBox(this));
		}

	}

	private void setZoomLevel() {
		Object[] keys = map.keySet().toArray();
		OverlayItem item;
		if(keys.length > 1) {
			int minLatitude = Integer.MAX_VALUE;
			int maxLatitude = Integer.MIN_VALUE;
			int minLongitude = Integer.MAX_VALUE;
			int maxLongitude = Integer.MIN_VALUE;

			for(Object key : keys) {
				item = map.get((String) key);
				GeoPoint p = item.getPoint();
				int lati = p.getLatitudeE6();
				int lon = p.getLongitudeE6();

				maxLatitude = Math.max(lati, maxLatitude);
				minLatitude = Math.min(lati, minLatitude);
				maxLongitude = Math.max(lon, maxLongitude);
				minLongitude = Math.min(lon, minLongitude);
			}
			mapController.zoomToSpan(Math.abs(maxLatitude - minLatitude), Math.abs(maxLongitude - minLongitude));
			mapController.animateTo(new GeoPoint((maxLatitude + minLatitude) / 2, (maxLongitude + minLongitude) / 2));

		} else {
			String key = (String) keys[0];
			item = map.get(key);
			mapController.animateTo(item.getPoint());
			while(mapController.zoomIn()) {

			}
			mapController.zoomOut();
		}
	}

	private Runnable showInRangeMessageBox(final Context context) {
		return new Runnable() {

			@Override
			public void run() {
				AlertDialog.Builder dlgAlert = new AlertDialog.Builder(context);
				dlgAlert.setTitle("SEEKER IN RANGE");
				dlgAlert.setMessage("SEEKER IN RANGE TEXT");
				dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(context, HelperActivity.class);
						HistoryManager.getInstance().stopTask();
						HistoryManager.getInstance().saveHistory(getApplicationContext());
						MessageOrchestrator.getInstance().removeDrawManager(DRAWMANAGER_TYPE.MAP);
						startActivity(intent);
						finish();
					}

				});
				AlertDialog dialog = dlgAlert.create();
				try {
					dialog.show();
				} catch(Exception exception) {
					Log.e(HelperMapActivity.class.getSimpleName(), exception.toString());
				}
			}
		};
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, HelperActivity.class);
		HistoryManager.getInstance().stopTask();
		HistoryManager.getInstance().saveHistory(getApplicationContext());
		MessageOrchestrator.getInstance().removeDrawManager(DRAWMANAGER_TYPE.MAP);
		startActivity(intent);
		finish();
	}
}
