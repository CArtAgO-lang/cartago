package cartago.tools.inspector;

import cartago.*;

public class ArtifactCreated extends LoggerEvent{

	private AgentId creatorId;
	private ArtifactId artifactId;
	
	public ArtifactCreated(long when, ArtifactId id, AgentId creator){
		super(when);
		artifactId = id;
		this.creatorId = creator;
	}
	
	public ArtifactId getArtifactId(){
		return artifactId;
	}
	
	public AgentId getCreatorId(){
		return creatorId;
	}
	
}
