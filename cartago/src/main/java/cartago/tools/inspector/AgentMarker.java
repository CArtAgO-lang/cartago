package cartago.tools.inspector;

import cartago.*;

public class AgentMarker extends Marker {

	private AgentId id;
	
	public AgentMarker(AgentId id, P2d pos){
		super(pos);
		this.id = id;
	}
	
	public AgentId getAgentId(){
		return id;
	}
	
}
