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
import java.util.concurrent.locks.ReentrantReadWriteLock;

import cartago.ArtifactId;
import cartago.Op;
import cartago.AgentId;


/**
 * Class representing a role and related policies.
 * 
 * @author aricci
 *
 */
public class Role {

	private ReentrantReadWriteLock rwl;
	private String name;
	
	private IArtifactUsePolicy useDefaultPolicy;
	
	private HashMap<String,IArtifactUsePolicy> usePolicyMap;

	public Role(String name){
		this.name = name;
		rwl = new ReentrantReadWriteLock();
		
		usePolicyMap = new HashMap<String,IArtifactUsePolicy>();
		useDefaultPolicy = IWorkspaceSecurityManager.ALWAYS_ALLOW_USE;
	}
	
	public String getName(){
		return name;
	}
	
	// manage policies
	
	public void addPolicyForUse(String artifactName, IArtifactUsePolicy policy){
		usePolicyMap.put(artifactName, policy);
	}

	public void removePolicyForUse(String artifactName){
		usePolicyMap.remove(artifactName);
	}

	// check policy
	
	public void setUseDefaultPolicy(IArtifactUsePolicy policy){
		useDefaultPolicy = policy;
	}

	public boolean canUse(AgentId aid, ArtifactId id, Op opDetail){
		try {
			rwl.readLock().lock();
			IArtifactUsePolicy p = usePolicyMap.get(id.getName());
			if (p!=null){
				return p.allow(aid, id, opDetail);
			} else {
				return useDefaultPolicy.allow(aid, id, opDetail);
			}
		} finally {
			rwl.readLock().unlock();
		}
	}	
}
