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

import java.util.*;
import java.util.concurrent.locks.*;

import cartago.ArtifactId;
import cartago.Op;
import cartago.AgentId;


/**
 * Generic security manager.
 * 
 * @author aricci
 *
 */
public class DefaultSecurityManager implements IWorkspaceSecurityManager {

	public static String DEFAULT_ROLE = "user";
	private HashMap<String, Role> roles;
	private ReentrantReadWriteLock rwl;
	
	public DefaultSecurityManager() {
		roles = new HashMap<String, Role>();
		rwl = new ReentrantReadWriteLock();
		Role def = new Role(DEFAULT_ROLE);
		roles.put(DEFAULT_ROLE, def);
	}

	// role management 
	
	public void addRole(String roleName) throws SecurityException {
		try {
			rwl.writeLock().lock();
			Role role = roles.get(roleName);
			if (role==null){
				role = new Role(roleName);
				roles.put(roleName, role);
			} else {
				throw new SecurityException("Role "+roleName+" already exists.");
			}
		} finally {
			rwl.writeLock().unlock();
		}	
	}
		
	public void removeRole(String roleName){
		rwl.writeLock().lock();
		roles.remove(roleName);		
		rwl.writeLock().unlock();
	}

	public boolean hasRole(String roleName) {
		try {
			rwl.readLock().lock();
			return roles.get(roleName) != null;
		} finally {
			rwl.readLock().unlock();
		}
	}

	public void setDefaultRoleName(String roleName) throws SecurityException {
		rwl.readLock().lock();
		Role role = roles.get(roleName);
		rwl.readLock().unlock();
		if (role!=null){
			DEFAULT_ROLE = roleName;
		} else {
			throw new SecurityException("Role "+roleName+" does not exist.");
		}
	}

	public String getDefaultRoleName(){
		return DEFAULT_ROLE;
	}

	// policy management
	
	public void addRolePolicy(String roleName, String artifactName, IArtifactUsePolicy policy) throws SecurityException {
		rwl.readLock().lock();
		Role role = roles.get(roleName);
		rwl.readLock().unlock();
		if (role!=null){
			role.addPolicyForUse(artifactName, policy);
		} else {
			throw new SecurityException("Unknown role: "+roleName);
		}
	}

	public void removeRolePolicy(String roleName, String artifactName) throws SecurityException {
		rwl.readLock().lock();
		Role role = roles.get(roleName);
		rwl.readLock().unlock();
		if (role!=null){
			role.removePolicyForUse(artifactName);
		} else {
			throw new SecurityException("Unknown role: "+roleName);
		}
	}


	public void setDefaultRolePolicy(String roleName, IArtifactUsePolicy policy) throws SecurityException  {
		Role role = roles.get(roleName);
		if (role!=null){
			role.setUseDefaultPolicy(policy);
		} else {
			throw new SecurityException("Unknown role: "+roleName);
		}
	}
	// check policy
	
	public boolean canDoAction(AgentId aid, ArtifactId id, Op opDetail){
		rwl.readLock().lock();
		Role role = roles.get(aid.getAgentRole());
		rwl.readLock().unlock();
		if (role == null){
			return false;
		} else {
			return role.canUse(aid,id,opDetail);
		}
	}
	public String[] getRoleList() {
		String[] roleList = new String[roles.size()];
		int i = 0;
		for (Role r: roles.values()){
			roleList[i++] = r.getName();
		}
		return roleList;
	}

	
}
