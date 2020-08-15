package cartago.infrastructure.lipermi;

import java.io.Serializable;

import cartago.AgentId;
import cartago.ArtifactId;
import cartago.CartagoException;
import cartago.IAlignmentTest;
import cartago.Op;
import cartago.WorkspaceId;

/**
 * Interface for remote contexts
 *
 * @author mguidi
 *
 */
public interface IAgentBodyRemote extends Serializable {
    
	AgentId getAgentId() throws CartagoException;
	
	void doAction(long actionId, String name, Op op, IAlignmentTest test, long timeout) throws  CartagoException;
	void doAction(long actionId, Op op, IAlignmentTest test, long timeout) throws  CartagoException;
	boolean doTryAction(long actionId, Op op, IAlignmentTest test, long timeout) throws  CartagoException;
	void quit();
	
	WorkspaceId getWorkspaceId() throws  CartagoException;

	// ArtifactId getArtifactIdFromOp(Op op);
	// ArtifactId getArtifactIdFromOp(String name, Op op);
	void ping();
	
}

