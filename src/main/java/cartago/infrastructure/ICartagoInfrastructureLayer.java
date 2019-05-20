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
package cartago.infrastructure;

import cartago.*;

/**
 * CArtAgO Infrastructure Layer interface.
 * 
 * Infrastructure layers allow for interacting with remote nodes
 * and for remote agents to work within the node.
 * 
 * @author aricci
 *
 */
public interface ICartagoInfrastructureLayer {
		

	/**
	 * Join a remote workspace using this service
	 * 
	 * @param wspName	workspace name
	 * @param address	node address
	 * @param cred	agent credential
	 * @param eventListener listener to perceive workspace events
	 * @return
	 * @throws CartagoInfrastructureLayerException
	 * @throws CartagoException
	 */
	ICartagoContext joinRemoteWorkspace(String wspName, String address, AgentCredential cred, ICartagoCallback eventListener) throws CartagoInfrastructureLayerException, CartagoException;
		
	/**
	 * Execute an linked operation from a local artifact to a target remote artifact using this service
	 * 
	 * @param callback	
	 * @param callbackId
	 * @param userId
	 * @param srcId
	 * @param targetId
	 * @param address
	 * @param op
	 * @param timeout
	 * @param test
	 * @return
	 * @throws CartagoInfrastructureLayerException
	 * @throws CartagoException
	 */
	OpId execRemoteInterArtifactOp(ICartagoCallback callback, long callbackId, AgentId userId, ArtifactId srcId, ArtifactId targetId, String address, Op op, long timeout, IAlignmentTest test) throws CartagoInfrastructureLayerException, CartagoException;
	
	/**
	 * Get the identifier of the CArtAgO node running at the specified address
	 * 
	 * @param address address of the node
	 * @return
	 * @throws CartagoInfrastructureLayerException
	 * @throws CartagoException
	 */
	// NodeId getNodeAt(String address) throws CartagoInfrastructureLayerException, CartagoException;

	/**
	 * Shutdown the layer
	 * 
	 * @throws CartagoException
	 */
	void shutdownLayer() throws CartagoException;
	
	/**
	 * Register a new logger on a remote wsp
	 * 
	 * @param wspName wsp name
	 * @param address address
	 * @param logger the logger
	 * @throws CartagoException
	 */
	void registerLoggerToRemoteWsp(String wspName, String address, ICartagoLogger logger) throws CartagoException;
	
	/**
	 * Start the infrastructure service to enable the access by remote agents 
	 * 
	 * @param node CArtAgO environment
	 * @param address address of the service
	 * 
	 * @throws CartagoInfrastructureLayerException
	 */
	void startService(String address) throws CartagoInfrastructureLayerException;
	
	/**
	 * Check if the the infrastructure service is running
	 * 
	 * @return
	 */
	boolean isServiceRunning();
	
	/**
	 * Shutdown the infrastructure service
	 * 
	 * @throws CartagoException
	 */
	void shutdownService() throws CartagoException;
}
