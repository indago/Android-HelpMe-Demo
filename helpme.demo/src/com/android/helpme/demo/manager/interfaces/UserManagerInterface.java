package com.android.helpme.demo.manager.interfaces;

import java.util.ArrayList;

import android.app.Activity;

import com.android.helpme.demo.utils.User;
import com.android.helpme.demo.utils.UserInterface;

public interface UserManagerInterface {

	public  void addUser(User position);

	public  ArrayList<User> getUsers();

	public  UserInterface getUserByName(String userName);
	public  UserInterface getUserById(String id);

	public  Runnable readUserFromProperty(Activity activity);

}