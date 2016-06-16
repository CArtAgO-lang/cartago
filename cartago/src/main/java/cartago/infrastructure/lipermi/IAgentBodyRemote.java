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
	boolean doAction(long actionId, Op op, IAlignmentTest test, long timeout) throws  CartagoException;
	void doAction(long actionId, ArtifactId id, Op op, IAlignmentTest test, long timeout) throws  CartagoException;
	boolean doAction(long actionId, String name, Op op, IAlignmentTest test, long timeout) throws  CartagoException;
	WorkspaceId getWorkspaceId() throws  CartagoException;
	void ping();
	
}

