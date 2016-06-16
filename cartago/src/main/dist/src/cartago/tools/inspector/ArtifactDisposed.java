package cartago.tools.inspector;

import cartago.*;

public class ArtifactDisposed extends LoggerEvent{

	private AgentId disposer;
	private ArtifactId artifactId;
	
	public ArtifactDisposed(long when, ArtifactId id, AgentId disposer){
		super(when);
		artifactId = id;
		this.disposer = disposer;
	}
	
	public ArtifactId getArtifactId(){
		return artifactId;
	}
	
	public AgentId getDisposerId(){
		return disposer;
	}
	
}
