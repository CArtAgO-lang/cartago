package cartago.util.agent;

import cartago.*;
import cartago.events.*;
import java.util.*;

public class ObsEventQueue {

	LinkedList<ArtifactObsEvent> list;
	
	public ObsEventQueue(){
		list = new LinkedList<ArtifactObsEvent>();
	}

	public synchronized void add(ArtifactObsEvent ev){
		list.add(ev);
		notifyAll();
	}

	public synchronized ArtifactObsEvent waitFor(IEventFilter filter) throws InterruptedException {
		ArtifactObsEvent ev = remove(filter);
		while (ev == null){
			wait();
			ev = remove(filter);
		}
		return ev;
	}
	
	public synchronized ArtifactObsEvent fetch(IEventFilter filter) throws InterruptedException {
		return remove(filter);
	}

	private ArtifactObsEvent remove(IEventFilter filter){
		Iterator<ArtifactObsEvent> it = list.iterator();
		while (it.hasNext()){
			ArtifactObsEvent ev = it.next();
			if (filter.select(ev)){
				it.remove();
				return ev;
			}
		}
		return null;
	}


}
