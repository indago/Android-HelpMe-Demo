package com.indago.helpme.gui;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.android.helpme.demo.interfaces.DrawManagerInterface;
import com.android.helpme.demo.interfaces.UserInterface;
import com.android.helpme.demo.manager.HistoryManager;
import com.android.helpme.demo.manager.MessageOrchestrator;
import com.android.helpme.demo.manager.PositionManager;
import com.android.helpme.demo.manager.RabbitMQManager;
import com.android.helpme.demo.manager.UserManager;
import com.android.helpme.demo.rabbitMQ.RabbitMQService;
import com.android.helpme.demo.utils.ThreadPool;
import com.android.helpme.demo.utils.User;
import com.indago.helpme.R;

public class HelpMeApp extends ATemplateActivity implements OnItemClickListener, DrawManagerInterface {
	private static final String LOGTAG = HelpMeApp.class.getSimpleName();

	private TabHost mTabHost;
	private Handler mHandler;
	private MessageOrchestrator mOrchestrator;

	/*
	 * 
	 */

	private ListView lvHelpER;
	private ListView lvHelpEE;

	//	private List<LogInItem> helpERItems;
	//	private List<LogInItem> helpEEItems;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_tabhost);

		mHandler = new Handler();

		initBackend();
	}

	private void initBackend() {
		ThreadPool.getThreadPool(10);

		Intent intent = new Intent(this, RabbitMQService.class);
		startService(intent);

		mOrchestrator = MessageOrchestrator.getInstance();
		mOrchestrator.addDrawManager(DRAWMANAGER_TYPE.LOGIN, this);

		mOrchestrator.listenToMessageSystem(RabbitMQManager.getInstance());
		mOrchestrator.listenToMessageSystem(PositionManager.getInstance(this));
		mOrchestrator.listenToMessageSystem(UserManager.getInstance());
		mOrchestrator.listenToMessageSystem(HistoryManager.getInstance());

	}

	private void initTabs(ArrayList<UserInterface> helpERList, ArrayList<UserInterface> helpEEList) {
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

		LogInListAdapter adapterHelpER = new LogInListAdapter(getApplicationContext(), R.layout.list_item_picture_text, helpERList);
		LogInListAdapter adapterHelpEE = new LogInListAdapter(getApplicationContext(), R.layout.list_item_picture_text, helpEEList);

		lvHelpER = (ListView) findViewById(R.id.lv_tab1);
		lvHelpEE = (ListView) findViewById(R.id.lv_tab2);

		lvHelpER.setAdapter(adapterHelpER);
		lvHelpEE.setAdapter(adapterHelpEE);

		lvHelpER.setOnItemClickListener(this);
		lvHelpEE.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		LogInItem item = (LogInItem) parent.getItemAtPosition(position);

		if(item.isHelper()) {
			startHelpERActivity();
		} else {
			startHelpEEActivity();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		ThreadPool.runTask(RabbitMQManager.getInstance().bindToService(this));
		ThreadPool.runTask(UserManager.getInstance().readUserChoice(getApplicationContext()));
	}

	@Override
	protected void onDestroy() {
		MessageOrchestrator.getInstance().removeDrawManager(DRAWMANAGER_TYPE.LOGIN);
		super.onDestroy();
	}

	@Override
	public void drawThis(Object object) {
		if(object instanceof User) {
			User user = (User) object;
			if(user.getHelfer()) {
				mHandler.post(startHelpERActivity());
			} else {
				mHandler.post(startHelpEEActivity());
			}
		} else if(object instanceof ArrayList<?>) {
			ArrayList<User> tmpList = (ArrayList<User>) object;
			final ArrayList<UserInterface> helpERList = new ArrayList<UserInterface>();
			final ArrayList<UserInterface> helpEEList = new ArrayList<UserInterface>();
			for(UserInterface user : tmpList) {
				if(user.getHelfer()) {
					helpERList.add(user);
				} else {
					helpEEList.add(user);
				}
			}

			mHandler.post(new Runnable() {
				@Override
				public void run() {
					initTabs(helpERList, helpEEList);
				}
			});
		}
	}

	private Runnable startHelpERActivity() {
		return new Runnable() {

			@Override
			public void run() {
				startActivity(new Intent(getBaseContext(), com.indago.helpme.gui.dashboard.HelpERDashboardActivity.class));
			}
		};

	}

	private Runnable startHelpEEActivity() {
		return new Runnable() {

			@Override
			public void run() {
				startActivity(new Intent(getBaseContext(), com.indago.helpme.gui.dashboard.HelpEEDashboardActivity.class));
			}
		};

	}
}
