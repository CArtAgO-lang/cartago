package cartago.tools.inspector;

import cartago.*;

public class AgentJoined extends LoggerEvent{

	private AgentId agentId;
	
	public AgentJoined(long when, AgentId id){
		super(when);
		agentId = id;
	}
	
	public AgentId getAgentId(){
		return agentId;
	}
	
}
