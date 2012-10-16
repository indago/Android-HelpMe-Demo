/**
 * 
 */
package com.android.helpme.demo;

import java.util.List;

import com.android.helpme.demo.manager.UserManager;
import com.android.helpme.demo.utils.MyItemnizedOverlay;
import com.android.helpme.demo.utils.User;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;

/**
 * @author Andreas Wieland
 *
 */
public class Maps extends MapActivity{
	private List<Overlay> mapOverlays;
	private MyItemnizedOverlay overlay;
	private MapController mapController;
	private Handler handler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maps);
		MapView mapView = (MapView) findViewById(R.id.mapview);
		
		handler = new Handler();
		mapView.setBuiltInZoomControls(true);

		mapOverlays= mapView.getOverlays();
		Drawable drawable = this.getResources().getDrawable(R.drawable.androidmarker);

		overlay = new MyItemnizedOverlay(drawable);

		mapController = mapView.getController();
		mapOverlays.add(overlay);
		handler.post(addMarker());
	}

	private Runnable addMarker(){
		return new Runnable() {
			@Override
			public void run() {
				for (User user : UserManager.getInstance().getUsers()) {					
					mapController.setCenter(user.getGeoPoint());
					OverlayItem overlayitem = new OverlayItem(user.getGeoPoint(), user.getName(),"");
					overlay.addOverlay(overlayitem);
				}

			}
		};
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

}
