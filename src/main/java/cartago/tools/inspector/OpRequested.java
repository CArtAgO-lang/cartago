package cartago.tools.inspector;

import cartago.*;

public class OpRequested extends LoggerEvent{

	private AgentId creatorId;
	private ArtifactId artifactId;
	private Op op;
	
	public OpRequested(long when, AgentId who, ArtifactId target, Op op){
		super(when);
		artifactId = target;
		creatorId = who;
		this.op = op;
	}
	
	public Op getOp(){
		return op;
	}
	
	public ArtifactId getArtifactId(){
		return artifactId;
	}
	
	public AgentId getAgentId(){
		return creatorId;
	}
	
}
