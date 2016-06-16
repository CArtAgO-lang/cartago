package cartago.tools.inspector;

import cartago.*;

public class FocussedArtifactInfo {

	private ArtifactId targetId;
    private AgentId agentId;
    
	public FocussedArtifactInfo(AgentId agentId, ArtifactId targetId){
		this.agentId = agentId;
		this.targetId = targetId;
	}
	
	public ArtifactId getTargetId(){
		return targetId;
	}
	
	public AgentId getAgentId(){
		return agentId;
	}
}
