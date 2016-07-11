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

import java.util.*;

/**
 * Agent descriptor - keeping track of agent info inside a workspace
 * 
 * @author aricci
 *
 */
public class AgentBody implements ICartagoContext {
	
	private AgentId id;
	private WorkspaceKernel wspKernel;
	
	/* agent callback to notify action/obs events */
	protected ICartagoCallback agentCallback;
	
	protected LinkedList<ArtifactDescriptor> focusedArtifacts;
	protected AgentBodyArtifact bodyArtifact;
	
	AgentBody(AgentId id, WorkspaceKernel env, ICartagoCallback agentCallback){
		this.id = id;
		this.wspKernel = env;
		this.agentCallback = agentCallback;
		focusedArtifacts = new LinkedList<ArtifactDescriptor>();
		bodyArtifact = null;
	}
	
	public AgentId getAgentId() {
		return id;
	}
	
	public WorkspaceId getWorkspaceId(){
		return wspKernel.getId();
	}
	
	public WorkspaceKernel getWSPKernel(){
		return wspKernel;
	}

	public void setBodyArtifact(AgentBodyArtifact art){
		bodyArtifact = art;
	}
	
	public AgentBodyArtifact getAgentBodyArtifact(){
		return bodyArtifact;
	}
	//
	
	public void doAction(long actionId, ArtifactId aid, Op op, IAlignmentTest test, long timeout) throws CartagoException {
		if (timeout == -1){
			timeout = Integer.MAX_VALUE;
		}
		wspKernel.execOp(actionId, id, agentCallback, aid, op, timeout, test);
	}

	public boolean doAction(long actionId, String name, Op op, IAlignmentTest test, long timeout) throws CartagoException {
		if (timeout == -1){
			timeout = Integer.MAX_VALUE;
		}
		return wspKernel.execOp(actionId, id, agentCallback, name, op, timeout, test);
	}
	
	public boolean doAction(long actionId, Op op, IAlignmentTest test, long timeout) throws CartagoException {
		if (timeout == -1){
			timeout = Integer.MAX_VALUE;
		}
		return wspKernel.execOp(actionId, id, agentCallback, op, timeout, test);
	}
	
	// called by the kernel
	
	public void addFocusedArtifacts(ArtifactDescriptor des){
		focusedArtifacts.add(des);
		if (bodyArtifact != null){
			bodyArtifact.beginExternalSession();
			bodyArtifact.addFocusedArtifact(des.getArtifact().getId().getWorkspaceId().getName(),
											des.getArtifact().getId().getName(),
											des.getArtifact().getId());
			bodyArtifact.endExternalSession(true);
		}
	}

	public void removeFocusedArtifacts(ArtifactDescriptor des){
		focusedArtifacts.remove(des);
	}
	
	public ICartagoCallback getCallback(){
		return agentCallback;
	}
	
	/* experimental */
	
	public boolean isObserving(ArtifactId id){
		for (ArtifactDescriptor des: focusedArtifacts){
			if (des.getArtifact().getId().equals(id)){
				return true;
			}
		}
		return false;
	}
	
	public void updateObsArtifactListWith(List<ObservableArtifactInfo> list, List<ObservableArtifactInfo> stopFocus, List<ObservableArtifactInfo> newFocus){
		Iterator<ArtifactDescriptor> itd = focusedArtifacts.iterator();
		while (itd.hasNext()) {
			ArtifactDescriptor des = itd.next();
			boolean found = false;
			Iterator<ObservableArtifactInfo> it = list.iterator();
			while (it.hasNext()){
				ObservableArtifactInfo el = it.next();
				if (el.getTargetArtifact().getId()==des.getArtifact().getId().getId()){
					found = true;
					it.remove();
					break;
				} 
			}
			if (!found){
				stopFocus.add(new ObservableArtifactInfo(des.getArtifact().getId(),des.getAdapter().readProperties()));
				itd.remove();
			}  
		}
		for (ObservableArtifactInfo el:list){
			newFocus.add(el);
			focusedArtifacts.add(el.getDescriptor());
		}
	}	


}
