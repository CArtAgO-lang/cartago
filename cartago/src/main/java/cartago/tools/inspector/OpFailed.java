package cartago.tools.inspector;

import cartago.*;

public class OpFailed extends LoggerEvent{

	private ArtifactId artifactId;
	private String msg;
	private Tuple desc;
	private OpId oid;
	
	public OpFailed(long when, ArtifactId target, OpId oid, String msg, Tuple desc){
		super(when);
		artifactId = target;
		this.msg = msg;
		this.desc = desc;
		this.oid = oid;
	}
	
	public ArtifactId getArtifactId(){
		return artifactId;
	}
	
	public String getMsg(){
		return msg;
	}
	
	public OpId getOpId(){
		return oid;
	}
	
	public Tuple getDescription(){
		return desc;
	}
}
