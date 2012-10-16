/**
 * 
 */
package com.android.helpme.demo.manager;

import java.util.ArrayList;

import com.android.helpme.demo.manager.interfaces.PositionManagerInterface;
import com.android.helpme.demo.messagesystem.AbstractMessageSystem;
import com.android.helpme.demo.messagesystem.AbstractMessageSystemInterface;
import com.android.helpme.demo.messagesystem.InAppMessage;
import com.android.helpme.demo.messagesystem.MESSAGE_TYPE;
import com.android.helpme.demo.utils.User;
import com.android.helpme.demo.utils.position.Position;
import com.android.helpme.demo.utils.position.PositionInterface;
import com.android.helpme.demo.utils.position.SimpleSelectionStrategy;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

/**
 * @author Andreas Wieland
 * 
 */
public class PositionManager extends AbstractMessageSystem implements PositionManagerInterface {
	private static final String LOGTAG = PositionManager.class.getSimpleName();
	private InAppMessage message;
	private static PositionManager manager;
	private LocationManager locationManager;
	private Location lastLocation;
	private boolean started;

	/**
	 * 
	 */
	private PositionManager(Activity activity) {
		locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
		lastLocation = null;
		started = false;
	}

	public static PositionManagerInterface getInstance() {
		return manager;
	}

	public static PositionManager getInstance(Activity activity) {
		if (manager == null) {
			manager = new PositionManager(activity);
		}
		return manager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indago.android.demo.messagesystem.AbstractMessageSystem#getLogTag()
	 */
	@Override
	public String getLogTag() {
		return LOGTAG;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indago.android.demo.messagesystem.AbstractMessageSystem#getMessage()
	 */
	@Override
	protected InAppMessage getMessage() {
		return message;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indago.android.demo.messagesystem.AbstractMessageSystem#setMessage
	 * (com.indago.android.demo.messagesystem.InAppMessage)
	 */
	@Override
	protected void setMessage(InAppMessage inAppMessage) {
		this.message = inAppMessage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.indago.android.demo.messagesystem.AbstractMessageSystem#getManager()
	 */
	@Override
	public AbstractMessageSystemInterface getManager() {
		return manager;
	}

	@Override
	public void onLocationChanged(Location location) {
		if (!SimpleSelectionStrategy.isPositionRelevant(location)) {
			return;
		}
//		if (lastLocation != null && !SimpleSelectionStrategy.isPositionRelevant(lastLocation, location)) {
//			return;
//		}
		lastLocation = location;
		PositionInterface wayPointData = new Position(location);
		Log.i(getLogTag(), "new Location arrived");
		
		fireMessageFromManager(wayPointData, MESSAGE_TYPE.LOCATION);
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.android.helpme.demo.manager.PositionManagerInterface#startLocationTracking()
	 */
	@Override
	public Runnable startLocationTracking() {
		return new Runnable() {

			@Override
			public void run() {
				if (started) {
					return;
				}
//				if (Looper.myLooper() == null) {
//					Looper.prepare();
//				}
//				;

				lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				if (lastLocation != null && SimpleSelectionStrategy.isPositionRelevant(lastLocation)) {
					fireMessageFromManager(new Position(lastLocation), MESSAGE_TYPE.LOCATION);
				} else {
					Log.i(LOGTAG, "requesting Location");
//					locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, manager);
					locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, manager);
					started = true;
					// Looper.myLooper().quit();
				}

			}
		};

	}

	/* (non-Javadoc)
	 * @see com.android.helpme.demo.manager.PositionManagerInterface#stopLocationTracking()
	 */
	@Override
	public Runnable stopLocationTracking() {
		return new Runnable() {

			@Override
			public void run() {
				if (!started) {
					return;
				}
				if (Looper.myLooper() == null) {
					Looper.prepare();
				}
				;

				locationManager.removeUpdates(manager);
				Looper.myLooper().quit();
				started = false;
			}
		};

	}

	/* (non-Javadoc)
	 * @see com.android.helpme.demo.manager.PositionManagerInterface#isStarted()
	 */
	@Override
	public boolean isStarted() {
		return started;
	}

}
