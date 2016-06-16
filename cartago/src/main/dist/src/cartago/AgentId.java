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

/**
 * Identifier of an agent inside a workspace.
 * 
 * @author aricci
 *
 */
public class AgentId implements java.io.Serializable {

	private int localId;
	private String name;
	private String role;
	private WorkspaceId wspId;
	private String globalId;
	private int hashCode;
	
	AgentId(String name, String globalId, int localId, String role, WorkspaceId wspId){
		this.name = name;
		this.localId = localId;
		this.role = role;
		this.globalId = globalId;
		this.wspId = wspId;
		this.hashCode = globalId.hashCode();
	}
	
	/**
	 * Get the name of the agent.
	 * 
	 * @return
	 */
	public String getAgentName(){
		return name;
	}
	
	public String toString(){
		return name;
	}

	/**
	 * Get the role;
	 * @return
	 */
	public String getAgentRole(){
		return role;
	}
	
	public WorkspaceId getWorkspaceId(){
		return this.wspId;
	}
	
	/**
	 * Get the numeric identifier of the agent.
	 * 
	 * @return
	 */
	public int getLocalId(){
		return localId;
	}

	/**
	 * Get the global identifier of the agent
	 * @return
	 */
	public String getGlobalId(){
		return globalId;
	}
	
	public boolean equals(Object aid){
		if (aid instanceof AgentId){
			AgentId ag = (AgentId) aid;
     		return globalId.equals(ag.globalId);
		} else {
			return false;
		}
	}
	
	public int hashCode(){
		return hashCode;
	}
}
