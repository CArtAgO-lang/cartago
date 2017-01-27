package cartago.infrastructure.lipermi;

import cartago.AgentCredential;
import cartago.AgentId;
import cartago.WorkspaceId;
import cartago.ArtifactId;
import cartago.CartagoException;
import cartago.IAlignmentTest;
import cartago.NodeId;
import cartago.Op;
import cartago.OpId;


/**
 * Interface CArtAgO node service
 *  
 * @author mguidi
 *
 */
public interface ICartagoNodeRemote {

	IAgentBodyRemote join(WorkspaceId wId, AgentCredential cred, ICartagoCallbackRemote callback) throws CartagoException;
	void quit(WorkspaceId wId, AgentId id) throws CartagoException;
	OpId execInterArtifactOp(ICartagoCallbackRemote callback, long callbackId, AgentId userId, ArtifactId srcId, ArtifactId targetId, Op op, long timeout, IAlignmentTest test) throws CartagoException;
	String getVersion() throws CartagoException;
	NodeId getNodeId() throws CartagoException;
	
}
