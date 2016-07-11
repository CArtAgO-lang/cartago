package cartago.tools;

import java.util.*;

public class EventOpInfo {

	private String methodName;
	private java.util.EventObject event;
	
	public EventOpInfo(String methodName, EventObject event){
		this.methodName = methodName;
		this.event = event;
	}
	
	public String getListenerName() {
		return methodName;
	}

	public EventObject getEvent() {
		return event;
	}

}
