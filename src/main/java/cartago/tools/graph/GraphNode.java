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
package cartago.tools.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a node in a graph. The nodes represents mainly the artifacts
 * which has a name and type and is placed in a workspace. The nodes may also being
 * observed by agents or linked to other artifacts what should be represented by
 * connections among nodes. The nodes may also have lists of operations and properties. 
 * 
 * @author cleberjamaral
 */
public class GraphNode {
	private String name;
	private String workspace;
	private String type;
	private List<String> observingAgents = new ArrayList<String>();
	private List<String> linkedArtifacts = new ArrayList<String>();
	private List<String> operations = new ArrayList<String>();
	private List<String> observableProperties = new ArrayList<String>();

	/**
	 * get name of related artifact
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set name of related artifact
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * get workspace of related artifact
	 * @return
	 */
	public String getWorkspace() {
		return workspace;
	}

	/**
	 * Set workspace of the related artifact
	 * @param workspace
	 */
	public void setWorkspace(String workspace) {
		this.workspace = workspace;
	}

	/**
	 * Get type of related artifact
	 * @return
	 */
	public String getType() {
		return type;
	}

	/**
	 * Set the type of the related artifact
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * List agents that are observing the related artifact
	 * @return
	 */
	public List<String> getObservingAgents() {
		return observingAgents;
	}

	/**
	 * Add (if does not exist) an agent that is observing the 
	 * related artifact
	 * @param observingAgent
	 */
	public void addNewObservingAgent(String observingAgent) {
		if (!this.observingAgents.contains(observingAgent)) 
			this.observingAgents.add(observingAgent);
	}
	
	/**
	 * Add (if does not exist) a linked artifact that this 
	 * related artifact is linked with
	 * @param linkedArtifact
	 */
	public void addNewLinkedArtifact(String linkedArtifact) {
		if (!this.linkedArtifacts.contains(linkedArtifact)) 
			this.linkedArtifacts.add(linkedArtifact);
	}

	/**
	 * List the artifacts that this one is linked with
	 * @return linkedArtifacts
	 */
	public List<String> getLinkedArtifacts() {
		return linkedArtifacts;
	}

	/**
	 * List the operations allowed by this node
	 * @return operations
	 */
	public List<String> getOperations() {
		return operations;
	}

	/**
	 * Add (if does not exist) an operation
	 * @param operation
	 */
	public void addNewOperation(String operation) {
		if (!this.operations.contains(operation)) 
			this.operations.add(operation);
	}

	/**
	 * List the observable properties of this node
	 * @return observableProperties
	 */
	public List<String> getObservableProperties() {
		return observableProperties;
	}

	/**
	 * Add (if does not exist) an observable property
	 * @param observableProperty
	 */
	public void addNewObservableProperty(String observableProperty) {
		if (!this.observableProperties.contains(observableProperty)) 
			this.observableProperties.add(observableProperty);
	}
}
