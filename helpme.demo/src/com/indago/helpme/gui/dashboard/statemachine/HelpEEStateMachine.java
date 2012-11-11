package com.indago.helpme.gui.dashboard.statemachine;

import java.util.ArrayList;

public class HelpEEStateMachine extends AStateMachine {
	private static String LOGTAG = HelpEEStateMachine.class.getSimpleName();

	/*
	 * HelpEEStateMachine is a singleton! Retrieve the instance with the
	 * static function getInstance();
	 */

	private static HelpEEStateMachine instance = new HelpEEStateMachine();

	public static HelpEEStateMachine getInstance() {
		return instance;
	}

	/*
	 * 
	 */

	private HelpEEStateMachine() {
		observerList = new ArrayList<IStateAwareView>(5);
		mState = STATES.SHIELDED;
	}

	@Override
	synchronized public Enum<?> nextState() {
		switch((STATES) mState) {
			case SHIELDED:
				mState = STATES.PART_SHIELDED;
				break;
			case PART_SHIELDED:
				mState = STATES.UNSHIELDED;
				break;
			case UNSHIELDED:
				mState = STATES.PRESSED;
				break;
			case PRESSED:
				mState = STATES.LOCKED;
				break;
			case LOCKED:
				//mState = STATES.CALLCENTER;
				break;
			case CALLCENTER:
				mState = STATES.CALLCENTER_PRESSED;
				break;
			case CALLCENTER_PRESSED:
				mState = STATES.FINISHED;
				break;
			case HELP_INCOMMING:
				mState = STATES.FINISHED;
				break;
			default:
				mState = STATES.SHIELDED;
				break;
		}

		updateAll();

		return mState;
	}

	@Override
	synchronized public void setState(Enum<?> state) {
		if(state != null && state instanceof STATES) {
			mState = (STATES) state;
			updateAll();
		}

	}
}
