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

import java.net.URL;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.*;

import cartago.*;
import cartago.security.SecurityException;



/**
 * Remote class wrapping the access to the agent body 
 * 
 * @author aricci, mpiunti
 */
public class AgentBodyRemote extends UnicastRemoteObject implements IAgentBodyRemote {
    
    public AgentBody ctx;  
	private long lastPingFromMind;
	
    
        /** Creates a new instance of CartagoRemoteContext */
    public AgentBodyRemote() throws SecurityException, RemoteException  {
        super();  
    }
    
    public AgentBodyRemote(AgentBody ctx) throws SecurityException, RemoteException  {
        super();  
        this.ctx = ctx;
        lastPingFromMind = System.currentTimeMillis();
    }

	/**
	 * Get user id inside the workspace
	 * 
	 * @return
	 */
	public AgentId getAgentId() throws CartagoException, RemoteException {
		return ctx.getAgentId();
	}

	public WorkspaceId getWorkspaceId() throws SecurityException, RemoteException, CartagoException {
		// TODO Auto-generated method stub
		return ctx.getWorkspaceId();
	}

	public void doAction(long agentCallbackId, ArtifactId id, Op op, IAlignmentTest test, long timeout) throws RemoteException, CartagoException {
		// TODO Auto-generated method stub
		ctx.doAction(agentCallbackId, id, op, test, timeout);
	}
	
	public void doAction(long agentCallbackId, String name, Op op, IAlignmentTest test, long timeout) throws RemoteException, CartagoException {
		// TODO Auto-generated method stub
		ctx.doAction(agentCallbackId, name, op, test, timeout);
	}

	
	public synchronized void  ping() throws RemoteException {
		lastPingFromMind = System.currentTimeMillis();
		/*
		try {
			System.out.println("PING for body "+ctx.getUserId().getAgentName());
		} catch (Exception ex){
			ex.printStackTrace();
		}*/
	}
	
	synchronized long getLastPing(){
		return lastPingFromMind;
	}	
	
	AgentBody getContext(){
		return ctx;
	}

	@Override
	public ArtifactId getArtifactIdFromOp(Op op) {
		return ctx.getArtifactIdFromOp(op);
	}

	@Override
	public ArtifactId getArtifactIdFromOp(String name, Op op) {
		return ctx.getArtifactIdFromOp(name,op);
	}


}
