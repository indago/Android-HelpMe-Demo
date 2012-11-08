package com.indago.helpme.gui;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnSystemUiVisibilityChangeListener;
import android.view.Window;
import android.view.WindowManager;

public abstract class ATemplateActivity extends Activity implements OnSystemUiVisibilityChangeListener {
	private static final String LOGTAG = ATemplateActivity.class.getSimpleName();
	protected static DisplayMetrics metrics = new DisplayMetrics();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);

		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onStart() {
		super.onStart();

		getWindow().getDecorView().getRootView().setOnSystemUiVisibilityChangeListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();

		setDefaulAppearance();
	}

	@Override
	public void onSystemUiVisibilityChange(int visibility) {
		if (visibility != View.SYSTEM_UI_FLAG_LOW_PROFILE) {
			Log.d(LOGTAG, "OTHER VISIBILITY " + visibility);
			setDefaulAppearance();
		}
	}

	protected void setDefaulAppearance() {
		getWindow().getDecorView().getRootView()
				.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
	}


}
