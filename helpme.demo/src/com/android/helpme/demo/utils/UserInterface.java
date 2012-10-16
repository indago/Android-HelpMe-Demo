package com.android.helpme.demo.utils;

import org.json.simple.JSONObject;

import com.android.helpme.demo.utils.position.Position;
import com.android.helpme.demo.utils.position.PositionInterface;
import com.google.android.maps.GeoPoint;

public interface UserInterface {
	
	public String getId();

	public String getName();

	public Boolean getHelfer();

	public Position getPosition();

	public void setPosition(Position position);

	public JSONObject getJsonObject();
	
	public GeoPoint getGeoPoint();
	
	public void updatePosition(Position position);

}