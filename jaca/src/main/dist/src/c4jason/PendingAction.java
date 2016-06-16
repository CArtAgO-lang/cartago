package c4jason;

import jason.asSemantics.ActionExec;
import jason.asSyntax.*;
import cartago.*;

public class PendingAction {

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
