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

	/* this is the local name, unique in the scope of the parent */
	private String name;	
	private WorkspaceId parentId; 

	
	WorkspaceId(){}
	
	/**
	 * Workspace identifier.
	 * 
	 * @param name local name, in the scope of the parent wsp
	 * @param parentId parent id
	 */
	public WorkspaceId(String name, WorkspaceId parentId){
		this.name = name;
		this.parentId = parentId;
	}
	
	/**
	 * 
	 * Workspace identifier for a root workspace.
	 * 
	 * @param name local name of root
	 */
	public WorkspaceId(String name){
		this.name = name;
	}

	/**
	 * Get the local name of the workspace
	 * 
	 * @return
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Get the workspace identifier of the parent workspace.
	 * 
	 * @return
	 */
	public WorkspaceId getParentId() {
		return parentId;
	}
	
	/**
	 * Get the full name, including path from root
	 * 
	 * @return
	 */
	public String getFullName(){
		if (parentId != null) {
			return parentId.getFullName() + "/" + name;
		} else {
			return "/" + name;
		}
	}
	

	public int hashCode(){
		return getFullName().hashCode();
	}
	
	public boolean equals(Object obj){
		return (obj instanceof WorkspaceId) && ((WorkspaceId)obj).getFullName().equals(this.getFullName()); 
	}
	
	public String toString(){
		return name;
	}
}
