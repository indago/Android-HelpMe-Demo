package com.indago.helpme.gui.dashboard;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

import com.indago.helpme.R;
import com.indago.helpme.gui.ATemplateActivity;
import com.indago.helpme.gui.dashboard.statemachine.HelpEEStateMachine;
import com.indago.helpme.gui.dashboard.statemachine.STATES;
import com.indago.helpme.gui.dashboard.views.HelpEEButtonView;
import com.indago.helpme.gui.dashboard.views.HelpEEHintView;
import com.indago.helpme.gui.dashboard.views.HelpEEProgressView;

public class HelpEEDashboardActivity extends ATemplateActivity {
	private static final String LOGTAG = HelpEEDashboardActivity.class.getSimpleName();

	private ImageView mTopCover;
	private Animator mFadeIn;
	private Animator mFadeOut;
	private HelpEEProgressView mProgressBars;
	private HelpEEButtonView mButton;
	private HelpEEHintView mHintViewer;
	private Vibrator mVibrator;
	private HelpEEStateMachine mStateMachine;

	private resetTimer mIdleTimer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(LOGTAG, "... logged in!");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help_ee_dashboard);

		mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

		mTopCover = (ImageView) findViewById(R.id.iv_topcover);
		mFadeIn = (Animator) AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.fade_in);
		mFadeOut = (Animator) AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.fade_out);

		mProgressBars = (HelpEEProgressView) findViewById(R.id.iv_help_ee_indicator);
		mHintViewer = (HelpEEHintView) findViewById(R.id.tv_help_ee_infoarea);
		mButton = (HelpEEButtonView) findViewById(R.id.btn_help_ee_button);

		mStateMachine = HelpEEStateMachine.getInstance();
		mStateMachine.addOne(mButton);
		mStateMachine.addOne(mHintViewer);
		mStateMachine.addOne(mProgressBars);

		mButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(v instanceof HelpEEButtonView && mStateMachine.getState() == STATES.LOCKED) {
					toCallCenter();
				}
			}
		});

		mButton.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(v instanceof HelpEEButtonView) {

					switch(event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							if(mStateMachine.getState() != STATES.LOCKED) {

								mStateMachine.nextState();

								switch((STATES) mStateMachine.getState()) {
									case PART_SHIELDED:
										if(mIdleTimer != null) {
											mIdleTimer = new resetTimer();
											mIdleTimer.execute(6000L);
										}
										break;
									case PRESSED:
										if(mIdleTimer != null) {
											mIdleTimer.dismiss();
										}
										break;

									default:
										if(mIdleTimer != null) {
											mIdleTimer.resetTime();
										}
										break;
								}

								mVibrator.vibrate(10);

							} else {
								/**
								 * FOR TESTING PURPOSE
								 */
								//new setStateTimer(10000L).execute(STATES.CALLCENTER);
							}

							break;
						case MotionEvent.ACTION_UP:
							if(mStateMachine.getState() == STATES.PRESSED) {
								ButtonStateChangeDelay mBRTimer = new ButtonStateChangeDelay();
								mBRTimer.execute(STATES.LOCKED);
							}
							break;
					}
				}
				return false;
			}
		});
	}

	//	@Override
	//	public boolean onCreateOptionsMenu(Menu menu) {
	//		getMenuInflater().inflate(R.menu.activity_help_ee_dashboard, menu);
	//		return true;
	//	}

	@Override
	protected void onPause() {
		super.onPause();
		if(mIdleTimer != null) {
			mIdleTimer.dismiss();
		}

		mStateMachine.setState(STATES.SHIELDED);

		finish();
	}

	private void reset() {
		mTopCover.setImageResource(R.drawable.drawable_white);
		mFadeIn.setTarget(mTopCover);
		mFadeOut.setTarget(mTopCover);
		mFadeOut.setStartDelay(100);
		mFadeIn.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {

				mStateMachine.setState(STATES.SHIELDED);

				mFadeOut.start();
				super.onAnimationEnd(animation);
			}
		});

		long[] pattern = { 0, 25, 75, 25, 75, 25, 75, 25 };
		mVibrator.vibrate(pattern, -1);
		mFadeIn.start();
	}

	public void toCallCenter() {
		mTopCover.setImageResource(R.drawable.drawable_yellow);
		mFadeIn.setTarget(mTopCover);
		mFadeOut.setTarget(mTopCover);
		mFadeOut.setStartDelay(100);
		mFadeIn.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {

				mStateMachine.setState(STATES.CALLCENTER);

				mFadeOut.start();
				super.onAnimationEnd(animation);
			}
		});

		long[] pattern = { 0, 25, 75, 25, 75, 25, 75, 25 };
		mVibrator.vibrate(pattern, -1);
		mFadeIn.start();
	}

	class resetTimer extends AsyncTask<Long, Void, Void> {

		private volatile long idleTimeout = 10000;
		private volatile boolean dismissed = false;

		private long oldTime;

		public resetTimer() {}

		synchronized public void resetTime() {
			oldTime = System.currentTimeMillis();
		}

		synchronized public void dismiss() {
			dismissed = true;
		}

		@Override
		protected Void doInBackground(Long... params) {
			idleTimeout = params[0];
			oldTime = System.currentTimeMillis();

			while(!dismissed && (System.currentTimeMillis() - oldTime) <= idleTimeout) {
				try {
					Thread.sleep((long) (250));
				} catch(InterruptedException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if(!dismissed) {
				reset();
			}
			super.onPostExecute(result);

			mIdleTimer = null;
		}
	}

	class setStateTimer extends AsyncTask<STATES, Void, STATES> {

		private volatile long idleTimeout = 10000;
		private volatile boolean dismissed = false;

		private long oldTime;

		public setStateTimer(long waitTime) {
			idleTimeout = waitTime;
		}

		synchronized public void dismiss() {
			dismissed = true;
		}

		@Override
		protected STATES doInBackground(STATES... params) {
			oldTime = System.currentTimeMillis();

			while(!dismissed && (System.currentTimeMillis() - oldTime) <= idleTimeout) {
				try {
					Thread.sleep((long) (250));
				} catch(InterruptedException e) {
					e.printStackTrace();
				}
			}

			return params[0];
		}

		@Override
		protected void onPostExecute(STATES result) {
			if(!dismissed && result != null) {
				mStateMachine.setState(result);
			}
			super.onPostExecute(result);
		}

	}

	class ButtonStateChangeDelay extends AsyncTask<STATES, Void, STATES> {

		@Override
		protected STATES doInBackground(STATES... params) {

			try {
				Thread.sleep((long) (500));
			} catch(InterruptedException e) {
				e.printStackTrace();
			}

			return params[0];
		}

		@Override
		protected void onPostExecute(STATES state) {
			if(state != null) {
				mStateMachine.setState(state);
			}
			super.onPostExecute(state);
		}

	}

}