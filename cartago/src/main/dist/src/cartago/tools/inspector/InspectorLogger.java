package cartago.tools.inspector;

import cartago.AgentId;
import cartago.ArtifactId;
import cartago.ArtifactObsProperty;
import cartago.ICartagoLogger;
import cartago.IEventFilter;
import cartago.Op;
import cartago.OpId;
import cartago.Tuple;

public class InspectorLogger implements  ICartagoLogger {

	private Inspector insp;
	private WorkspaceScene scene;
	
	public InspectorLogger(Inspector insp){
		this.insp = insp;
		scene = insp.getWorkspaceScene();
	}
	
	@Override
	public void agentJoined(long when, AgentId id) {
		// insp.notifyEvent(new AgentJoined(when,id));
		scene.addAgent(id);
	}

	@Override
	public void agentQuit(long when, AgentId id) {
		// insp.notifyEvent(new AgentQuit(when,id));
		scene.removeAgent(id);
	}

	@Override
	public void artifactCreated(long when, ArtifactId id, AgentId creator) {
		// insp.notifyEvent(new ArtifactCreated(when,id,creator));
		scene.addArtifact(id,creator);
	}

	@Override
	public void artifactDisposed(long when, ArtifactId id, AgentId disposer) {
		// insp.notifyEvent(new ArtifactDisposed(when,id,disposer));
		scene.removeArtifact(id);
	}
	
	@Override
	public void artifactFocussed(long when, AgentId who, ArtifactId id,
			IEventFilter ev) {
		// TODO Auto-generated method stub
		scene.addFocus(who, id);
	}

	@Override
	public void artifactNoMoreFocussed(long when, AgentId who, ArtifactId id) {
		// TODO Auto-generated method stub
	}

	@Override
	public void artifactsLinked(long when, AgentId id, ArtifactId linking,
			ArtifactId linked) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void newPercept(long when, ArtifactId aid, Tuple signal,
			ArtifactObsProperty[] added, ArtifactObsProperty[] removed,
			ArtifactObsProperty[] changed) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void opCompleted(long when, OpId oid, ArtifactId aid, Op op) {
		// insp.notifyEvent(new OpCompleted(when,aid,op));
		scene.changeOpStateToCompleted(oid);
	}

	@Override
	public void opFailed(long when, OpId oid, ArtifactId aid, Op op, String msg,
			Tuple descr) {
		scene.changeOpStateToFailed(oid);
	}

	@Override
	public void opRequested(long when, AgentId who, ArtifactId aid, Op op) {
		// insp.notifyEvent(new OpRequested(when,who,aid,op));
		//System.out.println("OP REQ "+op+" by "+who);
	}

	@Override
	public void opResumed(long when, OpId oid, ArtifactId aid, Op op) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void opStarted(long when, OpId oid, ArtifactId aid, Op op) {
		// insp.notifyEvent(new OpStarted(when,aid,op,oid));
		scene.addOngoingOp(oid.getAgentBodyId(), aid, op, oid);
		//System.out.println("OP STARTED "+op+" by "+oid.getOpName());
	}

	@Override
	public void opSuspended(long when, OpId oid, ArtifactId aid, Op op) {
		// TODO Auto-generated method stub
		
	}

}
