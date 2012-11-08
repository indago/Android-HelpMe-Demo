package com.indago.helpme.gui;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.indago.helpme.R;

public class HelpERControlcenterActivity extends Activity {

	private TabHost mTabHost;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help_er_controlcenter);

		mTabHost = (TabHost) findViewById(R.id.tabhost);
		mTabHost.setup();

		TabSpec statisticsTab = mTabHost.newTabSpec("Statistics");
		statisticsTab.setContent(R.id.ll_tab1);
		statisticsTab.setIndicator("Statistics");

		TabSpec aboutTab = mTabHost.newTabSpec("About");
		aboutTab.setContent(R.id.ll_tab2);
		aboutTab.setIndicator("About");

		mTabHost.addTab(statisticsTab);
		mTabHost.addTab(aboutTab);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.help_er_controlcenter, menu);
		return true;
	}
}
