package cartago;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.*;
import cartago.events.*;

/**
 * Class to manage a working session of an agent inside a workspace
 * 
 * @author aricci
 *
 */
public class CartagoSession implements ICartagoSession, ICartagoCallback {

	// one context for workspace, the agent can work in multiple workspaces
	private ConcurrentHashMap<WorkspaceId, ICartagoContext> contexts;

	// queue where percepts are notified by the environment
	private java.util.concurrent.ConcurrentLinkedQueue<CartagoEvent> perceptQueue;

	private ICartagoListener agentArchListener;
	private AtomicLong actionId;

	CartagoSession(ICartagoListener listener) throws CartagoException {
		contexts = new ConcurrentHashMap<WorkspaceId, ICartagoContext>();
		perceptQueue = new java.util.concurrent.ConcurrentLinkedQueue<CartagoEvent>();
		agentArchListener = listener;
		actionId = new AtomicLong(0);
	}

	void setInitialContext(WorkspaceId wspId, ICartagoContext startContext) {
		contexts.put(wspId, startContext);
		// currentContext = startContext;
		// currentWspId = wspId;
	}

	public long doAction(ArtifactId aid, Op op, IAlignmentTest test, long timeout) throws CartagoException {
		long actId = actionId.incrementAndGet();
		ICartagoContext ctx = null;
		ctx = contexts.get(aid.getWorkspaceId());
		if (ctx != null) {
			ctx.doAction(actId, aid, op, test, timeout);
			return actId;
		} else {
			throw new CartagoException("Wrong workspace.");
		}
	}

	public long doAction(Op op, IAlignmentTest test, long timeout) throws CartagoException {
		long actId = actionId.incrementAndGet();
		boolean found = false;
		for (java.util.Map.Entry<WorkspaceId, ICartagoContext> c : contexts.entrySet()) {
			ICartagoContext ctx = c.getValue();
			found = ctx.doAction(actId, op, test, timeout);
			if (found) {
				break;
			}
		}
		if (found) {
			return actId;
		} else {
			throw new CartagoException("Artifact not found.");
		}
	}

	public long doAction(WorkspaceId wspId, Op op, IAlignmentTest test, long timeout) throws CartagoException {
		long actId = actionId.incrementAndGet();
		ICartagoContext ctx = null;
		ctx = contexts.get(wspId);
		if (ctx != null) {
			ctx.doAction(actId, op, test, timeout);
			return actId;
		} else {
			throw new CartagoException("Wrong workspace.");
		}
	}

	public long doAction(String wspName, Op op, IAlignmentTest test, long timeout) throws CartagoException {
		long actId = actionId.incrementAndGet();
		ICartagoContext ctx = null;
		for (java.util.Map.Entry<WorkspaceId, ICartagoContext> c : contexts.entrySet()) {
			if (c.getKey().getName().equals(wspName)) {
				ctx = c.getValue();
				break;
			}
		}
		if (ctx != null) {
			ctx.doAction(actId, op, test, timeout);
			return actId;
		} else {
			throw new CartagoException("Wrong workspace.");
		}
	}

	public long doAction(String wspName, Op op, String artName, IAlignmentTest test, long timeout)
			throws CartagoException {
		long actId = actionId.incrementAndGet();
		ICartagoContext ctx = null;
		for (java.util.Map.Entry<WorkspaceId, ICartagoContext> c : contexts.entrySet()) {
			if (c.getKey().getName().equals(wspName)) {
				ctx = c.getValue();
				break;
			}
		}
		if (ctx != null) {
			ctx.doAction(actId, artName, op, test, timeout);
			return actId;
		} else {
			throw new CartagoException("Wrong workspace.");
		}
	}

	public long doAction(WorkspaceId wspId, String artName, Op op, IAlignmentTest test, long timeout)
			throws CartagoException {
		long actId = actionId.incrementAndGet();
		ICartagoContext ctx = null;
		ctx = contexts.get(wspId);
		if (ctx != null) {
			ctx.doAction(actId, artName, op, test, timeout);
			return actId;
		} else {
			throw new CartagoException("Wrong workspace.");
		}
	}

	public long doAction(Op op, String artName, IAlignmentTest test, long timeout) throws CartagoException {
		long actId = actionId.incrementAndGet();
		boolean found = false;
		for (java.util.Map.Entry<WorkspaceId, ICartagoContext> c : contexts.entrySet()) {
			ICartagoContext ctx = c.getValue();
			found = ctx.doAction(actId, artName, op, test, timeout);
			if (found) {
				break;
			}
		}
		if (found) {
			return actId;
		} else {
			throw new CartagoException("Wrong workspace.");
		}
	}

	// local

	public List<WorkspaceId> getJoinedWorkspaces() throws CartagoException {
		List<WorkspaceId> wsps = new LinkedList<WorkspaceId>();
		for (java.util.Map.Entry<WorkspaceId, ICartagoContext> c : contexts.entrySet()) {
			wsps.add(c.getKey());
		}
		return wsps;
	}

	/*
	 * public WorkspaceId getCurrentWorkspace(){ return currentWspId; }
	 * 
	 * public void setCurrentWorkspace(WorkspaceId wspId) throws
	 * CartagoException { synchronized (contexts){ ICartagoContext ctx =
	 * contexts.get(wspId); if (ctx != null){ currentContext = ctx; currentWspId
	 * = wspId; } else { for (java.util.Map.Entry<WorkspaceId,ICartagoContext>
	 * c: contexts.entrySet()){ System.out.println(c.getKey()+" "+ctx); } throw
	 * new CartagoException("Wrong workspace "+wspId); } } }
	 */
	//

	/**
	 * Fetch a new percept.
	 * 
	 * To be called in the sense stage of the agent execution cycle.
	 * 
	 */
	public CartagoEvent fetchNextPercept() {
		return perceptQueue.poll();
	}

	private void checkWSPEvents(CartagoEvent ev) {
		if (ev instanceof JoinWSPSucceededEvent) {
			JoinWSPSucceededEvent wspev = (JoinWSPSucceededEvent) ev;
			contexts.put(wspev.getWorkspaceId(), wspev.getContext());
		} else if (ev instanceof QuitWSPSucceededEvent) {
			QuitWSPSucceededEvent wspev = (QuitWSPSucceededEvent) ev;
			contexts.remove(wspev.getWorkspaceId());
		}
	}

	public void notifyCartagoEvent(CartagoEvent ev) {
		// System.out.println("NOTIFIED "+ev.getId()+"
		// "+ev.getClass().getCanonicalName());
		checkWSPEvents(ev);
		boolean keepEvent = true;
		if (agentArchListener != null) {
			keepEvent = agentArchListener.notifyCartagoEvent(ev);
		}
		if (keepEvent) {
			perceptQueue.add(ev);
		}
	}

}
