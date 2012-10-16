package com.android.helpme.demo.utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.android.helpme.demo.utils.position.Position;
import com.android.helpme.demo.utils.position.PositionInterface;
import com.android.helpme.demo.utils.position.SimpleSelectionStrategy;
import com.google.android.maps.GeoPoint;



/**
 * 
 * @author Andreas Wieland
 *
 */
public class User implements UserInterface {
	public static final String NAME = "Name";
	public static final String HELFER = "Helfer";
	public static final String POSITION = "Position";
	
	public static final String ID = "id";
	private String name;
	private String id;
	private Boolean helfer;
	private Position position; 

	public User(String id,String name, Boolean helfer) {
		this.name = name;
		this.helfer = helfer;
		this.id = id;
	}

	public User(JSONObject object) {
		this.name = (String) object.get(NAME);
		this.helfer = (Boolean) object.get(HELFER);
		this.id = (String) object.get(ID);
		if (object.get(POSITION) != null) {
			this.position = new Position(object);
		}
	}

	/* (non-Javadoc)
	 * @see com.android.helpme.demo.utils.UserInterface#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see com.android.helpme.demo.utils.UserInterface#getHelfer()
	 */
	@Override
	public Boolean getHelfer() {
		return helfer;
	}
	
	/* (non-Javadoc)
	 * @see com.android.helpme.demo.utils.UserInterface#getPosition()
	 */
	@Override
	public Position getPosition() {
		return position;
	}

	/* (non-Javadoc)
	 * @see com.android.helpme.demo.utils.UserInterface#setPosition(com.android.helpme.demo.utils.position.Position)
	 */
	@Override
	public void setPosition(Position position) {
		this.position = position;
	}
	
	
	
	public String getId() {
		return id;
	}

	
	@Override
	public String toString() {
		String string = new String();
		if (helfer) {
			string += "Helfer";
		}else {
			string += "Hilfe Suchender";
		}
		string += (" : "+ name);
		return string;
	}

	/* (non-Javadoc)
	 * @see com.android.helpme.demo.utils.UserInterface#getJsonObject()
	 */
	@Override
	public JSONObject getJsonObject() {
		JSONObject object = new JSONObject();
		object.put(NAME, name);
		object.put(HELFER, helfer);
		object.put(POSITION, position);
		object.put(ID, id);
		return object;
	}

	@Override
	public GeoPoint getGeoPoint() {
		int latitude = (int)(getPosition().getLatitude() * 1e6);
		int longitude = (int)(getPosition().getLongitude() * 1e6);
		GeoPoint point = new GeoPoint(latitude,longitude);
		return point;
	}

	@Override
	public void updatePosition(Position position) {
		if (SimpleSelectionStrategy.isPositionRelevant(getPosition(), position)) {
			
		}
		
	}
	
}
