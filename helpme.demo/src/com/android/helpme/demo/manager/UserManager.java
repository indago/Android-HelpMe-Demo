package com.android.helpme.demo.manager;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.app.Activity;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.provider.Settings.Secure;
import android.util.Log;

import com.android.helpme.demo.manager.interfaces.UserManagerInterface;
import com.android.helpme.demo.messagesystem.AbstractMessageSystem;
import com.android.helpme.demo.messagesystem.AbstractMessageSystemInterface;
import com.android.helpme.demo.messagesystem.InAppMessage;
import com.android.helpme.demo.messagesystem.MESSAGE_TYPE;
import com.android.helpme.demo.utils.User;
import com.android.helpme.demo.utils.UserInterface;

/**
 * 
 * @author Andreas Wieland
 * 
 */
public class UserManager extends AbstractMessageSystem implements UserManagerInterface {
	private static final String LOGTAG = UserManager.class.getSimpleName();
	private static final String USER_PROPERTIES = "user.properties";
	private static UserManager manager;
	private InAppMessage message;
	private HashMap<String,User> users;
	private UserInterface thisUser;
	private boolean userSet;

	public static UserManager getInstance() {
		if (manager == null) {
			manager = new UserManager();
		}
		return manager;
	}

	private UserManager() {
		users = new HashMap<String, User>();
		userSet = false;
	}
	
	public boolean isUserSet(){
		return userSet;
	}

	public UserInterface getThisUser() {
		return thisUser;
	}

	public void setThisUser(UserInterface user, String id) {
		if (!userSet) {
			this.thisUser = new User(id, user.getName(), user.getHelfer());
			userSet =true;
		}
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
		this.message = inAppMessage;
	}

	@Override
	public AbstractMessageSystemInterface getManager() {
		return manager;
	}

	/* (non-Javadoc)
	 * @see com.android.helpme.demo.manager.UserManagerInterface#addUser(com.android.helpme.demo.utils.User)
	 */
	@Override
	public void addUser(User user) {
		if (users.containsKey(user.getId())) {
			users.get(user.getId()).updatePosition(user.getPosition());
		}else{
			users.put(user.getId(), user);
		}
	}

	/* (non-Javadoc)
	 * @see com.android.helpme.demo.manager.UserManagerInterface#getUsers()
	 */
	@Override
	public ArrayList<User> getUsers() {
		ArrayList<User> list = new ArrayList<User>();
		Set<String> keys = users.keySet();
		for (String key : keys) {
			list.add(users.get(key));
		}
		return list;
	}

	/* (non-Javadoc)
	 * @see com.android.helpme.demo.manager.UserManagerInterface#getUser(java.lang.String)
	 */
	@Override
	public UserInterface getUserByName(String name) {
		Set<String> keys = users.keySet();
		for (String key : keys) {
			User user = users.get(key);
			if (user.getName().equalsIgnoreCase(name)) {
				return user;
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.android.helpme.demo.manager.UserManagerInterface#readUserFromProperty(android.app.Activity)
	 */
	@Override
	public Runnable readUserFromProperty(final Activity activity) {
		return new Runnable() {

			@Override
			public void run() {
				Resources resources = activity.getResources();
				AssetManager assetManager = resources.getAssets();

				// Read from the /assets directory
				try {
					InputStream inputStream = assetManager.open(USER_PROPERTIES);
					Reader reader = new InputStreamReader(inputStream);
					JSONParser parser = new JSONParser();
					Properties properties = new Properties();
					properties.load(reader);
					Set<Object> set = properties.keySet();
					for (Object key : set) {
						String string = (String) properties.get(key);
						JSONObject object = (JSONObject) parser.parse(string);
						addUser(new User(object));
					}
					Log.i(LOGTAG, "The properties are now loaded");
					fireMessageFromManager(users, MESSAGE_TYPE.USER);
				} catch (IOException e) {
					fireError(e);
				} catch (ParseException e) {
					fireError(e);
				}

			}
		};
	}
	
	public Runnable clear() {
		return new Runnable() {
			
			@Override
			public void run() {
				synchronized (users) {
					users.clear();
				}
			}
		};
		
	}

	@Override
	public UserInterface getUserById(String id) {
		return users.get(id);
	}

}
