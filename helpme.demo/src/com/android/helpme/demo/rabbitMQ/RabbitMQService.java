package com.android.helpme.demo.rabbitMQ;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.android.helpme.demo.R;
import com.android.helpme.demo.R.drawable;
import com.android.helpme.demo.R.string;
import com.android.helpme.demo.gui.HelperActivity;
import com.android.helpme.demo.gui.SwitcherActivity;
import com.android.helpme.demo.gui.DrawManager.DRAWMANAGER_TYPE;
import com.android.helpme.demo.manager.HistoryManager;
import com.android.helpme.demo.manager.MessageOrchestrator;
import com.android.helpme.demo.manager.PositionManager;
import com.android.helpme.demo.manager.RabbitMQManager;
import com.android.helpme.demo.manager.UserManager;
import com.android.helpme.demo.manager.interfaces.RabbitMQManagerInterface;
import com.android.helpme.demo.manager.interfaces.RabbitMQManagerInterface.ExchangeType;
import com.android.helpme.demo.messagesystem.InAppMessage;
import com.android.helpme.demo.messagesystem.InAppMessageType;
import com.android.helpme.demo.utils.ThreadPool;
import com.android.helpme.demo.utils.User;
import com.android.helpme.demo.utils.UserInterface;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

public class RabbitMQService extends Service implements RabbitMQSerivceInterface{
	public static final String LOGTAG = RabbitMQService.class.getSimpleName();
	private static String URL = "ec2-54-247-61-12.eu-west-1.compute.amazonaws.com";
	private NotificationManager mNM;
	private ConnectionFactory factory;
	private Connection connection;
	private HashMap<String,Channel> subscribedChannels;
	private Boolean connected = false;
	private ShutdownReactor shutdownReactor;
	private RabbitMQService service;

	public static final String EXCHANGE_NAME = "exchange_name",MESSAGE = "message",DATA_STRING = "data_string", EXCHANGE_TYPE = "exchange_type",TEXT = "text",TITLE = "title";

	// Used to receive messages from the Activity
	final Messenger inMessenger = new Messenger(new IncomingHandler());
	// Use to send message to the Activity
	private Messenger outMessenger;

