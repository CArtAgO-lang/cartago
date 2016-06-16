package cartago.tools.inspector;

abstract public class LoggerEvent {

	private long timestamp;
	
	LoggerEvent(long timestamp){
		this.timestamp = timestamp;
	}
	
}
