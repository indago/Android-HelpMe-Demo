/**
 * 
 */
package com.indago.helpme.gui2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.android.helpme.demo.interfaces.DrawManagerInterface;
import com.android.helpme.demo.manager.HistoryManager;
import com.android.helpme.demo.manager.MessageOrchestrator;
import com.android.helpme.demo.manager.PositionManager;
import com.android.helpme.demo.manager.RabbitMQManager;
import com.android.helpme.demo.manager.UserManager;
import com.android.helpme.demo.rabbitMQ.RabbitMQService;
import com.android.helpme.demo.utils.ThreadPool;
import com.android.helpme.demo.utils.User;

/**
 * @author Andreas Wieland
 * 
 */
public class SwitcherActivity extends Activity implements DrawManagerInterface {
	private Handler handler;
	private MessageOrchestrator orchestrator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		handler = new Handler();

		ThreadPool.getThreadPool(10);

		Intent intent = new Intent(this, RabbitMQService.class);
		startService(intent);

		orchestrator = MessageOrchestrator.getInstance();
		orchestrator.addDrawManager(DRAWMANAGER_TYPE.SWITCHER, this);

		orchestrator.listenToMessageSystem(RabbitMQManager.getInstance());
		orchestrator.listenToMessageSystem(PositionManager.getInstance(this));
		orchestrator.listenToMessageSystem(UserManager.getInstance());
		orchestrator.listenToMessageSystem(HistoryManager.getInstance());

		ThreadPool.runTask(RabbitMQManager.getInstance().bindToService(this));
		ThreadPool.runTask(UserManager.getInstance().readUserChoice(getApplicationContext()));
	}

	private Runnable startHelperActivity() {
		return new Runnable() {

			@Override
			public void run() {
				Intent intent;
				intent = new Intent(getBaseContext(), HelperActivity.class);
				startActivity(intent);

			}
		};

	}

	private Runnable startSeekerActivity() {
		return new Runnable() {

			@Override
			public void run() {
				Intent intent;
				intent = new Intent(getBaseContext(), SeekerActivity.class);
				startActivity(intent);
			}
		};

	}

	private Runnable startLoginActivity() {
		return new Runnable() {

			@Override
			public void run() {
				UserManager.getInstance().readUserFromProperty(getApplicationContext());
				Intent intent;
				intent = new Intent(getBaseContext(), LoginActivity.class);
				startActivity(intent);
			}
		};

	}

	@Override
	public void drawThis(Object object) {
		if(object instanceof User) {
			User user = (User) object;
			if(user.isHelper()) {
				handler.post(startHelperActivity());
			} else {
				handler.post(startSeekerActivity());
			}
		} else if(object == null) {
			handler.post(startLoginActivity());
		}
	}

}
