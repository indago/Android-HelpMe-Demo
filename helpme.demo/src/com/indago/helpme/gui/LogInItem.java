package com.indago.helpme.gui;

public class LogInItem {
	private int mImageID;
	private String mName;
	private boolean mHelper;

	public LogInItem(int imageID, String name, boolean helper) {
		mImageID = imageID;
		mName = name;
		mHelper = helper;
	}

	public int getImageID() {
		return mImageID;
	}

	public void setImageID(int mImageID) {
		this.mImageID = mImageID;
	}

	public String getName() {
		return mName;
	}

	public void setName(String mName) {
		this.mName = mName;
	}

	public boolean isHelper() {
		return mHelper;
	}

	public void setHelper(boolean mHelper) {
		this.mHelper = mHelper;
	}

	@Override
	public String toString() {
		String string = "";
		if(mHelper == true) {
			string += "Helper: ";
		} else {
			string += "Helpee: ";
		}

		string += mName + " ImageID: " + mImageID;
		return string;
	}

}