	class IncomingHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			Log.i(LOGTAG, "Got message");
			Bundle bundle = msg.getData();
			InAppMessageType type = InAppMessageType.valueOf(bundle.getString(MESSAGE));
			switch (type) {
			case CONNECTED:
				runThread(connect());
				break;
			case SUBSCRIBE:
				runThread(subscribeToChannel(bundle.getString(EXCHANGE_NAME), bundle.getString(EXCHANGE_TYPE)));
				break;
			case SEND:
				runThread(sendStringOnChannel(bundle.getString(DATA_STRING), bundle.getString(EXCHANGE_NAME)));
				break;
			case SUBSCRIBTION_ENDED:
				runThread(endSubscribtionToChannel(bundle.getString(EXCHANGE_NAME)));
				break;
			case NOTIFICATION:
				showNotification(bundle.getString(TEXT), bundle.getString(TITLE));
				break;

			default:
				Log.e(LOGTAG, "recevied Message with wrong Type");
				break;
			}
		}
	}
	// Unique Identification Number for the Notification.
	// We use it on Notification start, and to cancel it.
	private int NOTIFICATION = R.string.local_service_started;

	@Override
	public void onCreate() {
		mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		factory  = new ConnectionFactory();
		factory.setHost(URL);
		subscribedChannels = new HashMap<String, Channel>();
		shutdownReactor = new ShutdownReactor(subscribedChannels);
		service = this;
	}

	@Override
	public IBinder onBind(Intent intent) {
		Bundle extras = intent.getExtras();
		// Get messager from the Activity
		if (extras != null) {
			outMessenger = (Messenger) extras.get("MESSENGER");
		}
		// Return our messenger to the Activity to get commands
		return inMessenger.getBinder();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		runThread(disconnect());
		outMessenger = null;
		return false;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(LOGTAG, getString(R.string.local_service_started));
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		Toast.makeText(this, R.string.local_service_started, Toast.LENGTH_SHORT).show();

		String text = getString(R.string.waitingtext);
		String title = getString(R.string.waitingtitle);

		showNotification(text,title);
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		Log.i(LOGTAG, getString(R.string.local_service_stopped));
		mNM.cancelAll();
		runThread(disconnect());
		while (connected) {
			;
		}
		try {
			connection.close();
		} catch (IOException e) {
			Log.e(LOGTAG, e.toString());
		}
		// Tell the user we stopped.
		Toast.makeText(this, R.string.local_service_stopped, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void showNotification(String text, String title) {

		// The PendingIntent to launch our activity if the user selects this notification
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, SwitcherActivity.class), 0);


		Notification notification = new Notification.Builder(this)
		.setContentTitle(title)
		.setContentText(text)
		.setSmallIcon(R.drawable.ic_launcher)
		.setContentIntent(contentIntent)
		.setOngoing(true)
		.build();


		// Send the notification.
		mNM.notify(NOTIFICATION, notification);
	}

	@Override
	public Runnable connect() {
		return new Runnable() {

			@Override
			public void run() {
				if (connected) {
					return;
				}
				try {
					connection = factory.newConnection();
					connected = true;
					Log.i(LOGTAG, "connected to rabbitMQ");
					sendMessage(InAppMessageType.CONNECTED, null);

				} catch (IOException e) {
					Log.e(LOGTAG, e.toString());
				} catch (RemoteException e) {
					Log.e(LOGTAG, e.toString());
				}

			}
		};
	}

	@Override
	public Runnable disconnect() {
		return new Runnable() {

			@Override
			public void run() {
				if (!connected) {
					return;
				}
				Set<String> exchangeNames = subscribedChannels.keySet();
				for (String exchangeName : exchangeNames) {
					runThread(endSubscribtionToChannel(exchangeName));
				}
				subscribedChannels.clear();
				connected = false;
			}
		};
	}

	@Override
	public Runnable sendStringOnChannel(final String string,final String exchangeName) {
		return new Runnable() {

			@Override
			public void run() {
				try {
					Log.i(LOGTAG, "sending");
					Channel channel = subscribedChannels.get(exchangeName);
					if (channel != null) {
						channel.basicPublish(exchangeName, "", null, string.getBytes());
					}
				} catch (IOException e) {
					Log.e(LOGTAG, e.toString());
				}

			}
		};
	}

	@Override
	public Runnable subscribeToChannel(final String exchangeName,final String type) {
		return new Runnable() {

			@Override
			public void run() {
				try {
					// we create a new channel 
					Channel channel = connection.createChannel();
					channel.exchangeDeclare(exchangeName, type);
					String queueName = channel.queueDeclare().getQueue();
					channel.queueBind(queueName,exchangeName , "");
					channel.addShutdownListener(shutdownReactor);

					// we define what happens if we recieve a new Message
					channel.basicConsume(queueName,new RabbitMQConsumer(channel, service));
					subscribedChannels.put(exchangeName, channel);
				} catch (IOException e) {
					Log.e(LOGTAG, e.toString());
				}
			}
		};
	}

	@Override
	public Runnable endSubscribtionToChannel(final String exchangeName) {
		return new Runnable() {

			@Override
			public void run() {
				Channel channel = subscribedChannels.get(exchangeName);
				if (channel != null) {
					try {
						channel.close(0,exchangeName);
					} catch (IOException e) {
						Log.e(LOGTAG, e.toString());
					}
				}
			}
		};
	}

	protected void sendMessage(InAppMessageType type, String string) throws RemoteException{

		Message message = Message.obtain();
		Bundle bundle = new Bundle();
		bundle.putString(MESSAGE, type.name());
		if (type == InAppMessageType.RECEIVED_DATA) {
			bundle.putString(DATA_STRING, string);
		}
		message.setData(bundle);
		if (outMessenger != null) {
			outMessenger.send(message);
		}
	}

	private void runThread(Runnable runnable){
		new Thread(runnable).start();
	}
}
