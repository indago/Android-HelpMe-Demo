/**
 * 
 */
package com.indago.helpme.gui2;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;

import com.android.helpme.demo.interfaces.DrawManagerInterface;
import com.android.helpme.demo.manager.HistoryManager;
import com.android.helpme.demo.manager.MessageOrchestrator;
import com.android.helpme.demo.utils.Task;
import com.android.helpme.demo.utils.User;
import com.android.helpme.demo.utils.position.Position;
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
public class HistoryActivity extends MapActivity implements DrawManagerInterface {
	private List<Overlay> mapOverlays;
	private MyItemnizedOverlay overlay;
	private MapController mapController;
	private Handler handler;
	private Drawable blue_marker;

	/**
	 * 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//		setContentView(R.layout.foundhelper);
		setContentView(R.layout.maps);
		MapView mapView = (MapView) findViewById(R.id.mapview);
		handler = new Handler();

		mapView.setBuiltInZoomControls(true);

		mapOverlays = mapView.getOverlays();

		blue_marker = this.getResources().getDrawable(R.drawable.androidmarker_blue);
		blue_marker.setBounds(0, 0, blue_marker.getIntrinsicWidth(), blue_marker.getIntrinsicHeight());

		overlay = new MyItemnizedOverlay(blue_marker, this);

		mapController = mapView.getController();
		mapOverlays.add(overlay);

		MessageOrchestrator.getInstance().addDrawManager(DRAWMANAGER_TYPE.HISTORY, this);
		handler.post(HistoryManager.getInstance().loadHistory(getApplicationContext()));
	}

	private Runnable addMarker(final ArrayList<JSONObject> jsonObjects) {
		return new Runnable() {
			@Override
			public void run() {
				for(JSONObject jsonObject : jsonObjects) {
					Position position = new Position((JSONObject) jsonObject.get(Task.START_POSITION));
					User user = new User((JSONObject) jsonObject.get(Task.USER));
					String snippet = new String();

					snippet = "HISTORY SNIPPED";

					OverlayItem overlayitem = new OverlayItem(position.getGeoPoint(), user.getName(), snippet);
					overlay.addOverlay(overlayitem);
				}
				setZoomLevel();
			}
		};
	}

	private void setZoomLevel() {

		if(overlay.size() > 1) {
			int minLatitude = Integer.MAX_VALUE;
			int maxLatitude = Integer.MIN_VALUE;
			int minLongitude = Integer.MAX_VALUE;
			int maxLongitude = Integer.MIN_VALUE;

			for(OverlayItem item : overlay.getItems()) {
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

		} else if(overlay.size() == 1) {
			OverlayItem item = overlay.getItem(0);
			mapController.animateTo(item.getPoint());
			while(mapController.zoomIn()) {

			}
			mapController.zoomOut();
		}
	}

	@Override
	public void drawThis(Object object) {
		if(object instanceof ArrayList<?>) {
			ArrayList<JSONObject> arrayList = (ArrayList<JSONObject>) object;
			handler.post(addMarker(arrayList));
		}

	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, HelperActivity.class);
		MessageOrchestrator.getInstance().removeDrawManager(DRAWMANAGER_TYPE.HISTORY);
		startActivity(intent);
		finish();
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

}
