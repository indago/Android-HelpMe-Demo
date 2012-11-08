package com.indago.helpme.gui.dashboard.asynctasks;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import android.content.Context;
import android.os.AsyncTask;

public class ATCounter extends AsyncTask<Void, Void, Void> {

	private volatile boolean interrupted = false;
	private Context mContext;
	private int mLoopCount = 10;
	private int mStepValue = 1;
	volatile private long mSleepTime = mStepValue * 1000;

	private List<IAsyncTaskListener> mViewList = new LinkedList<IAsyncTaskListener>();

	public void addOne(IAsyncTaskListener object) {
		if(mViewList.contains(object)) {
			return;
		}
		mViewList.add(object);
	}

	public void removeOne(IAsyncTaskListener object) {
		mViewList.remove(object);
	}

	public ATCounter(Context context, int timeValue, int timeStep) {
		mContext = context;
		mLoopCount = timeValue;
		if(timeStep > 1) {
			mStepValue = timeStep;
			mSleepTime = timeStep * 1000;
			mSleepTime -= 50;
		}
	}

	public synchronized void interrupt() {
		interrupted = true;
	}

	@Override
	protected Void doInBackground(Void... params) {

		while(!interrupted && mLoopCount > 0) {
			try {
				Thread.sleep(mSleepTime);
			} catch(InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			publishProgress();
			mLoopCount -= mStepValue;
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		if(!interrupted) {
			ListIterator<IAsyncTaskListener> it = mViewList.listIterator();
			while(it.hasNext()) {
				(it.next()).onATExpired(this);
			}
		}
		super.onPostExecute(result);
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		ListIterator<IAsyncTaskListener> it = mViewList.listIterator();
		while(it.hasNext()) {
			(it.next()).onATUpdate(this);
		}
		super.onProgressUpdate(values);
	}
}
