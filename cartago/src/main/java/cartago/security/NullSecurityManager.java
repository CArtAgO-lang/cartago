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
package cartago.security;

import cartago.ArtifactId;
import cartago.Op;
import cartago.AgentId;

/**
 * Security manager with no restrictions.
 * 
 * @author aricci
 *
 */
public class NullSecurityManager implements IWorkspaceSecurityManager {

	public boolean canDoAction(AgentId aid, ArtifactId id, Op opDetail) {
		// TODO Auto-generated method stub
		return true;
	}

	public String getDefaultRoleName() {
		// TODO Auto-generated method stub
		return "";
	}

	public void addRole(String roleName) throws SecurityException {
		// TODO Auto-generated method stub
		
	}


	public void addRolePolicy(String roleName, String artifactName, IArtifactUsePolicy policy) throws SecurityException {
		// TODO Auto-generated method stub
		
	}

	public boolean hasRole(String roleName) {
		// TODO Auto-generated method stub
		return false;
	}

	public void removeRole(String roleName) {
		// TODO Auto-generated method stub
		
	}

	public void removeRolePolicy(String roleName, String artifactName) throws SecurityException {
		// TODO Auto-generated method stub
		
	}

	public void setDefaultRoleName(String roleName) throws SecurityException {
		// TODO Auto-generated method stub
		
	}

	public String[] getRoleList() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setDefaultRolePolicy(String roleName, IArtifactUsePolicy policy) throws SecurityException{
	
	}

}
