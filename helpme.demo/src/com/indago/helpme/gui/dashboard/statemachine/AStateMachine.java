package com.indago.helpme.gui.dashboard.statemachine;

import java.util.ArrayList;
import java.util.Iterator;


public abstract class AStateMachine implements IStateMachine {

	protected volatile ArrayList<IStateAwareView> observerList;
	protected volatile STATES mState;

	@Override
	public Enum<?> nextState() {
		return null;
	}

	@Override
	public void setState(Enum<?> state) {
		// TODO Auto-generated method stub

	}

	@Override
	synchronized public Enum<?> getState() {
		return mState;
	}

	@Override
	synchronized public void addOne(IStateAwareView view) {
		observerList.add(view);
		view.setState(this);

	}

	@Override
	synchronized public void removeOne(IStateAwareView view) {
		observerList.remove(view);

	}

	@Override
	synchronized public void removeAll() {
		observerList.clear();
	}

	@Override
	synchronized public void updateAll() {
		for(Iterator<IStateAwareView> iter = observerList.iterator(); iter.hasNext();) {
			iter.next().setState(this);
		}
	}

}
