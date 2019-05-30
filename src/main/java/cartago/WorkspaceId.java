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
import java.util.UUID;

/**
 * Identifier of a workspace
 *  
 * @author aricci
 */
public class WorkspaceId implements Serializable {

	/* this is the local name, unique in the scope of the parent */
	private String name;
	
	/* this is the full name - e.g.  /main/w0/w1 */
	private String fullName;
	
	/* this is the unique identifier of the wsp */
	private UUID uuid;
	
	WorkspaceId(){}
	
	/**
	 * Workspace identifier for Local environment.
	 * 
	 * @param fullName full name of the workspace: e.g. /main/w0
	 * @param wspId unique UUID identifying the workspace
	 * 
	 */
	public WorkspaceId(String fullName, UUID wspId) throws InvalidWorkspaceNameException {
		this.fullName = fullName;
		this.uuid = wspId;
		int index = fullName.lastIndexOf('/');
		if (index == -1) {
			name = fullName;
			fullName = "/"+fullName;
		} else {
			name = fullName.substring(index + 1);
		}
	}
	
	public WorkspaceId(String fullName) throws InvalidWorkspaceNameException {
		this(fullName, UUID.randomUUID());
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
	 * Get the full name, including path from root
	 * 
	 * @return
	 */
	public String getFullName(){
		return fullName;
	}
	
	/**
	 * Get the wsp UUID
	 * @return
	 */
	public UUID getUUID() {
		return this.uuid;
	}
	
	public int hashCode(){
		return uuid.hashCode();
	}
	
	public boolean equals(Object obj){
		return (obj instanceof WorkspaceId) && ((WorkspaceId)obj).uuid.equals(uuid); 
	}
	
	public String toString(){
		return name;
	}
}
