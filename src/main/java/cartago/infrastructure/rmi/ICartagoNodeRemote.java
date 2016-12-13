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

import java.rmi.*;

import cartago.*;
import cartago.security.*;
import cartago.topology.WorkspaceTree;


/**
 * Interface CArtAgO node service
 *  
 * @author aricci
 *
 */
public interface ICartagoNodeRemote extends Remote {

	ICartagoContext join(String wspName, AgentCredential cred, ICartagoCallback callback) throws RemoteException, CartagoException;

	void registerLogger(String wspName, ICartagoLogger logger) throws RemoteException, CartagoException;
	
	void quit(String wspName, AgentId id) throws RemoteException, CartagoException;

	OpId execInterArtifactOp(ICartagoCallback callback, long callbackId, AgentId userId, ArtifactId srcId, ArtifactId targetId, Op op, long timeout, IAlignmentTest test) throws RemoteException, CartagoException;

	String getVersion() throws CartagoException, RemoteException;
	
	NodeId getNodeId() throws CartagoException, RemoteException;

    WorkspaceId getMainWorkspaceId() throws CartagoException, RemoteException;
    //added by xavier
    CartagoWorkspace createWorkspace(String wspName) throws CartagoException;

    void setTree(WorkspaceTree tree);
    
}
