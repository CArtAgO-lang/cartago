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

import java.util.*;
/**
 * Interface for artifact adapters
 * 
 * @author aricci
 *
 */
public interface IArtifactAdapter {

	/**
	 * Init the artifact.
	 * 
	 * @param cfg initial configuration.
	 * @throws CartagoException
	 */
	void initArtifact(ArtifactConfig cfg) throws CartagoException;
	
	/**
	 * Request the execution of an operation
	 * 
	 * @param info information about the operation
	 * @throws CartagoException
	 */
	void doOperation(OpExecutionFrame info) throws CartagoException;

	/**
	 * Read a property
	 * 
	 * @param propertyName
	 * @return
	 * @throws CartagoException
	 */
	ArtifactObsProperty readProperty(String propertyName) throws CartagoException;			

	/**
	 * Read all properties
	 * 
	 * @return
	 * @throws CartagoException
	 */
	List<ArtifactObsProperty> readProperties();			
	
	/**
	 * Get the artifact manual
	 * 
	 * @return
	 */
	Manual getManual();
	
	/**
	 * Link to an artifact, specifying the out port
	 * 
	 * @param aid
	 * @param portName
	 */
	void linkTo(ArtifactId aid, String portName) throws CartagoException ;
	
	/**
	 * Get the operations
	 * 
	 * @return
	 * @throws CartagoException
	 */
	List<OpDescriptor> getOperations() throws CartagoException;
 
	/**
	 * Get current operation in execution
	 * 
	 * @return
	 * @throws CartagoException
	 */
	List<OperationInfo> getOpInExecution() throws CartagoException;
	
	/**
	 * Check if the artifact has the specified operation
	 * @param op
	 * @return
	 */
	boolean hasOperation(Op op);
	

	/**
	 * returns current artifact position
	 * @return
	 */
	AbstractWorkspacePoint getPosition();
	
	/**
	 * return current artifact obs radius
	 * @return
	 */
	double getObservabilityRadius();

}
