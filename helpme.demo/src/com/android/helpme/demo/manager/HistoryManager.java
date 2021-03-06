/**
 * 
 */
package com.android.helpme.demo.manager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.content.Context;

import com.android.helpme.demo.exceptions.DontKnowWhatHappenedException;
import com.android.helpme.demo.manager.interfaces.HistoryManagerInterface;
import com.android.helpme.demo.messagesystem.AbstractMessageSystem;
import com.android.helpme.demo.messagesystem.AbstractMessageSystemInterface;
import com.android.helpme.demo.messagesystem.InAppMessage;
import com.android.helpme.demo.messagesystem.InAppMessageType;
import com.android.helpme.demo.utils.Task;
import com.android.helpme.demo.utils.UserInterface;

/**
 * @author Andreas Wieland
 *
 */
public class HistoryManager extends AbstractMessageSystem implements HistoryManagerInterface,Observer {
	private static final String LOGTAG = HistoryManager.class.getSimpleName();
	private static HistoryManager manager;
	private static Task currentTask;
	private InAppMessage message;
	private static final String FILENAME = "history_file";
	private Context context;
	private ArrayList<String> arrayList;

	public static HistoryManager getInstance(){
		if (manager == null) {
			manager = new HistoryManager();
		}
		return manager;
	}

	/**
	 * 
	 */
	private HistoryManager() {
		context = null;
		arrayList = new ArrayList<String>();
	}

	/*
	 * (non-Javadoc)
	 * @see com.android.helpme.demo.manager.interfaces.HistoryManagerInterface#setContext(android.content.Context)
	 */
	@Override
	public void setContext(Context context) {
		this.context = context;
	}


	/* (non-Javadoc)
	 * @see com.android.helpme.demo.messagesystem.AbstractMessageSystemInterface#getLogTag()
	 */
	@Override
	public String getLogTag() {
		return LOGTAG;
	}

	/* (non-Javadoc)
	 * @see com.android.helpme.demo.messagesystem.AbstractMessageSystemInterface#getManager()
	 */
	@Override
	public AbstractMessageSystemInterface getManager() {
		return manager;
	}

	/* (non-Javadoc)
	 * @see com.android.helpme.demo.manager.interfaces.HistoryManagerInterface#getStatistics()
	 */
	@Override
	public ArrayList<String> getStatistics() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.android.helpme.demo.manager.interfaces.HistoryManagerInterface#getHistory()
	 */
	@Override
	public ArrayList<String> getHistory() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.android.helpme.demo.messagesystem.AbstractMessageSystem#getMessage()
	 */
	@Override
	protected InAppMessage getMessage() {
		return message;
	}

	/* (non-Javadoc)
	 * @see com.android.helpme.demo.messagesystem.AbstractMessageSystem#setMessage(com.android.helpme.demo.messagesystem.InAppMessage)
	 */
	@Override
	protected void setMessage(InAppMessage inAppMessage) {
		this.message = inAppMessage;
	}

	@Override
	public Task getTask() {
		return currentTask;
	}

	@Override
	public void startNewTask() {
		currentTask = new Task();
		currentTask.addObserver(this);
		currentTask.startTask();
	}

	@Override
	public void startNewTask(UserInterface user) {
			currentTask = new Task();
			currentTask.addObserver(this);
			currentTask.startTask(user);
	}

	@Override
	public void stopTask() {
		if (currentTask != null) {
			if (currentTask.isAnswered()) {
				arrayList.add(currentTask.stopTask().toString());
			}else {
				currentTask.stopUnfinishedTask();
			}
			currentTask = null;
		}		
	}
	
	private boolean readHistory(){
		if (context != null) {
			try{
				FileInputStream inputStream = context.openFileInput(FILENAME);
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
				String string = null; 
				while ((string = reader.readLine()) != null) {
					arrayList.add(string);
				}
				reader.close();
				inputStream.close();
				return true;
			}catch(IOException e){
				fireError(e);
			}
		}
		return false;
	}

	private boolean writeHistory(){
		if (context != null) {
			try {
				FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));

				for (String string : arrayList) {
					writer.write(string);
				}
				
				writer.close();
				fos.close();
				return true;
			} catch (IOException e) {
				fireError(e);
			}
		}
		return false;
	}
	
	@Override
	public Runnable loadHistory(Context applicationContext) {
		setContext(applicationContext);
		return new Runnable() {
			
			@Override
			public void run() {
				if (readHistory()) {
					fireMessageFromManager(arrayList, InAppMessageType.LOADED);
				}
			}
		};
	}
	
	@Override
	public Runnable saveHistory(Context applicationContext) {
		setContext(applicationContext);
		return new Runnable() {
			
			@Override
			public void run() {
				if (!writeHistory()) {
					fireError(new DontKnowWhatHappenedException());
				}
			}
		};
	}

	@Override
	public void update(Observable observable, Object data) {
		fireMessageFromManager(currentTask, InAppMessageType.TIMEOUT);
	}
}
