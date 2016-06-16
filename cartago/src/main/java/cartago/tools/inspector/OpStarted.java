package cartago.tools.inspector;

import cartago.*;

public class OpStarted extends LoggerEvent{

	private ArtifactId artifactId;
	private OpId opid;
	private Op op;
	
	public OpStarted(long when, ArtifactId target, Op op, OpId opid){
		super(when);
		artifactId = target;
		this.opid = opid;
		this.op = op;
	}
	
	public Op getOp(){
		return op;
	}
	
	public ArtifactId getArtifactId(){
		return artifactId;
	}
	
	public OpId getOpId(){
		return opid;
	}
	
}
