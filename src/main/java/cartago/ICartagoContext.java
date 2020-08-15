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
package cartago;

import cartago.util.agent.ArtifactObsProperty;

/**
 * Cartago Context Interface - set of primitives to work inside a workspace.
 * 
 * @author aricci 
 */
public interface ICartagoContext {
    
	
	/**
	 * Use an artifact by requesting the execution of the specified operation.
	 * 
	 * 
	 * @param actionId identifier of the use action - used by the callback
	 * @param name target artifact (NOTE THAT this is like the full id, being inside a wsp)
	 * @param op operation to execute
	 * @param test alignment test - null if not specified
	 * @param timeout operation timeout - -1 if not specified
	 * @return true if an artifact with the specified name and operation is found
	 */
	void doAction(long actionId, String name, Op op, IAlignmentTest test, long timeout) throws CartagoException;
		
	/**
	 * Use an artifact by requesting the execution of the specified operation.
	 * 
	 * @param actionId identifier of the use action - used by the callback
	 * @param wid  identifier of the target workspace
	 * @param op operation to execute
	 * @param test alignment test - null if not specified
	 * @param timeout operation timeout - -1 if not specified
	 * @return true if an artifact with the specified name and operation is found
	 */
	void doAction(long actionId, Op op, IAlignmentTest test, long timeout) throws CartagoException;
	
	
	/**
	 * Try to perform an action in the workspace. If there are no artifacts 
	 * providing an operation corresponding to the action, the request is not processed
	 * and no failures are generated (false is returned).
	 * 
	 * @param actionId identifier of the use action - used by the callback
	 * @param op operation to execute
	 * @param test alignment test - null if not specified
	 * @param timeout operation timeout - -1 if not specified
	 * @return true if the action has processed
	 * @throws CartagoException
	 */
	boolean doTryAction(long actionId, Op op, IAlignmentTest test, long timeout) throws CartagoException;

	/**
	 * Quit from the workspace.
	 */
	void quit() throws CartagoException;
	
	/**
	 * Get workspace id
	 * 
	 * @return
	 */
	WorkspaceId getWorkspaceId()  throws CartagoException;
	
	/**
	 * Get agent id in the workspace
	 * 
	 * @return
	 */
	AgentId getAgentId() throws CartagoException;
	
	
}

