package cartago.util.agent;

import cartago.*;
import cartago.events.*;

/**
 * Future to access info about the action feedback
 * 
 * @author aricci
 *
 */
public class ActionFeedback {

	private ActionFeedbackQueue queue;
	private CartagoActionEvent actionEvent;
	private long actionId;
	
	public ActionFeedback(long actionId, ActionFeedbackQueue queue){
		this.actionId = actionId;
		this.queue = queue;
	}

	public void waitForCompletion() throws InterruptedException{
		actionEvent = queue.waitFor(actionId);
	}

	public boolean succeeded() throws ActionNotCompletedException {
		if (actionEvent != null){
			if (actionEvent instanceof ActionSucceededEvent){
				return true;
			} else {
				return false;
			}
		} else {
			throw new ActionNotCompletedException();
		}
	}

	public boolean failed() throws ActionNotCompletedException {
		if (actionEvent != null){
			if (actionEvent instanceof ActionFailedEvent){
				return true;
			} else {
				return false;
			}
		} else {
			throw new ActionNotCompletedException();
		}
	}

	public Op getOp(){
		return actionEvent.getOp();
	}

	public CartagoActionEvent getActionEvent(){
		return actionEvent;
	}
}
