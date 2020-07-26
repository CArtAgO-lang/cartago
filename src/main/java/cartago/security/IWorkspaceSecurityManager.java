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
package cartago.security;

import cartago.ArtifactId;
import cartago.Op;
import cartago.AgentId;

/**
 * Interface for a CArtAgO security manager component.
 * 
 * @author aricci
 *
 */
public interface IWorkspaceSecurityManager extends java.io.Serializable {

	public static final IArtifactUsePolicy ALWAYS_ALLOW_USE = new AlwaysAllowUsePolicy();
	public static final IArtifactUsePolicy ALWAYS_FORBID_USE = new AlwaysForbidUsePolicy();

	/**
	 * Set the default role name.
	 * 
	 * @return
	 */
	void setDefaultRoleName(String roleName) throws SecurityException;

	/**
	 * Get the default role name.
	 * 
	 * @return
	 */
	String getDefaultRoleName();
	

	/**
	 * Add a role.
	 * 
	 * @param roleName role name
	 * @throws SecurityException
	 */
	void addRole(String roleName) throws SecurityException;

	/**
	 * Remove a role, if it exists
	 * 
	 * @param roleName
	 */
	void removeRole(String roleName);

	/**
	 * Check if a role is present
	 * 
	 * @param roleName
	 * @return
	 */
	boolean hasRole(String roleName);

	/**
	 * Get current roles list.
	 * 
	 * @return
	 */
	String[] getRoleList();
	
	/**
	 * Set the default use policy
	 * 
	 * @param roleName
	 * @param artifactName
	 * @param policy
	 * @throws SecurityException
	 */
	void setDefaultRolePolicy(String roleName, 
			IArtifactUsePolicy policy) throws SecurityException;

	/**
	 * Add a new use policy
	 * 
	 * @param roleName
	 * @param artifactName
	 * @param policy
	 * @throws SecurityException
	 */
	void addRolePolicy(String roleName, String artifactName,
			IArtifactUsePolicy policy) throws SecurityException;

	/**
	 * Remove a use policy
	 * 
	 * @param roleName
	 * @param artifactName
	 * @throws SecurityException
	 */
	void removeRolePolicy(String roleName, String artifactName)
			throws SecurityException;

		
	//
	
	/**
	 * Check if a use action is allowed.
	 * 
	 * @param aid
	 * @param id
	 * @param opDetail
	 * @return
	 */
	boolean canDoAction(AgentId aid, ArtifactId id, Op opDetail);
	

}
