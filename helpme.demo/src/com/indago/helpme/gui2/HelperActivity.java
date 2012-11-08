package com.indago.helpme.gui2;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.helpme.demo.interfaces.DrawManagerInterface;
import com.android.helpme.demo.interfaces.UserInterface;
import com.android.helpme.demo.manager.HistoryManager;
import com.android.helpme.demo.manager.MessageOrchestrator;
import com.android.helpme.demo.manager.UserManager;
import com.android.helpme.demo.utils.ThreadPool;
import com.android.helpme.demo.utils.User;
import com.indago.helpme.R;

/**
 * 
 * @author Andreas Wieland
 * 
 */
public class HelperActivity extends Activity implements DrawManagerInterface {
	private ListView listView;
	private ArrayAdapter<com.android.helpme.demo.interfaces.UserInterface> adapter;
	private ArrayList<String> data;
	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.foundhelper);
		listView = (ListView) findViewById(R.id.foundHelper);
		handler = new Handler();

		data = new ArrayList<String>();

		adapter = new ArrayAdapter<UserInterface>(this, R.layout.simplerow);
		adapter.addAll(UserManager.getInstance().getUsers());

		listView.setAdapter(adapter);
		MessageOrchestrator.getInstance().addDrawManager(DRAWMANAGER_TYPE.LIST, this);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				UserInterface user = adapter.getItem(position);
				showPosition(user);
			}
		});
	}

	private Runnable addUser(final UserInterface user, final Context context) {
		return new Runnable() {

			@Override
			public void run() {
				adapter.add(user);
				data.add(user.getId());

				AlertDialog.Builder dlgAlert = new AlertDialog.Builder(context);
				dlgAlert.setTitle("new Help Assignment");
				dlgAlert.setMessage("Do you want to help: " + user.getName() + " ?");
				dlgAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						showPosition(user);
					}

				});

				dlgAlert.setNegativeButton("No", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});

				dlgAlert.create().show();
			}
		};
	}

	@Override
	public void drawThis(Object object) {
		if(object instanceof User) {
			if(!data.contains(((User) object).getId())) {
				handler.post(addUser((User) object, this));
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch(item.getItemId()) {
			case R.id.menu_relog:
				ThreadPool.runTask(UserManager.getInstance().deleteUserChoice(getApplicationContext()));
				intent = new Intent(getBaseContext(), SwitcherActivity.class);
				break;
			case R.id.menu_history:
				intent = new Intent(getBaseContext(), HistoryActivity.class);
				break;
			default:
				return super.onOptionsItemSelected(item);
		}
		startActivity(intent);
		return true;
	}

	public void showPosition(UserInterface user) {
		HistoryManager.getInstance().startNewTask(user);
		Intent myIntent = new Intent(this.getApplicationContext(), HelperMapActivity.class);
		startActivity(myIntent);
	}

	@Override
	public void onBackPressed() {}
}
