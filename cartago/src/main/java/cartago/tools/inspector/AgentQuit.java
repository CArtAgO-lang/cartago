package cartago.tools.inspector;

import cartago.*;

public class AgentQuit extends LoggerEvent{

	private AgentId agentId;
	
	public AgentQuit(long when, AgentId id){
		super(when);
		agentId = id;
	}
	
	public AgentId getAgentId(){
		return agentId;
	}
	
}
