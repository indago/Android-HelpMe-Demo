package com.android.helpme.demo.manager;

import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.util.Log;

import com.android.helpme.demo.manager.interfaces.RabbitMQManagerInterface;
import com.android.helpme.demo.messagesystem.AbstractMessageSystem;
import com.android.helpme.demo.messagesystem.AbstractMessageSystemInterface;
import com.android.helpme.demo.messagesystem.InAppMessage;
import com.android.helpme.demo.messagesystem.MESSAGE_TYPE;
import com.android.helpme.demo.utils.User;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

public class RabbitMQManager extends AbstractMessageSystem implements RabbitMQManagerInterface {
	public static final String LOGTAG = RabbitMQManager.class.getSimpleName();
	public static String QUEUE;
	private static final String EXCHANGE_NAME = "call";
	private static String URL = "ec2-54-247-61-12.eu-west-1.compute.amazonaws.com";
	private static RabbitMQManager rabbitMQManager;

	private InAppMessage message;
	private ConnectionFactory factory;
	private Connection connection;
	private Channel channel;
	private Boolean connected = false;

	public static RabbitMQManager getInstance() {
		if (rabbitMQManager == null) {
			rabbitMQManager = new RabbitMQManager();
		}
		return rabbitMQManager;
	}

	/* (non-Javadoc)
	 * @see com.android.helpme.demo.manager.RabbitMQManagerInterface#connect()
	 */
	@Override
	public Runnable connect(){
		return new Runnable() {

			@Override
			public void run() {
				if (connected) {
					return;
				}
				try {
					connection = factory.newConnection();
					channel = connection.createChannel();
					//					channel.queueDeclare(QUEUE, false, false, false, null);

					channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
					QUEUE = channel.queueDeclare().getQueue();
					channel.queueBind(QUEUE, EXCHANGE_NAME, "");

					connected = true;
					Log.i(LOGTAG, "connected to rabbitMQ");
					fireMessageFromManager(rabbitMQManager, MESSAGE_TYPE.CONNECTED);
				} catch (IOException e) {
					fireError(e);
				}

			}
		};

	}

	private RabbitMQManager() {
		factory  = new ConnectionFactory();
		factory.setHost(URL);
	}

	/* (non-Javadoc)
	 * @see com.android.helpme.demo.manager.RabbitMQManagerInterface#sendString(java.lang.String)
	 */
	@Override
	public Runnable sendString(final String string) {
		return new Runnable() {

			@Override
			public void run() {
				try {
					Log.i(LOGTAG, "sending");
					channel.basicPublish(EXCHANGE_NAME, "", null, string.getBytes());
				} catch (IOException e) {
					fireError(e);
				}

			}
		};
	}

	/* (non-Javadoc)
	 * @see com.android.helpme.demo.manager.RabbitMQManagerInterface#getString()
	 */
	@Override
	public Runnable getString() {
		return new Runnable() {

			@Override
			public void run() {
				QueueingConsumer consumer = new QueueingConsumer(channel);
				try {
					channel.basicConsume(QUEUE, true, consumer);
					while (connected) {
						QueueingConsumer.Delivery delivery = consumer.nextDelivery();
						String string = new String(delivery.getBody());
						//						Log.i(LOGTAG, "recevied sth: " + string);

						JSONParser parser = new JSONParser();
						JSONObject object;
						object = (JSONObject) parser.parse((String) message.getObject());
						User user = new User(object);
						fireMessageFromManager(user, MESSAGE_TYPE.USER);
					}

				}catch (ParseException e) {
					fireError(e); 
				}catch (IOException e) {
					fireError(e);
				} catch (ShutdownSignalException e) {
					fireError(e);
				} catch (ConsumerCancelledException e) {
					fireError(e);
				} catch (InterruptedException e) {
					fireError(e);
				}


			}
		};
	}

	@Override
	public String getLogTag() {
		return LOGTAG;
	}

	@Override
	protected InAppMessage getMessage() {
		return message;
	}

	@Override
	protected void setMessage(InAppMessage inAppMessage) {
		message = inAppMessage;

	}

	@Override
	public AbstractMessageSystemInterface getManager() {
		return rabbitMQManager;
	}

}
