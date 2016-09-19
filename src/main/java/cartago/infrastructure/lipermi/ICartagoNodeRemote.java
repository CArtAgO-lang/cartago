package cartago.infrastructure.lipermi;

import cartago.AgentCredential;
import cartago.AgentId;
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

	IAgentBodyRemote join(String wspName, AgentCredential cred, ICartagoCallbackRemote callback) throws CartagoException;
	void quit(String wspName, AgentId id) throws CartagoException;
	OpId execInterArtifactOp(ICartagoCallbackRemote callback, long callbackId, AgentId userId, ArtifactId srcId, ArtifactId targetId, Op op, long timeout, IAlignmentTest test) throws CartagoException;
	String getVersion() throws CartagoException;
	NodeId getNodeId() throws CartagoException;
	
}
