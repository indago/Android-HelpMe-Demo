package com.android.helpme.demo;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.android.helpme.demo.DrawManager.DRAWMANAGER_TYPE;
import com.android.helpme.demo.manager.MessageOrchestrator;
import com.android.helpme.demo.manager.PositionManager;
import com.android.helpme.demo.manager.RabbitMQManager;
import com.android.helpme.demo.manager.UserManager;
import com.android.helpme.demo.manager.interfaces.MessageOrchestratorInterface;
import com.android.helpme.demo.utils.ThreadPool;
import com.android.helpme.demo.utils.User;
import com.android.helpme.demo.utils.UserInterface;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends Activity implements DrawManager{
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
		init();
	}
	
	private void init(){
		orchestrator = MessageOrchestrator.getInstance();
		orchestrator.setDrawManager(DRAWMANAGER_TYPE.MAIN,this);
		wireButton();
	}
	
	

	private void wireButton() {
		buttonHelpMe.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				progressBar.setVisibility(ProgressBar.VISIBLE);
				buttonHelpMe.setActivated(false);
				buttonHelpMe.setClickable(false);
				uihandler.post (PositionManager.getInstance().startLocationTracking());
			}
		});
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		getMenuInflater().inflate(R.menu.activity_main, menu);
//		return true;
//	}
	@Override
	public void drawThis(Object object) {
		if (object instanceof User) {
//			progressBar.setVisibility(ProgressBar.INVISIBLE);
			Intent myIntent = new Intent(this.getApplicationContext(), FoundHelperActivity.class);
			startActivity(myIntent);
		}

	}
}
