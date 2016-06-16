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

import java.io.Serializable;

/**
 * Identifier of a workspace
 *  
 * @author aricci
 */
public class WorkspaceId implements Serializable {

	private String name;
	private NodeId nodeId;
	private int hashCode;
	
	WorkspaceId(String name, NodeId id){
		this.name = name;
		this.nodeId = id;
		hashCode = id.hashCode();
	}
	
	/**
	 * Get the numeric identifier of the workspace
	 * 
	 * @return
	 */
	public String getId(){
		return name+"-"+nodeId.getId();
	}
	
	/**
	 * Get node id
	 * 
	 * @return
	 */
	public NodeId getNodeId(){
		return nodeId;
	}
	
	/**
	 * Get the logic name of the workspace
	 * 
	 * @return
	 */
	public String getName(){
		return name;
	}
		
	public int hashCode(){
		return hashCode;
	}
	
	public boolean equals(Object obj){
		return (obj instanceof WorkspaceId) && ((WorkspaceId)obj).getId().equals(this.getId()); 
	}
	
	public String toString(){
		return name;
	}
}
