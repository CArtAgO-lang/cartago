/**
 * CArtAgO - DEIS, University of Bologna
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package cartago.infrastructure.rmi;

import java.io.Serializable;
import java.net.URL;
import java.rmi.*;

import cartago.AgentCredential;
import cartago.AgentId;
import cartago.ArtifactConfig;
import cartago.ArtifactId;
import cartago.ArtifactObsProperty;
import cartago.CartagoException;
import cartago.IAlignmentTest;
import cartago.ICartagoContext;
import cartago.ICartagoCallback;
import cartago.Manual;
import cartago.Op;
import cartago.OpId;
import cartago.OpRequestTimeoutException;
import cartago.OperationException;
import cartago.OperationUnavailableException;
import cartago.WorkspaceId;
import cartago.security.SecurityException;


/**
 * Class used to adapt Agent Body remote interface to Agent Body
 * interface.
 * 
 * @author aricci
 * 
 */
public class AgentBodyProxy implements ICartagoContext, Serializable {

	private IAgentBodyRemote ctx;
	private WorkspaceId wspId;

	AgentBodyProxy(IAgentBodyRemote ctx) throws CartagoException {
		// ctx.focus(((AbstractAgentCallback)actx).getProxy(), id, tag);
		this.ctx = ctx;
		try {
			wspId = ctx.getWorkspaceId();
		} catch (RemoteException ex) {
			throw new CartagoException(ex.getMessage());
		}
	}

	public AgentId getAgentId() throws CartagoException {
		try {
			return ctx.getAgentId();
		} catch (RemoteException ex) {
			throw new CartagoException(ex.getMessage());
		}
	}

	public WorkspaceId getWorkspaceId() throws CartagoException {
		return wspId;
	}

	public void doAction(long agentCallbackId, ArtifactId id, Op op, IAlignmentTest test,
			long timeout) throws CartagoException {
		try {
			ctx.doAction(agentCallbackId, id.getName(), op, test, timeout);			
		} catch (RemoteException ex) {
			throw new CartagoException(ex.getMessage());
		}

	}
	
	public void doAction(long agentCallbackId, String id, Op op, IAlignmentTest test, long timeout) throws CartagoException {
		try {
			ctx.doAction(agentCallbackId, id, op, test, timeout);			
		} catch (RemoteException ex) {
			throw new CartagoException(ex.getMessage());
		}

	}

	@Override
	public void doAction(long agentCallbackId, Op op, IAlignmentTest test, long timeout) throws CartagoException {
		try {
			ctx.doAction(agentCallbackId, op, test, timeout);			
		} catch (RemoteException ex) {
			throw new CartagoException(ex.getMessage());
		}
	}
	
	public void ping() throws CartagoException {
		try {
			ctx.ping();
		} catch (RemoteException ex) {
			throw new CartagoException(ex.getMessage());
		}
	}


	/*
	public ArtifactId getArtifactIdFromOp(Op op) throws CartagoException {
		try {
			return ctx.getArtifactIdFromOp(op);			
		} catch (RemoteException ex) {
			throw new CartagoException(ex.getMessage());
		}	
	}

	public ArtifactId getArtifactIdFromOp(String name, Op op) throws CartagoException {
		// TODO Auto-generated method stub
		try {
			return ctx.getArtifactIdFromOp(name,op);			
		} catch (RemoteException ex) {
			throw new CartagoException(ex.getMessage());
		}	
	}*/
	
}
