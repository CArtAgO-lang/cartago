package cartago.infrastructure.lipermi;

import java.io.IOException;

import lipermi.net.Client;
import cartago.AgentId;
import cartago.ArtifactId;
import cartago.CartagoException;
import cartago.IAlignmentTest;
import cartago.ICartagoContext;
import cartago.Op;
import cartago.WorkspaceId;

/**
 * 
 * @author mguidi
 *
 */
public class AgentBodyProxy implements ICartagoContext {

	private static final long serialVersionUID = 1L;
	private IAgentBodyRemote mCtx;
	private Client mClient;
	
	public AgentBodyProxy(IAgentBodyRemote ctx, Client client) {
		mCtx = ctx;
		mClient = client;
	}
	

	public void doAction(long actionId, Op op, IAlignmentTest test, long timeout) throws CartagoException {
		mCtx.doAction(actionId, op, test, timeout);
	}
	
	@Override
	public void doAction(long actionId, String name, Op op,
			IAlignmentTest test, long timeout) throws CartagoException {
		
		mCtx.doAction(actionId, name, op, test, timeout);
	}

	@Override
	public AgentId getAgentId() throws CartagoException {
		return mCtx.getAgentId();
	}

	@Override
	public WorkspaceId getWorkspaceId() throws CartagoException {
		return mCtx.getWorkspaceId();
	}
	
	public void ping() throws CartagoException {
		mCtx.ping();
	}
	
	public void close() throws CartagoException {
		try {
			mClient.close();
		} catch (IOException e) {
			throw new CartagoException();
		}
	}


/*	public ArtifactId getArtifactIdFromOp(Op op) {
		return mCtx.getArtifactIdFromOp(op);
	}


	@Override
	public ArtifactId getArtifactIdFromOp(String name, Op op) {
		return mCtx.getArtifactIdFromOp(name,op);
	}
*/

	

}
