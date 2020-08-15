package cartago;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.*;
import cartago.events.*;

/**
 * Class representing a working session of an agent inside an environment
 * 
 * - one for each agent  
 * - keeps track  of all workspaces joined
 * - accessed by the agent API
 * 
 * @author aricci
 *
 */
public class AgentSession implements IAgentSession, ICartagoCallback, Serializable {

	// one context for workspace, the agent can work in multiple workspaces
	private ConcurrentHashMap<WorkspaceId, ICartagoContext> contexts;
	private ICartagoContext homeWspCtx;
	private WorkspaceId		homeWspId;
	
	// queue where percepts are notified by the environment
	private java.util.concurrent.ConcurrentLinkedQueue<CartagoEvent> perceptQueue;

	private ICartagoListener agentArchListener;
	private AtomicLong actionId;
	private static long NO_OP_FOUND = -1;
	
	private static AgentCredential credential;
	private static String agentRole;
	private ArtifactId agentSessionArtifactId;	
	
	private static String wspNotJoined = "Workspace not joined";
		
	AgentSession(AgentCredential credential, String agentRole, ICartagoListener listener) throws CartagoException {
		contexts = new ConcurrentHashMap<WorkspaceId, ICartagoContext>();
		perceptQueue = new java.util.concurrent.ConcurrentLinkedQueue<CartagoEvent>();
		agentArchListener = listener;
		this.agentRole = agentRole;
		this.credential = credential;
		actionId = new AtomicLong(0);
	}

	void init(ArtifactId agentSessionArtifactId, WorkspaceId homeWspId, ICartagoContext startContext) {
		this.agentSessionArtifactId = agentSessionArtifactId;
		contexts.put(homeWspId, startContext);
		this.homeWspId = homeWspId;
		this.homeWspCtx = startContext;
	}

	public ArtifactId getAgentSessionArtifactId() {
		return agentSessionArtifactId;
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
				throw new CartagoException(wspNotJoined);
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
				throw new CartagoException(wspNotJoined);
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
				throw new CartagoException(wspNotJoined);
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
				throw new CartagoException(wspNotJoined);
			}
		}
	}

	public long doAction(Op op, String wspName, IAlignmentTest test, long timeout) throws CartagoException {
		long actId = actionId.incrementAndGet();
		ICartagoContext ctx = null;
		synchronized (this){
			// if it is a full name...
			if (wspName.startsWith("/")) {
				for (Map.Entry<WorkspaceId, ICartagoContext> e: contexts.entrySet()){
					if (e.getKey().getFullName().equals(wspName)) {
						ctx = e.getValue();
						break;
					}
				}
			} else {
				// single name
				
				for (Map.Entry<WorkspaceId, ICartagoContext> e: contexts.entrySet()){
					if (e.getKey().getName().equals(wspName)) {
						ctx = e.getValue();
						break;
					}
				}
			}
			if (ctx != null) {
				ctx.doAction(actId, op, test, timeout);
				return actId;
			} else {
				throw new CartagoException(wspNotJoined);
			}
		}
	}
	
	public long doActionWithImplicitCtx(Op op, WorkspaceId currentWspId, IAlignmentTest test, long timeout) throws CartagoException {
		long actId = actionId.incrementAndGet();
		ICartagoContext ctx = null;
		synchronized (this){
			boolean processed = false;
			/* first, try in current wsp */
			ctx = contexts.get(currentWspId);
			if (ctx != null) {
				processed = ctx.doTryAction(actId, op, test, timeout);
			}
			/* then try in home wsp */
			if (!processed) {
				processed = homeWspCtx.doTryAction(actId, op, test, timeout);
			}
			/* then in all other wsps */
			if (!processed) {
				for (Map.Entry<WorkspaceId, ICartagoContext> e: contexts.entrySet()){
					if (!e.getKey().equals(currentWspId) && !e.getKey().equals(homeWspId)) {
						ctx = e.getValue();
						processed = ctx.doTryAction(actId, op, test, timeout);
						if (processed) {
							break;
						}
					}
				}
			}
			if (!processed) {
				return NO_OP_FOUND;
			} else {
				return actId;
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

	public ICartagoContext getJoinedWsp(WorkspaceId wid) {
		return contexts.get(wid);
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
			contexts.put(wspev.getWorkspaceId(), wspev.getContext());
		} else if (ev instanceof QuitWSPSucceededEvent) {
			QuitWSPSucceededEvent wspev = (QuitWSPSucceededEvent) ev;
			contexts.remove(wspev.getWorkspaceId());
		}
	}

	public void notifyCartagoEvent(CartagoEvent ev) {
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
