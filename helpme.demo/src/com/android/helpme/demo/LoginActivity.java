package com.android.helpme.demo;

import java.util.ArrayList;

import com.android.helpme.demo.DrawManager.DRAWMANAGER_TYPE;
import com.android.helpme.demo.manager.MessageOrchestrator;
import com.android.helpme.demo.manager.PositionManager;
import com.android.helpme.demo.manager.RabbitMQManager;
import com.android.helpme.demo.manager.UserManager;
import com.android.helpme.demo.manager.interfaces.MessageOrchestratorInterface;
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
	private MessageOrchestratorInterface orchestrator;
	private Handler uihandler;

	//TODO
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.foundhelper);

		uihandler = new Handler();
		listView = (ListView) findViewById(R.id.foundHelper);
		data = new ArrayList<String>();
		init();
		uihandler.post(showNotification("Select", "Bitte wählen sie einen Benutzer aus", this));
	}

	private void init(){
		ThreadPool.getThreadPool(10);

		orchestrator = MessageOrchestrator.getInstance();
		orchestrator.setDrawManager(DRAWMANAGER_TYPE.LOGIN, this);
		orchestrator.listenToMessageSystem(RabbitMQManager.getInstance());
		orchestrator.listenToMessageSystem(PositionManager.getInstance(this));
		orchestrator.listenToMessageSystem(UserManager.getInstance());

		ThreadPool.runTask(RabbitMQManager.getInstance().connect());
		ThreadPool.runTask(UserManager.getInstance().readUserFromProperty(this));

		adapter = new ArrayAdapter<String>(this, R.layout.simplerow,data);
		for (UserInterface user : UserManager.getInstance().getUsers()) {
			adapter.add(user.toString());
		}

		listView.setAdapter(adapter);
		wireItemClick(this);
	}
	
	private void wireItemClick(final Context context) {
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				String[] name = adapter.getItem(position).split(":");
				UserInterface user = UserManager.getInstance().getUserByName(name[1]);
				if (user != null) {
					String android_id = Secure.getString(context.getContentResolver(),
                            Secure.ANDROID_ID); 
					
					UserManager.getInstance().setThisUser(user,android_id);
					
					uihandler.post(UserManager.getInstance().clear());
					
					if (user.getHelfer()) {
						Intent myIntent = new Intent(context, FoundHelperActivity.class);
						startActivity(myIntent);
					}else {
						Intent myIntent = new Intent(context, MainActivity.class);
						startActivity(myIntent);
					}
					
					return;
				}
			}
		});
	}

	private Runnable showNotification(final String title,final String text,final Context context){
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
	
	private Runnable addUser(final ArrayList<User> users){
		return new Runnable() {
			
			@Override
			public void run() {
				for (UserInterface user : users) {
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
			ArrayList<User> users = (ArrayList<User>) object;
			uihandler.post(addUser(users));
		}
	}
}
