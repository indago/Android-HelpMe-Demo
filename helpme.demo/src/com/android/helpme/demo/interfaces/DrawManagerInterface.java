package com.android.helpme.demo.interfaces;

public interface DrawManagerInterface {
	public enum DRAWMANAGER_TYPE {
		SEEKER, HELPER, LOGIN, MAP, HELPERCOMMING, SWITCHER,HISTORY;
	}
	public void drawThis(Object object);

}
