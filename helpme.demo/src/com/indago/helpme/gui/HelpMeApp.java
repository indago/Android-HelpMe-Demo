package com.indago.helpme.gui;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.indago.helpme.R;

public class HelpMeApp extends ATemplateActivity implements OnItemClickListener {
	private static final String LOGTAG = HelpMeApp.class.getSimpleName();

	private TabHost mTabHost;

	/*
	 * 
	 */
	private static final String[] helpERNames = { "Martin M.", "Stefanie M.", "Andreas W." };
	private static final String[] helpEENames = { "Gerhard L.", "Hanna G.", "Gustav A." };

	private static final int[] helpERPictures = { R.drawable.help_er_martin, R.drawable.help_er_steffi, R.drawable.help_er_andreas };
	private static final int[] helpEEPictures = { R.drawable.help_ee_man1, R.drawable.help_ee_woman1, R.drawable.help_ee_man2 };

	private ListView lvHelpER;
	private ListView lvHelpEE;

	private List<LogInItem> helpERItems;
	private List<LogInItem> helpEEItems;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_tabhost);

		mTabHost = (TabHost) findViewById(R.id.tabhost);
		mTabHost.setup();

		TabSpec specHelpER = mTabHost.newTabSpec((String) getResources().getText(R.string.tab_helper));
		specHelpER.setContent(R.id.ll_tab1);
		specHelpER.setIndicator((String) getResources().getText(R.string.tab_helper));

		TabSpec specHelpEE = mTabHost.newTabSpec((String) getResources().getText(R.string.tab_helpee));
		specHelpEE.setContent(R.id.ll_tab2);
		specHelpEE.setIndicator((String) getResources().getText(R.string.tab_helpee));

		mTabHost.addTab(specHelpER);
		mTabHost.addTab(specHelpEE);

		helpERItems = new ArrayList<LogInItem>();
		helpEEItems = new ArrayList<LogInItem>();

		for(int i = 0; i < helpERNames.length; i++) {
			LogInItem item = new LogInItem(helpERPictures[i], helpERNames[i], true);
			helpERItems.add(item);
		}

		for(int i = 0; i < helpEENames.length; i++) {
			LogInItem item = new LogInItem(helpEEPictures[i], helpEENames[i], false);
			helpEEItems.add(item);
		}

		LogInListAdapter adapterHelpER = new LogInListAdapter(getApplicationContext(), R.layout.list_item_picture_text, helpERItems);
		LogInListAdapter adapterHelpEE = new LogInListAdapter(getApplicationContext(), R.layout.list_item_picture_text, helpEEItems);

		lvHelpER = (ListView) findViewById(R.id.lv_tab1);
		lvHelpEE = (ListView) findViewById(R.id.lv_tab2);

		lvHelpER.setAdapter(adapterHelpER);
		lvHelpEE.setAdapter(adapterHelpEE);

		lvHelpER.setOnItemClickListener(this);
		lvHelpEE.setOnItemClickListener(this);
	}

	//	@Override
	//	public boolean onCreateOptionsMenu(Menu menu) {
	//
	//		MenuInflater inflater = getMenuInflater();
	//		inflater.inflate(R.menu.activity_login, menu);
	//
	//		return super.onCreateOptionsMenu(menu);
	//	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		LogInItem item = (LogInItem) parent.getItemAtPosition(position);

		if(item.isHelper()) {
			simulateIncomingHelpCall(view);
			//			startHelpERProfile(view);
		} else {
			startHelpEEProfile(view);
		}
	}

	public void startHelpEEProfile(View view) {
		Log.d(LOGTAG, "Logging in...");
		startActivity(new Intent(view.getContext(), com.indago.helpme.gui.dashboard.HelpEEDashboardActivity.class));
	}

	public void simulateIncomingHelpCall(View view) {
		Log.d(LOGTAG, "Logging in...");
		startActivity(new Intent(view.getContext(), com.indago.helpme.gui.dashboard.HelpERDashboardActivity.class));
	}
}
