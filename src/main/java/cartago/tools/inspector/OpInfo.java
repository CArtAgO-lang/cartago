package cartago.tools.inspector;

import cartago.*;

public class OpInfo {

	private ArtifactId targetId;
	private Op op;
	private AgentId agentId;
	private OpId oid;
	public static enum OpState { requested, started, completed, failed};
	private OpState opState;
	private long timestamp;
	
	public OpInfo(AgentId agentId, ArtifactId targetId, Op op){
		this.agentId = agentId;
		this.targetId = targetId;
		this.op = op;
		opState = OpState.requested;
		timestamp = System.currentTimeMillis();
	}

	public boolean hasCompleted(){
		return opState.equals(OpState.completed);
	}

	public boolean hasFailed(){
		return opState.equals(OpState.failed);
	}
	
	public long getTimestamp(){
		return timestamp;
	}
	
	public boolean hasStarted(){
		return opState.equals(OpState.started);
	}

	public void changeStateToStarted(OpId oid){
		opState = OpState.started;
		this.oid = oid;
		timestamp = System.currentTimeMillis();
	}

	public void changeStateToCompleted(OpId oid){
		opState = OpState.completed;
		timestamp = System.currentTimeMillis();		
	}
	
	public void changeStateToFailed(OpId oid){
		opState = OpState.failed;
		timestamp = System.currentTimeMillis();
	}

	public Op getOp(){
		return op;
	}
	
	public OpId getOpId(){
		return oid;
	}
	
	public ArtifactId getTargetId(){
		return targetId;
	}
	
	public AgentId getAgentId(){
		return agentId;
	}
	
	public OpState getOpState(){
		return opState;
	}
}
