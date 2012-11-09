package com.indago.helpme.gui.dashboard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.indago.helpme.R;
import com.indago.helpme.gui.ATemplateActivity;

public class HelpERCallDetailsActivity extends ATemplateActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_help_er_call_details);

	}

	@Override
	public void onBackPressed() {

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		// set title
		alertDialogBuilder.setTitle("Warning!\nCanceling Support Offer!");

		// set dialog message
		alertDialogBuilder.setMessage("Do you really want to cancel your current support offer?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// if this button is clicked, close
				// current activity
				finish();
			}
		}).setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// if this button is clicked, just close
				// the dialog box and do nothing
				dialog.cancel();
			}
		});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
		//startActivity(new Intent(getApplicationContext(), com.indago.helpme.gui.HelpERControlcenterActivity.class), null);

		//super.onBackPressed();

		//finish();
	}

}
