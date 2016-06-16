package cartago.tools.inspector;

import cartago.*;

public class OpCompleted extends LoggerEvent{

	private ArtifactId artifactId;
	private OpId oid;
	
	public OpCompleted(long when, ArtifactId target, OpId oid){
		super(when);
		artifactId = target;
		this.oid = oid;
	}
	
	public ArtifactId getArtifactId(){
		return artifactId;
	}

	public OpId getOpId(){
		return oid;
	}
	
}
