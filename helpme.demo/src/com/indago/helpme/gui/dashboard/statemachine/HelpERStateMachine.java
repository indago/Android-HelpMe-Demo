package com.indago.helpme.gui.dashboard.statemachine;

import java.util.ArrayList;



public class HelpERStateMachine extends AStateMachine {
	private static String LOGTAG = HelpERStateMachine.class.getSimpleName();

	/*
	 * HelpEEStateMachine is a singleton! Retrieve the instance with the
	 * static function getInstance();
	 */

	private static HelpERStateMachine instance = new HelpERStateMachine();

	public static HelpERStateMachine getInstance() {
		return instance;
	}

	/*
	 * 
	 */

	private HelpERStateMachine() {
		observerList = new ArrayList<IStateAwareView>(5);
		mState = STATES.DEFAULT;
	}


	@Override
	synchronized public void setState(Enum<?> state) {
		if(state != null && state instanceof STATES) {
			mState = (STATES) state;
			updateAll();
		}

	}
}
