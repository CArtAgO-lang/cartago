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

/**
 * Base class for user credential.
 * 
 * @author aricci
 *
 */
public abstract class AgentCredential implements java.io.Serializable {
	
	private String userName;
	private String globalId;
	private String roleName;
	
	public AgentCredential(String userName, String roleName, String globalId){
		this.userName = userName;
		this.globalId = globalId;
		this.roleName = roleName;
	}
	
	/**
	 * Get the user identifier (name).
	 * 
	 * @return
	 */
	public String getId(){
		return userName;
	}
	
	/**
	 * Get the role name.
	 * 
	 * @return
	 */
	public String getRoleName(){
		return roleName;
	}
	
	/**
	 * Get the global identifier 
	 * 
	 * @return
	 */
	public String getGlobalId(){
		return globalId;
	}
}
