package com.android.helpme.demo.gui;

import java.util.ArrayList;

import com.android.helpme.demo.R;
import com.android.helpme.demo.R.id;
import com.android.helpme.demo.R.layout;
import com.android.helpme.demo.gui.DrawManager.DRAWMANAGER_TYPE;
import com.android.helpme.demo.manager.HistoryManager;
import com.android.helpme.demo.manager.MessageOrchestrator;
import com.android.helpme.demo.manager.PositionManager;
import com.android.helpme.demo.manager.RabbitMQManager;
import com.android.helpme.demo.manager.UserManager;
import com.android.helpme.demo.manager.interfaces.MessageOrchestratorInterface;
import com.android.helpme.demo.rabbitMQ.RabbitMQService;
import com.android.helpme.demo.utils.ThreadPool;
import com.android.helpme.demo.utils.User;
import com.android.helpme.demo.utils.UserInterface;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class LoginActivity extends Activity implements DrawManager{
	private ListView listView;
	public static ArrayAdapter<String> adapter;
	private ArrayList<String> data;
	private ArrayList<User> list;
	private Handler uihandler;


	//TODO
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.foundhelper);
		this.setTitle(R.string.title_activity_login);
		
//		Intent intent = new Intent(this, MyService.class);
//		this.startService(intent);

		uihandler = new Handler();
		listView = (ListView) findViewById(R.id.foundHelper);
		data = new ArrayList<String>();
		init();
		
		uihandler.post(showMessageBox("Select", "Bitte wählen sie einen Benutzer aus", this));
	}

	private void init(){
		MessageOrchestrator.getInstance().addDrawManager(DRAWMANAGER_TYPE.LOGIN, this);
		
		adapter = new ArrayAdapter<String>(this, R.layout.simplerow,data);
		listView.setAdapter(adapter);
		wireItemClick(this);
		list = UserManager.getInstance().getUsers();
		if (list.size() == 0) {
			ThreadPool.runTask(UserManager.getInstance().readUserFromProperty(getApplicationContext()));
		}
		else {
			uihandler.post(addUser());
		}
	}
	
	private void wireItemClick(final Context context) {
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				String[] name = adapter.getItem(position).split(":");
				UserInterface user = null;
				for (User userTemp : list) {
					String string = name[1];
					string = string.trim();
					if (userTemp.getName().equalsIgnoreCase(string)) {
						user = userTemp;
					}
				}
				if (user != null) {
					ThreadPool.runTask(UserManager.getInstance().setThisUser(user, getApplicationContext()));
					
					if (user.getHelfer()) {
						Intent myIntent = new Intent(context, HelperActivity.class);
						startActivity(myIntent);
						finish();
					}else {
						Intent myIntent = new Intent(context, SeekerActivity.class);
						startActivity(myIntent);
						finish();
					}
					return;
				}
			}
		});
	}

	private Runnable showMessageBox(final String title,final String text,final Context context){
		return new Runnable() {
			
			@Override
			public void run() {
				AlertDialog.Builder dlgAlert = new AlertDialog.Builder(context);
				dlgAlert.setTitle(title);
				dlgAlert.setMessage(text);
				dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
					}

				});
				AlertDialog dialog = dlgAlert.create();
				dialog.show();
			}
		};
		
	}
	
	private Runnable addUser(){
		return new Runnable() {
			
			@Override
			public void run() {
				for (UserInterface user : list) {
					if (adapter.getPosition(user.toString()) != 0) {
						adapter.add(user.toString());
					}
				}
			}
		};
	}

	@Override
	public void drawThis(Object object) {
		if (object instanceof ArrayList<?>) {
			list = (ArrayList<User>) object;
			uihandler.post(addUser());
		}
	}
	
	public void onBackPressed() {};
	
	@Override
	protected void onDestroy() {
		MessageOrchestrator.getInstance().removeDrawManager(DRAWMANAGER_TYPE.LOGIN);
		super.onDestroy();
	}
}
