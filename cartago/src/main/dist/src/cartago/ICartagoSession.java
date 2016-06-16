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
package cartago;

import java.util.List;

/**
 * Main interface for acting and perceiving in CArtAgO Environments.
 * 
 * @author aricci
 *
 */
public interface ICartagoSession {
	
	/**
	 * Executing an action, i.e. an operation over an artifact
	 * 
	 * @param aid target artifact
	 * @param op target operation
	 * @param test alignment test
	 * @param timeout timeout
	 * @return unique action identifier 
	 * @throws CartagoException
	 */
	long doAction(ArtifactId aid, Op op, IAlignmentTest test, long timeout) throws CartagoException;
	
	/**
	 * Executing an action, i.e. an operation over an artifact 
	 * 
	 * @param op target operation
	 * @param test alignment test
	 * @param timeout timeout
	 * @return unique action identifier 
	 * @throws CartagoException
	 */
	long doAction(Op op, IAlignmentTest test, long timeout) throws CartagoException;
	
	/**
	 * Executing an action, i.e. an operation over an artifact
	 * 
	 * In this case, the target artifact is not specified.
	 * 
	 * @param wspId workspace identifier
	 * @param op target operation
	 * @param test alignment test
	 * @param timeout timeout
	 * @return unique action identifier 
	 * @throws CartagoException
	 */	
	long doAction(WorkspaceId wspId, Op op, IAlignmentTest test, long timeout) throws CartagoException;


	/**
	 * Executing an action, i.e. an operation over an artifact
	 * 
	 * In this case, the target artifact is not specified.
	 * 
	 * @param wspId workspace identifier
	 * @param op target operation
	 * @param test alignment test
	 * @param timeout timeout
	 * @return unique action identifier 
	 * @throws CartagoException
	 */	
	long doAction(String wspName, Op op, IAlignmentTest test, long timeout) throws CartagoException;
	
	
	/**
	 * Executing an action, i.e. an operation over an artifact
	 * 
	 * In this case, the name of the artifact and the workspace are specified.
	 * 
	 * @param artName target artifact
	 * @param wspId workspace identifier
	 * @param op target operation
	 * @param test alignment test
	 * @param timeout timeout
	 * @return unique action identifier 
	 * @throws CartagoException
	 */
	long doAction(WorkspaceId wspId, String artName, Op op, IAlignmentTest test, long timeout) throws CartagoException;


	/**
	 * Executing an action, i.e. an operation over an artifact
	 * 
	 * In this case, the name of the artifact and the workspace are specified.
	 * 
	 * @param artName target artifact
	 * @param wspId workspace identifier
	 * @param op target operation
	 * @param test alignment test
	 * @param timeout timeout
	 * @return unique action identifier 
	 * @throws CartagoException
	 */
	long doAction(String wspName, Op op, String artName, IAlignmentTest test, long timeout) throws CartagoException;

	
	/**
	 * Executing an action, i.e. an operation over an artifact 
	 * 
	 * @param artName target artifact
	 * @param op target operation
	 * @param test alignment test
	 * @param timeout timeout
	 * @return unique action identifier 
	 * @throws CartagoException
	 */
	long doAction(Op op, String artName, IAlignmentTest test, long timeout) throws CartagoException;

	
	/**
	 * Get the current workspaces joined by the agent.
	 * 
	 * @return workspace identifier.
	 * 
	 * @throws CartagoException
	 */
	List<WorkspaceId> getJoinedWorkspaces() throws CartagoException;
	
	/**
	 * Fetch the next percept from the percept queue.
	 * 
	 * @return null if no percepts are available.
	 */
	CartagoEvent fetchNextPercept();
	
}
