package com.indago.helpme.gui.dashboard;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import com.indago.helpme.R;
import com.indago.helpme.gui.ATemplateActivity;
import com.indago.helpme.gui.dashboard.statemachine.HelpERStateMachine;
import com.indago.helpme.gui.dashboard.statemachine.STATES;
import com.indago.helpme.gui.dashboard.views.HelpERButtonView;
import com.indago.helpme.gui.dashboard.views.HelpERHintView;
import com.indago.helpme.gui.dashboard.views.HelpERProgressView;

public class HelpERDashboardActivity extends ATemplateActivity {
	private static final String LOGTAG = HelpERDashboardActivity.class.getSimpleName();

	private ImageView mTopCover;
	private Animator mFadeIn;
	private Animator mFadeOut;
	private HelpERProgressView mProgressBars;
	private HelpERButtonView mButton;
	private HelpERHintView mHintViewer;
	private Vibrator mVibrator;
	private HelpERStateMachine mStateMachine;
	private TextView mCounterText;
	private SlidingDrawer mSlidingDrawer;

	private CountdownTimer mCDT;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(LOGTAG, "... logged in!");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help_er_dashboard);

		mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

		mTopCover = (ImageView) findViewById(R.id.iv_topcover);
		mFadeIn = (Animator) AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.fade_in);
		mFadeOut = (Animator) AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.fade_out);

		mProgressBars = (HelpERProgressView) findViewById(R.id.iv_helpme_help_er_indicator);
		mHintViewer = (HelpERHintView) findViewById(R.id.tv_helpme_help_er_infoarea);
		mButton = (HelpERButtonView) findViewById(R.id.btn_helpme_help_er_button);
		mCounterText = (TextView) findViewById(R.id.tv_incomming_call_counter);

		/*
		 * SlidingDrawer Menu Setup
		 */
		mSlidingDrawer = (SlidingDrawer) findViewById(R.id.slidingdrawer);

		ViewGroup vg = (ViewGroup) mSlidingDrawer.getContent();
		vg.setRotation(180);
		TextView name = (TextView) vg.findViewById(R.id.tv_help_ee_name);
		TextView age = (TextView) vg.findViewById(R.id.tv_help_ee_age);
		TextView location = (TextView) vg.findViewById(R.id.tv_help_ee_location);

		Drawable[] drawables = new Drawable[4];
		drawables[0] = getResources().getDrawable(R.drawable.user_picture_background);
		drawables[1] = getResources().getDrawable(R.drawable.help_ee_woman1);
		drawables[2] = getResources().getDrawable(R.drawable.user_picture_overlay);
		drawables[3] = getResources().getDrawable(R.drawable.user_picture_border);

		ImageView picture = (ImageView) vg.findViewById(R.id.iv_help_ee_picture);
		picture.setImageDrawable(new LayerDrawable(drawables));

		Button handle = (Button) ((LinearLayout) mSlidingDrawer.getHandle()).getChildAt(0);
		handle.setText("DRAG DOWN FOR MORE INFORMATION!");
		handle.setTextSize(14f);

		/*
		 * StateMachine Setup
		 */

		mStateMachine = HelpERStateMachine.getInstance();
		mStateMachine.addOne(mButton);
		mStateMachine.addOne(mHintViewer);
		mStateMachine.addOne(mProgressBars);
		mStateMachine.updateAll();

		mCDT = new CountdownTimer();

		mButton.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(v instanceof HelpERButtonView) {

					switch(event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							if(mStateMachine.getState() == STATES.DEFAULT) {

								int x = (int) event.getX();
								int y = (int) event.getY();

								// Toast.makeText(getApplicationContext(), "X: " + x + " Y: " + y, Toast.LENGTH_LONG).show();

								if(y < (v.getMeasuredHeight() * 0.5)) {
									mStateMachine.setState(STATES.ACCEPTED);
								} else {
									mStateMachine.setState(STATES.DECLINED);
								}

								mVibrator.vibrate(10);

							}
							break;
						case MotionEvent.ACTION_UP:

							if(mStateMachine.getState() == STATES.ACCEPTED) {
								Toast.makeText(getApplicationContext(), "Thank you for providing help!", Toast.LENGTH_LONG).show();

								mCDT.dismiss();

								startActivity(new Intent(getApplicationContext(), com.indago.helpme.gui.dashboard.HelpERCallDetailsActivity.class));

								finish();
							} else if(mStateMachine.getState() == STATES.DECLINED) {
								Toast.makeText(getApplicationContext(), "Too bad! Maybe next time!", Toast.LENGTH_LONG).show();
								mCDT.dismiss();

								startActivity(new Intent(getApplicationContext(), com.indago.helpme.gui.HelpERControlcenterActivity.class));

								finish();
							} else {
								Toast.makeText(getApplicationContext(), "UNDEFINED!", Toast.LENGTH_LONG).show();
							}
							mStateMachine.setState(STATES.DEFAULT);
							break;
					}
				}
				return false;
			}
		});
	}

	//	@Override
	//	public boolean onCreateOptionsMenu(Menu menu) {
	//		getMenuInflater().inflate(R.menu.activity_help_er_dashboard, menu);
	//		return true;
	//	}

	@Override
	protected void onResume() {
		super.onResume();

		mCDT.execute();
	}

	@Override
	protected void onPause() {
		super.onPause();
		//mCountdown.dissmiss();
		mStateMachine.setState(STATES.DEFAULT);
		mCDT.dismiss();
		finish();
	}

	private class CountdownTimer extends AsyncTask<Void, Void, Void> {

		private volatile int seconds = 60;
		private volatile boolean dismissed = false;
		private volatile boolean stopThread = false;

		public void resetTime() {
			seconds = 60;
		}

		public void dismiss() {
			dismissed = true;
		}

		@Override
		synchronized protected Void doInBackground(Void... params) {
			try {

				while(!stopThread && !dismissed) {
					Thread.sleep(980);
					publishProgress();
				}
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if(!dismissed) {
				Toast.makeText(getApplicationContext(), "Time's up!", Toast.LENGTH_LONG).show();

				startActivity(new Intent(getApplicationContext(), com.indago.helpme.gui.HelpERControlcenterActivity.class));

				finish();
			}

			super.onPostExecute(result);
		}

		@Override
		protected void onProgressUpdate(Void... values) {

			if(seconds >= 0) {
				mCounterText.setText("" + seconds);
				if((seconds % 10 == 9 || seconds == 0) && seconds < 50) {
					mProgressBars.countdownStep(1);
				}

				seconds--;
			} else {
				stopThread = true;
			}

			super.onProgressUpdate(values);
		}

	}

}
