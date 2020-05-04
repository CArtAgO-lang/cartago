package jaca;

import java.io.Serializable;

import jason.asSemantics.ActionExec;
import jason.asSyntax.Structure;

public class PendingAction implements Serializable {

	private long actionId;
	private Structure action;
	private ActionExec actionExec;
	
	public PendingAction(long actionId, Structure action, ActionExec actionExec){
		this.action = action;
		this.actionExec = actionExec;
		this.actionId = actionId;
	}

	public long getActionId(){
		return actionId;
	}
	
	public Structure getAction(){
		return action;
	}
	
	public ActionExec getActionExec(){
		return actionExec;
	}

}
