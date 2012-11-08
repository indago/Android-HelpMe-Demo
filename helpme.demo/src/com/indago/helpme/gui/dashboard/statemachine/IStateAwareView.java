package com.indago.helpme.gui.dashboard.statemachine;



public interface IStateAwareView {

	public void setState(IStateMachine stateMachine);

	public Enum<?> getState();

}
