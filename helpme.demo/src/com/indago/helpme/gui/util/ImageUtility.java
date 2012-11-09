package com.indago.helpme.gui.util;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.indago.helpme.R;

public class ImageUtility {
	private ImageUtility() {}

	public static Drawable retrieveDrawable(Context context, String identifier) {

		if(context == null) {
			return null;
		}

		Drawable drawable = context.getResources().getDrawable(R.drawable.ic_person_dummy);

		if(identifier.equalsIgnoreCase("help_ee_man3")) {
			drawable = (Drawable) context.getResources().getDrawable(R.drawable.help_ee_man3);
		} else if(identifier.equalsIgnoreCase("help_ee_man4")) {
			drawable = (Drawable) context.getResources().getDrawable(R.drawable.help_ee_man4);
		} else if(identifier.equalsIgnoreCase("help_ee_woman2")) {
			drawable = (Drawable) context.getResources().getDrawable(R.drawable.help_ee_woman2);
		} else if(identifier.equalsIgnoreCase("help_er_martin")) {
			drawable = (Drawable) context.getResources().getDrawable(R.drawable.help_er_martin);
		} else if(identifier.equalsIgnoreCase("help_er_steffi")) {
			drawable = (Drawable) context.getResources().getDrawable(R.drawable.help_er_steffi);
		} else if(identifier.equalsIgnoreCase("help_er_andreas")) {
			drawable = (Drawable) context.getResources().getDrawable(R.drawable.help_er_andreas);
		}

		return drawable;
	}
}
