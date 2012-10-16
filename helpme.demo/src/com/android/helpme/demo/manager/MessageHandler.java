package com.android.helpme.demo.manager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Element;

import android.location.Location;
import android.util.Log;

import com.android.helpme.demo.DrawManager;
import com.android.helpme.demo.DrawManager.DRAWMANAGER_TYPE;
import com.android.helpme.demo.MainActivity;
import com.android.helpme.demo.exceptions.UnkownMessageType;
import com.android.helpme.demo.exceptions.WrongObjectType;
import com.android.helpme.demo.manager.interfaces.MessageHandlerInterface;
import com.android.helpme.demo.manager.interfaces.MessageOrchestratorInterface;
import com.android.helpme.demo.manager.interfaces.RabbitMQManagerInterface;
import com.android.helpme.demo.messagesystem.AbstractMessageSystem;
import com.android.helpme.demo.messagesystem.InAppMessage;
import com.android.helpme.demo.utils.ThreadPool;
import com.android.helpme.demo.utils.User;
import com.android.helpme.demo.utils.position.Position;
import com.android.helpme.demo.utils.position.PositionInterface;

/**
 * 
 * @author Andreas Wieland
 * 
 */
public abstract class MessageHandler extends AbstractMessageSystem implements MessageHandlerInterface{

	abstract protected boolean reloadDatabase();

	protected void handleLocationMessage(InAppMessage message) {
		switch (message.getType()) {
		case LOCATION:
			if (!(message.getObject() instanceof PositionInterface)) {
				fireError(new WrongObjectType(message.getObject(), Position.class));
				return;
			}
			PositionInterface position = (PositionInterface) message.getObject();
			JSONObject object = new JSONObject();
			object = UserManager.getInstance().getThisUser().getJsonObject();
			object.put(User.POSITION, position.getJSON());

			ThreadPool.runTask(PositionManager.getInstance().stopLocationTracking());
			ThreadPool.runTask(RabbitMQManager.getInstance().sendString(object.toString()));
			break;

		default:
			fireError(new UnkownMessageType());
			break;
		}
	}

	protected void handleRabbitMQMessages(InAppMessage message) {
		switch (message.getType()) {
		case CONNECTED:
			if (!(message.getObject() instanceof RabbitMQManagerInterface)) {
				fireError(new WrongObjectType(message.getObject(), RabbitMQManager.class));
				return;
			}
			ThreadPool.runTask(RabbitMQManager.getInstance().getString());
			break;
		case SEND:
			// TODO
			break;
		case USER:
			if (!(message.getObject() instanceof User)) {
				fireError(new WrongObjectType(message.getObject(), User.class));
				return;
			}
			
			User user = (User) message.getObject();
			if (UserManager.getInstance().isUserSet() && UserManager.getInstance().getThisUser().getHelfer() && !user.getHelfer()) {
				ThreadPool.runTask(PositionManager.getInstance().startLocationTracking());
			}
			UserManager.getInstance().addUser(user);
			if (getDrawManager(DRAWMANAGER_TYPE.LIST) == null) {
				getDrawManager(DRAWMANAGER_TYPE.MAIN).drawThis(user);
			} else {
				getDrawManager(DRAWMANAGER_TYPE.LIST).drawThis(user);
			}


			break;

		default:
			fireError(new UnkownMessageType());
			break;
		}
	}

	protected void handleUserMessages(InAppMessage message) {
		switch (message.getType()) {
		case USER:
			if (!(message.getObject() instanceof ArrayList<?>)) {
				fireError(new WrongObjectType(message.getObject(), ArrayList.class));
				return;
			}

			getDrawManager(DRAWMANAGER_TYPE.LOGIN).drawThis(message.getObject());
			break;

		default:
			fireError(new UnkownMessageType());
			break;
		}
	}

	/**
	 * handles messages from Stomp Connection
	 * 
	 * @param message
	 */
	// protected void handleStompMessages(Message message) {
	// Element element = (Element) message.getObject();
	// Long id;
	// switch (message.getType()) {
	// case APPOINTMENT:
	// id = new Long(element.getAttribute("id"));
	// Appointment appointment = AppointmentDB.getDB().getAppointment(id);
	// appointment.setStatus(Status.valueOf(element.getAttribute("status")));
	// ThreadPool.runTask(AppointmentDB.getDB().show(id));
	// break;
	// case PREPTASK:
	// id = new Long(element.getAttribute("id"));
	// Subtask preptask = SubTaskFromAppointmentDB.getDB().getPrepTask(id);
	// preptask.setStatus(Status.valueOf(element.getAttribute("status")));
	// ThreadPool.runTask(SubTaskFromAppointmentDB.getDB().show(id));
	// break;
	// case SUBTASK:
	// id = new Long(element.getAttribute("id"));
	// Subtask subtask = SubTaskFromTaskDB.getDB().getSubtask(id);
	// subtask.setStatus(Status.valueOf(element.getAttribute("status")));
	// ThreadPool.runTask(SubTaskFromTaskDB.getDB().show(id));
	// break;
	// case TASK:
	// id = new Long(element.getAttribute("id"));
	// Task task = TaskDB.getDB().getTask(id);
	// task.setStatus(Status.valueOf(element.getAttribute("status")));
	// ThreadPool.runTask(TaskDB.getDB().show(id));
	// break;
	// case ITEM:
	// id = new Long(element.getAttribute("id"));
	// Item item = ItemDB.getDB().getItem(id);
	// item.setStatus(Status.valueOf(element.getAttribute("status")));
	// ThreadPool.runTask(ItemDB.getDB().show(id));
	// break;
	// case JOURNEY:
	// case APPOINTMENT_REMINDER:
	// case PREPTASK_REMINDER:
	// id = new Long(element.getAttribute("id"));
	// ArrayList<Status> buttons = new ArrayList<Status>();
	// if (new Boolean(element.getAttribute("startable")).booleanValue() ) {
	// buttons.add(Status.STARTABLE);
	// }
	// if (new Boolean(element.getAttribute("confirmable")).booleanValue()) {
	// buttons.add(Status.CONFIRMABLE);
	// }
	// if (new Boolean(element.getAttribute("cancelable")).booleanValue()) {
	// buttons.add(Status.CANCELABLE);
	// }
	// if (new Boolean(element.getAttribute("snoozable")).booleanValue()) {
	// buttons.add(Status.SNOOZEABLE);
	// }
	//
	// ThreadPool.runTask(SQLConnection.getConnection().getReminder(id,buttons,message.getType()));
	// break;
	// case CONFIG_CHANGE:
	// Long timestamp = new Long(element.getAttribute("timestamp"));
	// timestamp *= 1000;
	// Calendar calendar = Calendar.getInstance();
	// calendar.setTime(getDay());
	// calendar.add(Calendar.DAY_OF_YEAR, 1);
	// long day = getDay().getTime();
	// long dayafter = calendar.getTime().getTime();
	// if (timestamp >= day && timestamp <= dayafter) {
	// ThreadPool.runTask(new Runnable() {
	//
	// public void run() {
	// reloadDatabase();
	// }
	// });
	// } else {
	// int blub = 1;
	// blub++;
	// }
	// break;
	// case LOGIN:
	// // TODO login handle
	// break;
	// case LOGOUT:
	// // TODO logout handle
	// break;
	//
	// default:
	// fireError(new UnkownMessageType(STOMPConnection.class));
	// break;
	// }
	// }

}
