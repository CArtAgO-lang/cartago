package cartago.util.agent;

import cartago.*;
import cartago.CartagoException;

/**
 * Exception raised when an action fail (doAction sync)
 * 
 * @author aricci
 *
 */
public class ActionFailedException extends CartagoException {
	
	private Tuple descr;
	private String msg;
	
	public ActionFailedException(String msg, Tuple descr){
		this.msg = msg;
		this.descr = descr;
	}
	
	/**
	 * Get the failure message
	 * 
	 * @return
	 */
	public String getFailureMsg(){
		return msg;
	}

	/**
	 * Get the failure description
	 * 
	 * @return
	 */
	public Tuple getFailureDescr(){
		return descr;
	}

}
