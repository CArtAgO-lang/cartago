package cartago;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
	private LinkedList<WorkspaceId> contextOrderedList;
	
	// queue where percepts are notified by the environment
	private java.util.concurrent.ConcurrentLinkedQueue<CartagoEvent> perceptQueue;

	private ICartagoListener agentArchListener;
	private AtomicLong actionId;

	private static AgentCredential credential;
	private static String agentRole;
	private ArtifactId agentContextId;	
		
	CartagoSession(AgentCredential credential, String agentRole, ICartagoListener listener) throws CartagoException {
		contexts = new ConcurrentHashMap<WorkspaceId, ICartagoContext>();
		contextOrderedList = new java.util.LinkedList<WorkspaceId>();
		perceptQueue = new java.util.concurrent.ConcurrentLinkedQueue<CartagoEvent>();
		agentArchListener = listener;
		this.agentRole = agentRole;
		this.credential = credential;
		actionId = new AtomicLong(0);
	}

	void init(ArtifactId agentContextId, WorkspaceId initialWspId, ICartagoContext startContext) {
		this.agentContextId = agentContextId;
		contexts.put(initialWspId, startContext);
		synchronized (contextOrderedList) {
			contextOrderedList.addFirst(initialWspId);
		}
	}

	public ArtifactId getAgentContextArtifactId() {
		return agentContextId;
	}

	public String getEnvName() {
		return CartagoEnvironment.getInstance().getName();
	}
	
	public long doAction(ArtifactId aid, Op op, IAlignmentTest test, long timeout) throws CartagoException  {
		long actId = actionId.incrementAndGet();
		ICartagoContext ctx = null;
		synchronized (this){
			ctx = contexts.get(aid.getWorkspaceId());
			if (ctx != null) {
				ctx.doAction(actId, aid.getName(), op, test, timeout);
				return actId;
			} else {
				throw new CartagoException("Wrong workspace.");
			}
		}
	}

	public long doAction(WorkspaceId wspId, String artName, Op op, IAlignmentTest test, long timeout)
			throws CartagoException {
		long actId = actionId.incrementAndGet();
		ICartagoContext ctx = null;
		synchronized (this){
			ctx = contexts.get(wspId);
			if (ctx != null) {
				ctx.doAction(actId, artName, op, test, timeout);
				return actId;
			} else {
				throw new CartagoException("Wrong workspace.");
			}
		}
	}
	
	public long doAction(String wspName, String artName, Op op, IAlignmentTest test, long timeout)
			throws CartagoException {
		long actId = actionId.incrementAndGet();
		ICartagoContext ctx = null;
		synchronized (this){
			for (Map.Entry<WorkspaceId, ICartagoContext> e: contexts.entrySet()){
				if (e.getKey().getName().equals(wspName)) {
					ctx = e.getValue();
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
	}

	public long doAction(Op op, WorkspaceId wspId, IAlignmentTest test, long timeout) throws CartagoException {
		long actId = actionId.incrementAndGet();
		ICartagoContext ctx = null;
		synchronized (this){
			ctx = contexts.get(wspId);
			if (ctx != null) {
				ctx.doAction(actId, op, test, timeout);
				return actId;
			} else {
				throw new CartagoException("Workspace not found.");
			}
		}
	}

	public long doAction(Op op, String wspName, IAlignmentTest test, long timeout) throws CartagoException {
		long actId = actionId.incrementAndGet();
		ICartagoContext ctx = null;
		synchronized (this){
			for (Map.Entry<WorkspaceId, ICartagoContext> e: contexts.entrySet()){
				if (e.getKey().getName().equals(wspName)) {
					ctx = e.getValue();
					break;
				}
			}
			if (ctx != null) {
				ctx.doAction(actId, op, test, timeout);
				return actId;
			} else {
				throw new CartagoException("Workspace not found.");
			}
		}
	}

	public List<WorkspaceId> getJoinedWorkspaces() throws CartagoException {
		List<WorkspaceId> wsps = new LinkedList<WorkspaceId>();
		for (java.util.Map.Entry<WorkspaceId, ICartagoContext> c : contexts.entrySet()) {
			wsps.add(c.getKey());
		}
		return wsps;
	}

	// Utility methods

	public WorkspaceId getJoinedWspId(String wspName) throws CartagoException {
		for (java.util.Map.Entry<WorkspaceId, ICartagoContext> c : contexts.entrySet()) {
			if (c.getKey().getName().equals(wspName)) {
				return c.getKey();
			}
		}
		throw new CartagoException("Workspace not joined.");
	}
	
	public WorkspaceId getCurrentWorkspace() {
		synchronized (contextOrderedList) {
			return this.contextOrderedList.getFirst();
		}
	}
	
	/**
	 * Make a new artifact instance
	 * 
	 * @param artifactName logic name
	 * @param templateName type
	 * @return
	 * @throws CartagoException
	 */
	public ArtifactId makeArtifact(WorkspaceId wid, String artifactName, String templateName, Object[] params) throws CartagoException {
		OpFeedbackParam<ArtifactId> res = new OpFeedbackParam<ArtifactId>();
		try{
			doAction(new Op("makeArtifact", artifactName, templateName, params, res), wid, null, -1);
		} catch (Exception ex){
			throw new CartagoException();
		}
		return res.get();
	}

	
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
			WorkspaceId wid = wspev.getWorkspaceId();		
			contexts.put(wspev.getWorkspaceId(), wspev.getContext());
			synchronized (contextOrderedList) {
				contextOrderedList.addFirst(wspev.getWorkspaceId());
			}
		} else if (ev instanceof QuitWSPSucceededEvent) {
			QuitWSPSucceededEvent wspev = (QuitWSPSucceededEvent) ev;
			contexts.remove(wspev.getWorkspaceId());
			synchronized (contextOrderedList) {
				contextOrderedList.remove(wspev.getWorkspaceId());
			}
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
