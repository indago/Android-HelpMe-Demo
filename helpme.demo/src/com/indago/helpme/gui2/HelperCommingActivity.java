/**
 * 
 */
package com.indago.helpme.gui2;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.android.helpme.demo.interfaces.DrawManagerInterface;
import com.android.helpme.demo.interfaces.UserInterface;
import com.android.helpme.demo.manager.HistoryManager;
import com.android.helpme.demo.manager.MessageOrchestrator;
import com.android.helpme.demo.utils.Task;
import com.indago.helpme.R;

/**
 * @author Andreas Wieland
 * 
 */
public class HelperCommingActivity extends Activity implements DrawManagerInterface {
	private TextView textView;
	private Handler handler;
	private DecimalFormat decimalFormat;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.helpercoming);
		textView = (TextView) findViewById(R.id.textView);
		handler = new Handler();
		handler.post(setText());

		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
		decimalFormatSymbols.setDecimalSeparator('.');
		decimalFormatSymbols.setGroupingSeparator(',');
		decimalFormat = new DecimalFormat("#,##0.00", decimalFormatSymbols);

		MessageOrchestrator.getInstance().addDrawManager(DRAWMANAGER_TYPE.HELPERCOMMING, this);
	}

	private Runnable setText() {
		return new Runnable() {

			@Override
			public void run() {
				Double distance = HistoryManager.getInstance().getTask().getDistance();
				textView.setText("Distance to helper: " + decimalFormat.format(distance) + " m");
			}
		};

	}

	private Runnable helperIsNextToYou(final UserInterface userInterface, final Context context) {
		return new Runnable() {

			@Override
			public void run() {
				AlertDialog.Builder dlgAlert = new AlertDialog.Builder(context);
				dlgAlert.setTitle("HELPER IN RANGE");
				String text = "HELPER IN RANGE TEXT";
				text.replace("[name]", userInterface.getName());
				dlgAlert.setMessage(text);
				dlgAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(context, SeekerActivity.class);
						HistoryManager.getInstance().stopTask();
						MessageOrchestrator.getInstance().removeDrawManager(DRAWMANAGER_TYPE.HELPERCOMMING);
						startActivity(intent);
						finish();
					}
				});

				dlgAlert.create().show();

			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.android.helpme.demo.gui.DrawManager#drawThis(java.lang.Object)
	 */
	@Override
	public void drawThis(Object object) {
		if(object instanceof UserInterface) {
			handler.post(setText());
		} else if(object instanceof Task) {
			Task task = (Task) object;
			handler.post(helperIsNextToYou(task.getUser(), this));
		}
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, SeekerActivity.class);
		HistoryManager.getInstance().stopTask();
		HistoryManager.getInstance().saveHistory(getApplicationContext());
		MessageOrchestrator.getInstance().removeDrawManager(DRAWMANAGER_TYPE.HELPERCOMMING);
		startActivity(intent);
		finish();
	}

}
