package com.indago.helpme.gui.dashboard.asynctasks;

import android.os.AsyncTask;

public interface IAsyncTaskListener {
	public void onATUpdate(AsyncTask<?, ?, ?> source);

	public void onATDismissed(AsyncTask<?, ?, ?> source);

	public void onATExpired(AsyncTask<?, ?, ?> source);
}
