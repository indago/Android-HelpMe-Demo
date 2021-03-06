/**
 * 
 */
package com.android.helpme.demo.rabbitMQ;

import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.os.RemoteException;
import android.util.Log;

import com.android.helpme.demo.manager.interfaces.RabbitMQManagerInterface;
import com.android.helpme.demo.messagesystem.InAppMessageType;
import com.android.helpme.demo.utils.User;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;

/**
 * @author Andreas Wieland
 *
 */
public class RabbitMQConsumer extends DefaultConsumer {
	private static final String LOGTAG = RabbitMQConsumer.class.getSimpleName();
	private RabbitMQService rabbitMQSerivce;

	/**
	 * @param channel
	 */
	public RabbitMQConsumer(Channel channel, RabbitMQService service) {
		super(channel);
		this.rabbitMQSerivce = service;
	}
	
	@Override
	public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body) throws IOException {
		String string = new String(body);
		try {
			rabbitMQSerivce.sendMessage(InAppMessageType.RECEIVED_DATA, string);
		} catch (RemoteException e) {
			Log.e(LOGTAG, e.toString());
		}
	}

}
