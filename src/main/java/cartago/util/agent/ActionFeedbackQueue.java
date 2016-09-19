package cartago.util.agent;

import java.util.Iterator;
import java.util.LinkedList;

import cartago.events.CartagoActionEvent;

/**
 * Used in CArtAgo Java API
 * 
 * @author aricci
 *
 */
public class ActionFeedbackQueue {

	LinkedList<CartagoActionEvent> list;
	
	public ActionFeedbackQueue(){
		list = new LinkedList<CartagoActionEvent>();
	}

	public synchronized void add(CartagoActionEvent ev){
		list.add(ev);
		notifyAll();
	}

	public synchronized CartagoActionEvent waitFor(long id) throws InterruptedException {
		CartagoActionEvent ev = remove(id);
		while (ev == null){
			wait();
			ev = remove(id);
		}
		return ev;
	}
	
	private CartagoActionEvent remove(long id){
		Iterator<CartagoActionEvent> it = list.iterator();
		while (it.hasNext()){
			CartagoActionEvent ev = it.next();
			if (ev.getActionId() == id){
				it.remove();
				return ev;
			}
		}
		return null;
	}
}
