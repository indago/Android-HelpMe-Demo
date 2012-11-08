package com.indago.helpme.gui2;

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
import android.widget.Button;
import android.widget.ProgressBar;

import com.android.helpme.demo.interfaces.DrawManagerInterface;
import com.android.helpme.demo.interfaces.MessageOrchestratorInterface;
import com.android.helpme.demo.manager.HistoryManager;
import com.android.helpme.demo.manager.MessageOrchestrator;
import com.android.helpme.demo.manager.UserManager;
import com.android.helpme.demo.utils.Task;
import com.android.helpme.demo.utils.ThreadPool;
import com.android.helpme.demo.utils.User;
import com.indago.helpme.R;

public class SeekerActivity extends Activity implements DrawManagerInterface {
	private static final String LOG_TAG = "MainActivity";

	private Button buttonHelpMe;
	private ProgressBar progressBar;
	private MessageOrchestratorInterface orchestrator;
	private Handler uihandler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		uihandler = new Handler();
		buttonHelpMe = (Button) findViewById(R.id.helpButton);
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		progressBar.setVisibility(ProgressBar.INVISIBLE);
		buttonHelpMe.setVisibility(Button.VISIBLE);
		init();
	}

	private void init() {
		orchestrator = MessageOrchestrator.getInstance();
		orchestrator.addDrawManager(DRAWMANAGER_TYPE.SEEKER, this);
		wireButton();
	}

	private void wireButton() {
		buttonHelpMe.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				progressBar.setVisibility(ProgressBar.VISIBLE);
				//				buttonHelpMe.setActivated(false);
				//				buttonHelpMe.setClickable(false);
				buttonHelpMe.setVisibility(Button.INVISIBLE);
				HistoryManager.getInstance().startNewTask();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.menu_relog:
				ThreadPool.runTask(UserManager.getInstance().deleteUserChoice(getApplicationContext()));
				Intent intent = new Intent(getBaseContext(), SwitcherActivity.class);
				startActivity(intent);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private Runnable noUserFound(final Context context) {
		return new Runnable() {

			@Override
			public void run() {
				AlertDialog.Builder dlgAlert = new AlertDialog.Builder(context);
				dlgAlert.setTitle("NO HELPER");
				dlgAlert.setMessage("NO HELPER TEXT");
				dlgAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						progressBar.setVisibility(ProgressBar.INVISIBLE);
						buttonHelpMe.setVisibility(Button.VISIBLE);
					}
				});

				dlgAlert.create().show();

			}
		};
	}

	@Override
	public void drawThis(Object object) {
		if(object instanceof User) {
			//			progressBar.setVisibility(ProgressBar.INVISIBLE);
			Intent myIntent = new Intent(this.getApplicationContext(), HelperCommingActivity.class);
			startActivity(myIntent);
			finish();
		}
		if(object instanceof Task) {
			uihandler.post(noUserFound(this));
		}
	}

	@Override
	public void onBackPressed() {
		//		Intent intent = new Intent(this, SeekerActivity.class);
		//		startActivity(intent);
	}
}
