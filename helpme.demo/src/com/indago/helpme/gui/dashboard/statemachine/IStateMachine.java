package com.indago.helpme.gui.dashboard.statemachine;


public interface IStateMachine {

	public Enum<?> nextState();

	public Enum<?> getState();
	
	public void setState(Enum<?> state);

	public void updateAll();

	public void addOne(IStateAwareView view);

	public void removeOne(IStateAwareView view);

	public void removeAll();

}
