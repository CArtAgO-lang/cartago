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
import cartago.events.*;

/**
 * Artifact descriptor, keeping track of artifact
 * management information
 * 
 * @author aricci
 *
 */
public class ArtifactDescriptor {

	private Artifact artifact;
	private AgentId creator;
	private AbstractArtifactAdapter adapter;

	private boolean observed;
	private ArrayList<ArtifactObserver> observers;
	
	public ArtifactDescriptor(Artifact artifact, AgentId creator, AbstractArtifactAdapter adapter){
		this.artifact = artifact;
		this.adapter = adapter;
		this.creator = creator;
		observers = new ArrayList<ArtifactObserver>();
		observed = false;
	}
	
	public Artifact getArtifact(){
		return artifact;
	}
		
	public AgentId getAgentCreator(){
		return creator;
	}
		
	public String getArtifactType(){
		return artifact.getClass().getCanonicalName();
	}
	
	public AbstractArtifactAdapter getAdapter(){
		return adapter;
	}
	
	public synchronized  void removeAllObservers(){
		observers.clear();
	}

	public synchronized  void addObserver(AgentId id, IEventFilter ev, ICartagoCallback ctx){
		Iterator<ArtifactObserver> it = observers.iterator();
		boolean found = false;
		while (it.hasNext()){
			ArtifactObserver obs = it.next();
			if (obs.getAgentId().equals(id)){
				found = true;
				break;
			}
		}
		if (!found){
			ArtifactObserver obs = new ArtifactObserver(id, ev, ctx);
			observers.add(obs);
			observed = true;
		}
	}
	
	public synchronized boolean removeObserver(AgentId ctxId){
			Iterator<ArtifactObserver> it = observers.iterator();
			boolean found = false;
			while (it.hasNext()){
				ArtifactObserver obs = it.next();
				if (obs.getAgentId().equals(ctxId)){
					it.remove();
					found = true;
					break;
				}
			}
			if (found){
				if (observers.size()==0){
					observed = false;
					/*
					if (artifact instanceof AgentBodyArtifact){
						((AgentBodyArtifact)artifact).getAgentBody().setObserved(false);
					}*/
				}
			}
			return found;
	}

	public synchronized boolean isObserved(){
			return observed;
	}

	public synchronized boolean isObservedBy(AgentId userId){
		Iterator<ArtifactObserver> it = observers.iterator();
		if (observed){
			while (it.hasNext()){
				ArtifactObserver obs = it.next();
				if (obs.getAgentId().equals(userId)){
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Return the list of agents that are observing this artifact
	 * @return list o agents that are observing this artifact
	 */
	public synchronized List<ArtifactObserver> getObservers() {
		return observers;
	}

	public synchronized void notifyObservers(CartagoEvent ev){
		if (observed){
			for (ArtifactObserver obs:observers){
				try {
					IEventFilter filter = obs.getFilter();
					if (filter == null){
						obs.getListener().notifyCartagoEvent(ev);
					} else {
						try {
							if (ev instanceof ArtifactObsEvent){
								boolean res = filter.select((ArtifactObsEvent)ev);
								if (res){
									obs.getListener().notifyCartagoEvent(ev);
								}
							}
						} catch (Exception ex){
							// error in filter eval.
							ex.printStackTrace();
						}
					}
				} catch (Exception ex){
					ex.printStackTrace();
				}
			}
		}
	}
		
	public synchronized void notifyObserver(AgentId id, CartagoEvent ev){
		if (observed){
			for (ArtifactObserver obs:observers){
				try {
					if (obs.getAgentId().equals(id)){
						IEventFilter filter = obs.getFilter();
						if (filter == null){
							obs.getListener().notifyCartagoEvent(ev);
						} else {
							try {
								if (ev instanceof ArtifactObsEvent){
									boolean res = filter.select((ArtifactObsEvent)ev);
									if (res){
										obs.getListener().notifyCartagoEvent(ev);
									}
								}
							} catch (Exception ex){
								// error in filter eval.
								ex.printStackTrace();
							}
						}
						break;
					}
				} catch (Exception ex){
					ex.printStackTrace();
				}
			}
		}
	}


}
