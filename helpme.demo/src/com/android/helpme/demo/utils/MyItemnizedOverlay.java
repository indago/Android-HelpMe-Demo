/**
 * 
 */
package com.android.helpme.demo.utils;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

/**
 * @author Andreas Wieland
 *
 */
public class MyItemnizedOverlay extends ItemizedOverlay<OverlayItem> {
	ArrayList<OverlayItem> items;

	public MyItemnizedOverlay(Drawable drawable) {
		super(drawable);
		items = new ArrayList<OverlayItem>();
	}

	public void addOverlay(OverlayItem overlay) {
		items.add(overlay);
		populate();
	}

	@Override
	protected OverlayItem createItem(int arg0) {
		return items.get(arg0);
	}

	@Override
	public int size() {
		return items.size();
	}

}
