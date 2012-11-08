/**
 * 
 */
package com.indago.helpme.gui2;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

/**
 * @author Andreas Wieland
 * 
 */
public class MyItemnizedOverlay extends ItemizedOverlay<OverlayItem> {
	private ArrayList<OverlayItem> items;
	private Context context;

	public MyItemnizedOverlay(Drawable drawable) {
		super(boundCenterBottom(drawable));
		items = new ArrayList<OverlayItem>();
	}

	public MyItemnizedOverlay(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		this.context = context;
		items = new ArrayList<OverlayItem>();
	}

	public void addOverlay(OverlayItem overlay) {
		items.add(overlay);
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return items.get(i);
	}

	@Override
	public int size() {
		return items.size();
	}

	public ArrayList<OverlayItem> getItems() {
		return items;
	}

	@Override
	protected boolean onTap(int index) {
		OverlayItem item = items.get(index);
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setTitle(item.getTitle());
		dialog.setMessage(item.getSnippet());
		dialog.show();
		return true;
	}

	public void removeItem(OverlayItem item) {
		items.remove(item);
	}
}
