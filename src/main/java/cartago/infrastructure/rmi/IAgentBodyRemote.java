/**
 * CArtAgO - DISI, University of Bologna
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
import java.rmi.Remote;
import java.rmi.RemoteException;

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
import cartago.WorkspaceId;



/**
 * Interface for remote contexts
 *
 * @author aricci
 *
 */
public interface IAgentBodyRemote extends Remote {
    
	/**
	 * Get user id inside the workspace
	 * 
	 * @return
	 */
	AgentId getAgentId() throws CartagoException, RemoteException;

	void doAction(long actionId, String name, Op op, IAlignmentTest test, long timeout) throws  RemoteException, CartagoException;
	void doAction(long actionId, Op op, IAlignmentTest test, long timeout) throws  RemoteException, CartagoException;
	void quit() throws  RemoteException, CartagoException;

	// ArtifactId getArtifactIdFromOp(Op op) throws  RemoteException, CartagoException;
	// ArtifactId getArtifactIdFromOp(String name, Op op) throws  RemoteException, CartagoException;

	/**
	 * Get workspace id
	 *
	 */
	WorkspaceId getWorkspaceId() throws  RemoteException, CartagoException;

	// Working with workspaces
	
	/**
	 * Ping for signaling mind existence
	 * 
	 * @throws RemoteException
	 */
	void ping() throws RemoteException;

}

