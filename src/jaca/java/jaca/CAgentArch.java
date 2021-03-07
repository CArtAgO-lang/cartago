package jaca;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import cartago.AgentBodyArtifact;
import cartago.AgentId;
import cartago.AgentSessionArtifact;
import cartago.ArtifactDescriptor;
import cartago.ArtifactId;
import cartago.ArtifactInfo;
import cartago.ArtifactObsProperty;
import cartago.CartagoEnvironment;
import cartago.CartagoEvent;
import cartago.CartagoException;
import cartago.IAgentSession;
import cartago.IAlignmentTest;
import cartago.ICartagoCallback;
import cartago.ICartagoController;
import cartago.Manual;
import cartago.ObservableArtifactInfo;
import cartago.Op;
import cartago.OpDescriptor;

import cartago.Tuple;
import cartago.Workspace;
import cartago.WorkspaceDescriptor;
import cartago.WorkspaceId;
import cartago.events.ActionFailedEvent;
import cartago.events.ActionSucceededEvent;
import cartago.events.ArtifactObsEvent;
import cartago.events.ConsultManualSucceededEvent;
import cartago.events.FocusSucceededEvent;
import cartago.events.FocussedArtifactDisposedEvent;
import cartago.events.JoinWSPSucceededEvent;
import cartago.events.ObsArtListChangedEvent;
import cartago.events.QuitWSPSucceededEvent;
import cartago.events.StopFocusSucceededEvent;
import jason.architecture.AgArch;
import jason.asSemantics.ActionExec;
import jason.asSemantics.Event;
import jason.asSemantics.Intention;
import jason.asSemantics.Message;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Atom;
import jason.asSyntax.ListTerm;
import jason.asSyntax.Literal;
import jason.asSyntax.LiteralImpl;
import jason.asSyntax.StringTerm;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;
import jason.asSyntax.Trigger;
import jason.asSyntax.Trigger.TEOperator;
import jason.asSyntax.Trigger.TEType;
import jason.bb.BeliefBase;

public class CAgentArch extends AgArch implements cartago.ICartagoListener, Serializable {

	private static final long serialVersionUID = 1L;
	
	static protected final Term OBS_PROP_PERCEPT = ASSyntax.createStructure("percept_type", ASSyntax.createAtom("obs_prop"));
	static protected final Term OBS_EV_PERCEPT   = ASSyntax.createStructure("percept_type", ASSyntax.createAtom("obs_ev"));

	private HashMap<ArtifactId, Set<Atom>> mappings = new HashMap<>();
	/*static private final List<String> DEF_OPS = Arrays.asList( 
		      "makeArtifact","removeArtifactFactory","addArtifactFactory","lookupArtifactByType","lookupArtifact","focusWhenAvailable",
		      "disposeArtifact","quitWorkspace","linkArtifacts","stopFocus","getCurrentArtifacts","focus","init","getRoleList","setSecurityManager",
		      "addRole","removeRole","addRolePolicy","removeRolePolicy","setDefaultRolePolicy","out","in","inp","rd","rdp","joinRemoteWorkspace",
		      "getNodeId","enableLinkingWithNode","shutdownNode","crash","joinWorkspace","createWorkspace","print","println");
    */
	
	protected transient IAgentSession envSession;

	// actions that have been executed and wait for a completion events
	protected Map<Long, PendingAction> pendingActions;


	/* last wsp joined across all intentions */
	protected WorkspaceId lastWspId;
	
	/* to keep track of current wsps for every intention */
	protected ConcurrentHashMap<Intention, LinkedList<WorkspaceId>> currentWspIntentionMap;
	protected ConcurrentHashMap<Long, Intention> pendingJoinWsp;

	// each agent has its own Java object map
	protected JavaLibrary lib;

	protected transient Logger logger;

	// private boolean firstManualFetched;

	// short cuts
	protected transient jason.bb.BeliefBase belBase;
	protected transient jason.asSemantics.Agent agent;
	private Set<ArtifactId> focusedArts = null;

	
	public CAgentArch() {
		super();
		
		pendingActions = new ConcurrentHashMap<>();
		currentWspIntentionMap = new ConcurrentHashMap<>();
		pendingJoinWsp = new ConcurrentHashMap<>();
		
		logger = Logger.getLogger("CAgentArch");
		lib = new JavaLibrary();
	}

	public IAgentSession getSession() {
		return envSession;
	}

	
	/**
	 * Creates the agent class defined by <i>agClass</i>, default is jason.asSemantics.Agent. The agent class will parse the source code, create the transition system (TS), ...
	 */
	@Override
	public void init() throws Exception {
		initBridge();
	}
	
	protected void initBridge() {
		String agentName = getTS().getAgArch().getAgName();
		try {			
			this.agent = getTS().getAg();
			this.belBase = agent.getBB();

			envSession = jaca.CartagoEnvironment.getInstance().startSession(agentName, this);
			lastWspId = envSession.getJoinedWorkspaces().get(0);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.warning("[CARTAGO] WARNING: No default workspace found for " + agentName);
		}
	}

	@Override
	public void stop() {
		if (envSession != null) {
			try {
				for (WorkspaceId wid: getEnvSession().getJoinedWorkspaces()) {
				    try {
				        envSession.doAction(new Op("quitWorkspace"), wid, null, -1);
				        //getTS().getLogger().info("quit "+wid.getName());
				    //} catch (CartagoException e) {
				    } catch (Exception e) {
				        if (! (e instanceof InterruptedException)) {
				            e.printStackTrace();
				        }
				    }

				}
			} catch (CartagoException e) {
				e.printStackTrace();
			}
		    // experimental
		     try {
		    	// remove session_ artifacts of the agent
		    	for (ArtifactId a: computeFocusedArts()) {
		    		if (a.getArtifactType().equals(AgentSessionArtifact.class.getName()) ||
		    		    a.getArtifactType().equals(AgentBodyArtifact.class.getName())) {
		    			System.out.println("**** disposing "+a.getName());
		    			envSession.doAction(new Op("disposeArtifact", new Object[] { a }), a.getWorkspaceId(), null, -1);  
		    		}
		    	}
		    } catch (Exception e) {
		    	e.printStackTrace();
		    }
		}
	}

	@Override
	public void act(ActionExec actionExec) {
		// logger.info("NEW ACTION  "+actionExec.getActionTerm()+" agent: "+this.getAgName());
			
		Intention currentIntention = getTS().getC().getSelectedIntention();
		
		Structure action = actionExec.getActionTerm();

		ArtifactId aid = null;
		WorkspaceId wspId = null;
		String artName = null;
		String wspName = null;
		try {
			boolean failed = false;
			ListTerm lt = action.getAnnots();
			if (lt != null) {
				Iterator<Term> it = lt.iterator();
				while (it.hasNext()) {
					Term annot = it.next();
					if (annot.isStructure()) {
						Structure st = (Structure) annot;
						if (st.getFunctor().equals("art") || st.getFunctor().equals("artifact_name")) {
							if (st.getTerm(0).isString())
								artName = ((StringTerm) (st.getTerm(0))).getString();
							else
								artName = st.getTerm(0).toString();
						} else if (st.getFunctor().equals("aid") || st.getFunctor().equals("artifact_id")) {
							Object obj = getObject(st.getTerm(0));
							if (obj != null && obj instanceof ArtifactId) {
								aid = (ArtifactId) obj;
							}
						} else if (st.getFunctor().equals("wid")) {
							Object obj = getObject(st.getTerm(0));
							if (obj != null && obj instanceof WorkspaceId) {
								wspId = (WorkspaceId) obj;
							}
						} else if (st.getFunctor().equals("wsp")) {
							if (st.getTerm(0).isString())
								wspName = ((StringTerm) (st.getTerm(0))).getString();
							else
								wspName = st.getTerm(0).toString();
						} else {
							logger.warning("Use failed: unknown annotation " + annot);
							Term reason = Literal.parseLiteral("action_failed(" + action + ",unknown_annotation)");
							String msg = "Use  error - unknown annotation " + annot;
							notifyActionFailure(actionExec, reason, msg);
							failed = true;
							break;
						}
					}
				}
			}
			
			if (!failed) {
				// parse op
				Op op = parseOp(action);
				IAlignmentTest test = null;
				long timeout = Long.MAX_VALUE;

				long actId   = -1;
				if (aid != null) {
					/* general case - the artifact id is known */
					actId = envSession.doAction(aid, op, test, timeout);
				} else if (wspId != null) {
					if (artName != null) {
						/* the artifact name is known */
						actId = envSession.doAction(wspId, artName, op, test, timeout);
					} else {
						/* only the workspace id is known */
						actId = envSession.doAction(op, wspId, test, timeout);
					}				
				} else if (wspName != null) {
					if (artName != null) {
						/* artifact name and wsp name known */
						actId = envSession.doAction(wspName, artName, op, test, timeout);
					} else {
						// only operation + wsp name known
						actId = envSession.doAction(op, wspName, test, timeout);
					}

				}
				
				if (actId == -1 && !isCartagoOperation(op.getName())) {
					// try using assigned namespaces x artefact
					outer: for (ArtifactId aid1 : focusedArtifacts(action.getNS())) {// iterates artifacts focused using nsp associated with the action
						try {
							ArtifactInfo info = CartagoEnvironment.getInstance()
									.getController(aid1.getWorkspaceId().getFullName())
									.getArtifactInfo(aid1.getName());
							for (OpDescriptor o : info.getOperations()) {
								if (o.getOp().getName().equals(op.getName())) { // if artifact aid1 implements op then
									actId = envSession.doAction(aid1, op, test, timeout); 
									break outer; // action executes a corresponding op in only one artifact
								}
							}
						} catch (CartagoException e) {
							// can be ignored (?)
							//e.printStackTrace();
						}
					}					
				}
				
				if (actId == -1) {
										
					/* 
					 * Op with implicit artifact & workspace 
					 * 
					 * According to CArtAgO semantics, the operation will be performed
					 * on the last workspace joined by the agent.
					 * 
					 */
					
					/* define which is the current wsp, related to the intention */ 
					
					WorkspaceId currentImplicitWsp = null;
					LinkedList<WorkspaceId> wspIdList = currentWspIntentionMap.get(currentIntention);
					if (wspIdList != null) {
						currentImplicitWsp = wspIdList.getLast();
					} else {
						/* if no info about specific intention => let's assume the last wsp joined */
						currentImplicitWsp = this.lastWspId;
					}
					
					// implicit artifact
				
					/*
					 * handling special cases
					 */
					if (op.getName().equals("joinWorkspace")){
						
						/* make the wspRef absolute if needed, depending on the currentWsp */
						
						Object[] params = op.getParamValues();
						if (params.length > 0) {
							String wspRef = (String) params[0];
							if (!wspRef.startsWith("/")) {
								params[0] = currentImplicitWsp.getFullName() + "/" + wspRef;
							} 
						}			
						actId = envSession.doAction(envSession.getAgentSessionArtifactId(), op, test, timeout);
						pendingJoinWsp.put(actId, currentIntention);
					} else if (op.getName().equals("createWorkspace")) {
						Object[] params = op.getParamValues();
						if (params.length > 0) {
							String wspRef = (String) params[0];
							int index = wspRef.indexOf('/');
							if (index == -1) {
								/* action on the workspace artifact of the implicit workspace */
								actId = envSession.doAction(op, currentImplicitWsp, test, timeout);
							} else {
								String fullPath = wspRef;
								if (!wspRef.startsWith("/")) {
									fullPath = currentImplicitWsp.getFullName() + "/" + wspRef;
								}
								fullPath = resolveRelativePath(fullPath);
								int index2 = fullPath.lastIndexOf('/');
								String wspRef2 = fullPath.substring(0, index2);
								artName = fullPath.substring(index2 + 1);
								
								WorkspaceDescriptor des = CartagoEnvironment.getInstance().resolveWSP(wspRef2);
								
								/* override the workspace full name in the op with the simple name */
								params[0] = artName;
								actId = envSession.doAction(op, des.getId(), test, timeout);
							}
						}							
						
					} else if (op.getName().equals("lookupArtifact") || op.getName().equals("makeArtifact")) {
						Object[] params = op.getParamValues();
						if (params.length > 0) {
							String artRef = (String) params[0];
							int index = artRef.indexOf('/');
							if (index == -1) {
								/* action on the workspace artifact of the implicit workspace */
								actId = envSession.doAction(op, currentImplicitWsp, test, timeout);
							} else {
								String fullPath = artRef;
								if (!artRef.startsWith("/")) {
									fullPath = currentImplicitWsp.getFullName() + "/" + artRef;
								}
								
								fullPath = resolveRelativePath(fullPath);
								
								int index2 = fullPath.lastIndexOf('/');
								String wspRef = fullPath.substring(0, index2);
								artName = fullPath.substring(index2 + 1);
								
								WorkspaceDescriptor des = CartagoEnvironment.getInstance().resolveWSP(wspRef);
								if (des.isLocal()) {
									/* use only the name */
									params[0] = artName;
									actId = envSession.doAction(op, des.getId(), test, timeout);
								} else {
									wspId = CartagoEnvironment.getInstance().getRootWSP().getId();
									actId = envSession.doAction(op, wspId, test, timeout);
								}
							}
						}							
					}  else if (op.getName().equals("quitWorkspace")) {
						actId = envSession.doAction(envSession.getAgentSessionArtifactId(), op, test, timeout);
					} else {
						actId = envSession.doActionWithImplicitCtx(op, currentImplicitWsp, test,timeout);
					}
				}

				if (actId != -1) {
					PendingAction pa = new PendingAction(actId, action, (ActionExec) actionExec);
					pendingActions.put(actId, pa);
					//getTS().getLogger().info("Agent "+getAgName()+" executed op: "+op.getName()+" on artifact "+aid);
				} else {
					String msg = "Action failed: " + actionExec.getActionTerm()+", in namespace " + action.getNS() + ", operation " + op;
					logger.warning(msg);
					Term reasonTerm = null;
					try {
						reasonTerm = Literal.parseLiteral("action_failed(" + action.getFunctor() + ",op("+action.getNS()+",\""+op+"\"))");
					} catch (Throwable serror) {
						reasonTerm = Literal.parseLiteral("action_failed(" + action.getFunctor() + ",op(notparsedop))");	
					}
					Literal reason = ASSyntax.createLiteral("env_failure", reasonTerm);
					notifyActionFailure(actionExec, reason, msg);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.warning("Op " + action + " on artifact " + aid + "(artifact_name= " + artName + ") by " + this.getAgName() + " failed - op: " + action);
			Term reasonTerm = Literal.parseLiteral("action_failed(" + action.getFunctor() + ",generic_error)");
			Literal reason = ASSyntax.createLiteral("env_failure", reasonTerm);
			String msg = "Action failed: " + actionExec.getActionTerm() + ". "+ex.getMessage();
			notifyActionFailure(actionExec, reason, msg);
		}
	}
	
	boolean isCartagoOperation(String o) {
		return o.equals("joinWorkspace") ||
			   o.equals("createWorkspace") ||
			   o.equals("lookupArtifact") ||
			   o.equals("makeArtifact") ||
			   o.equals("quitWorkspace");
				
	}
	private int nbAcumEvents = 0;

	private String resolveRelativePath(String path) {
		String[] parts = path.split("/");
		List<String> list = new ArrayList<String>();
		for (String p: parts) {
			if (!p.equals("")) {
				list.add(p);
			}
		}
		int index = 0;
		while (index < list.size()) {
			String elem = list.get(index);
			if (elem.equals(".")) {
				list.remove(index);
			} else if (elem.equals("..")) {
				list.remove(index);
				if (list.size() > 0) {
					list.remove(index - 1);
				} else {
					return null;
				}
			} else {
				index++;
			}
		}
		StringBuffer sb = new StringBuffer();
		for (String s: list) {
			sb.append("/"+s);
		}
		return sb.toString();
	}
	
	@Override
	public Collection<Literal> perceive() {
		if (envSession == null) // the init isn't finished yet...
			return super.perceive();

		try {
			CartagoEvent evt = envSession.fetchNextPercept();
			while (evt != null) {
				nbAcumEvents++;
				
				if (evt instanceof ActionSucceededEvent) {
					perceiveActionSucceeded((ActionSucceededEvent) evt);
				} else if (evt instanceof ActionFailedEvent) {
					perceiveActionFailed((ActionFailedEvent) evt);
				} else if (evt instanceof FocussedArtifactDisposedEvent) {
					perceiveDispose((FocussedArtifactDisposedEvent) evt);
				} else if (evt instanceof ArtifactObsEvent) {
					ArtifactObsEvent ev = (ArtifactObsEvent) evt;
					perceiveSignal(ev);
					perceivedChangedOP(ev);
					perceiveAddedOP(ev);
					perceiveRemovedOP(ev);
				} else if (evt instanceof ObsArtListChangedEvent) {
					/* experimental */
					ObsArtListChangedEvent ev = (ObsArtListChangedEvent) evt;
					List<ObservableArtifactInfo> newFocused = ev.getNewFocused();
					for (ObservableArtifactInfo info : newFocused) {
						// System.out.println("topology info: new observable: "+info.getTargetArtifact());
						addObsPropertiesBel(info.getTargetArtifact(), info.getObsProperties(), Literal.DefaultNS);
					}
					List<ObservableArtifactInfo> lostFocus = ev.getNoMoreFocused();
					for (ObservableArtifactInfo info : lostFocus) {
						// System.out.println("topology info: no more observable: "+info.getTargetArtifact());
						this.removeObsPropertiesBel(info.getTargetArtifact(), info.getObsProperties(), Literal.DefaultNS);
					}
				}
				evt = envSession.fetchNextPercept();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.severe("Exception in fetching events from the context.");
		}
		// THE METHOD MUST RETURN NULL: since the percept semantics is different (event vs. state), all the the percepts from the env must be managed here, not by the BUF
		// JH: change to return the normal answer in case other environment is being used
		return super.perceive();
	}
	
	@Override
    public Map<String,Object> getStatus() {
    	Map<String,Object> r = super.getStatus();
    	r.put("nbAcumEvents", this.nbAcumEvents);
		nbAcumEvents = 0;
    	return r;
	}

	private void perceiveAddedOP(ArtifactObsEvent ev) {
		ArtifactObsProperty[] props = ev.getAddedProperties();
		if (props != null) {
			if (mappings.get(ev.getArtifactId()) == null) {
				mappings.put(ev.getArtifactId(), new HashSet<Atom>());
				mappings.get(ev.getArtifactId()).add(Literal.DefaultNS);
			}
			for (Atom nsp : mappings.get(ev.getArtifactId())) { // for all name spaces this OP is mapped to:
				addObsPropertiesBel(ev.getArtifactId(), props, nsp);
			}
		}
	}

	private void perceiveRemovedOP(ArtifactObsEvent ev) {
		for (Atom nsp : mappings.get(ev.getArtifactId())) { // for all name spaces, remove there the OP
			removeObsPropertiesBel(ev.getArtifactId(), ev.getRemovedProperties(), nsp);
		}
	}

	private void perceivedChangedOP(ArtifactObsEvent ev) {
		ArtifactObsProperty[] props = ev.getChangedProperties();
		if (props != null) {
			if (mappings.get(ev.getArtifactId()) == null) { // any obs_prop when any mapping exists should be in default
				mappings.put(ev.getArtifactId(), new HashSet<Atom>());
				mappings.get(ev.getArtifactId()).add(Literal.DefaultNS);
			}

			for (Atom nsp : mappings.get(ev.getArtifactId())) {
				for (ArtifactObsProperty prop: props) {
					removeObsPropertiesBel(ev.getArtifactId(), prop, nsp);
					addObsPropertiesBel(ev.getArtifactId(), prop, nsp);	
				}
			}
		}
	}

	private void perceiveSignal(ArtifactObsEvent ev) {
		Tuple signal = ev.getSignal();
		if (signal != null) {
			if (mappings.get(ev.getArtifactId()) == null) { // any obs_prop when any mapping exists should be in default
				mappings.put(ev.getArtifactId(), new HashSet<Atom>());
				mappings.get(ev.getArtifactId()).add(Literal.DefaultNS);
			}
			// System.out.println("signal: "+signal);
			for (Atom nsp : mappings.get(ev.getArtifactId())) {
				Literal l = obsEventToLiteral(nsp, ev);
				if (l != null) {
					Trigger te = new Trigger(TEOperator.add, TEType.belief, l);
					getTS().updateEvents(new Event(te, Intention.EmptyInt));
				}
			}
		}
	}

	private void perceiveDispose(FocussedArtifactDisposedEvent ev) {
		// removeObsPropertiesBel(ev.getArtifactId(), ev.getObsProperties());
		for (Atom nsp : mappings.get(ev.getArtifactId()))
			removeObsPropertiesBel(ev.getArtifactId(), ev.getObsProperties(), nsp);
		mappings.remove(ev.getArtifactId());
	}

	private void perceiveActionFailed(ActionFailedEvent ev) {
		PendingAction action = pendingActions.remove(ev.getActionId());
		if (action != null) {
			try {
				Term reason = null;
				Tuple failureInfo = ev.getFailureDescr();
				try {
					if (failureInfo != null) {
						reason = lib.tupleToTerm(failureInfo);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				notifyActionFailure(action.getActionExec(), reason, ev.getFailureMsg());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private void perceiveActionSucceeded(ActionSucceededEvent ev) throws Exception, CartagoException, NoSuchFieldException, IllegalAccessException {
		if (ev.getActionId() == -1) {
			if (ev instanceof FocusSucceededEvent) {
				// this happens when an agent joins a wsp and an agent body artifact is created and focused automatically
				FocusSucceededEvent ev1 = (FocusSucceededEvent) ev;
				addObsPropertiesBel(ev1.getTargetArtifact(), ev1.getObsProperties(), Literal.DefaultNS);
			} else if (ev instanceof StopFocusSucceededEvent) {
				StopFocusSucceededEvent ev1 = (StopFocusSucceededEvent) ev;
				removeObsPropertiesBel(ev1.getTargetArtifact(), ev1.getObsProperties(), Literal.DefaultNS);
			}
		}

		PendingAction action = pendingActions.remove(ev.getActionId());
		// logger.info("Processing action succeeded: "+action.getAction());
		if (action != null) {
			notifyActionSuccess(ev.getOp(), action.getAction(), action.getActionExec());
			if (ev instanceof FocusSucceededEvent) {
				perceiveFocusSucceeded((FocusSucceededEvent) ev);
			} else if (ev instanceof StopFocusSucceededEvent) {
				perceiveStopFocus((StopFocusSucceededEvent) ev);
			} else if (ev instanceof JoinWSPSucceededEvent) {
				this.lastWspId = ((JoinWSPSucceededEvent) ev).getWorkspaceId();
				Intention intent = pendingJoinWsp.get(ev.getActionId());
				if (intent != null) {
					LinkedList<WorkspaceId> wsps = currentWspIntentionMap.get(intent);	
					if (wsps == null) {
						wsps = new LinkedList<WorkspaceId>();
						currentWspIntentionMap.put(intent, wsps);
					}
					if (wsps.contains(this.lastWspId)) {
						wsps.remove(this.lastWspId);
					}
					wsps.addLast(this.lastWspId);
				}
			} else if (ev instanceof QuitWSPSucceededEvent) {
				Intention intent = pendingJoinWsp.get(ev.getActionId());
				if (intent != null) {
					LinkedList<WorkspaceId> wsps = currentWspIntentionMap.get(intent);	
					if (wsps != null && wsps.contains(this.lastWspId)) {
						wsps.remove(this.lastWspId);
					}
				}
				
			} else if (ev instanceof ConsultManualSucceededEvent) {
				this.consultManual(((ConsultManualSucceededEvent) ev).getManual());
			}
		}
	}

	private void perceiveStopFocus(StopFocusSucceededEvent ev1) throws CartagoException, NoSuchFieldException, IllegalAccessException {
		// removeObsPropertiesBel(ev1.getTargetArtifact(), ev1.getObsProperties());
		Atom nsp = ((NameSpaceOp) ev1.getOp()).getNS();
		removeObsPropertiesBel(ev1.getTargetArtifact(), ev1.getObsProperties(), nsp);
		Set<Atom> aa = mappings.get(ev1.getTargetArtifact());
		if (aa != null) { 
			aa.remove(nsp);
			// The Observer is added again
			if (!aa.isEmpty()) {
				String wspName = ev1.getTargetArtifact().getWorkspaceId().getName();
				for (AgentId ag : CartagoEnvironment.getInstance().getController(wspName).getCurrentAgents()) // to get the agentId from agName
					if (ag.getAgentName().equals(getAgName())) {
						
						Workspace wsp = CartagoEnvironment.getInstance().resolveWSP(wspName).getWorkspace();
						/*
						Field f = class.getDeclaredField("instance"); // 1
						f.setAccessible(true); // 2
						CartagoNode node = (CartagoNode) f.get(CartagoEnvironment.class); // 3
						WorkspaceKernel kernel = node.getWorkspace(wspName).getKernel(); // 4
						f = kernel.getClass().getDeclaredField("artifactMap"); // 5
						f.setAccessible(true); // 6
						ArtifactDescriptor des = ((HashMap<String, ArtifactDescriptor>) f.get(kernel)).get(ev1.getTargetArtifact().getName()); // 'des' is what i want!
						*/
						ArtifactDescriptor des = wsp.getArtifactDescriptor(ev1.getTargetArtifact().getName());
						des.addObserver(ag, null, (ICartagoCallback) envSession);
						break;
					}
			} else {
				mappings.remove(ev1.getTargetArtifact());
			}
		}
	}

	private void perceiveFocusSucceeded(FocusSucceededEvent ev) {
		// addObsPropertiesBel(ev1.getTargetArtifact(), ev1.getObsProperties());
		Atom nsp = Literal.DefaultNS;
		if (ev.getOp() instanceof NameSpaceOp) { 
			nsp = ((NameSpaceOp) ev.getOp()).getNS();
			if (mappings.get(ev.getTargetArtifact()) == null)
				mappings.put(ev.getTargetArtifact(), new HashSet<Atom>());
			mappings.get(ev.getTargetArtifact()).add(nsp);
		}
		addObsPropertiesBel(ev.getTargetArtifact(), ev.getObsProperties(), nsp);
		
		// add focused/3 from body-art to its nsp
		/*
		Literal artName = null;
		try {
			// use parsing to consider the name space in the art name
			artName = new LiteralImpl(ASSyntax.parseLiteral(ev.getTargetArtifact().getName())); // create a new literal to avoid the parsing it as atom or internal.action			
		} catch (ParseException e) {
			artName = new LiteralImpl(ev.getTargetArtifact().getName());
		}
		try {
			// add artifact_type annot in the art name
			artName.addAnnot(ASSyntax.createStructure("artifact_type", ASSyntax.createString(ev.getTargetArtifact().getArtifactType())));
			
			Literal l = ASSyntax.createLiteral(nsp, "focused", 
					new StringTermImpl(ev.getTargetArtifact().getWorkspaceId().getFullName()),
					artName, 
					lib.objectToTerm(ev.getTargetArtifact()));
			l.addAnnot(BeliefBase.TPercept);

			agent.addBel(l);
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
	}

	protected Op parseOp(Structure action) {
		Object[] opArgs = new Object[action.getArity()];
		for (int i = 0; i < opArgs.length; i++) {
			opArgs[i] = lib.termToObject(action.getTerm(i));
		}
		
		// some "filters" 
		/*if ("makeArtifact".equals(action.getFunctor()) || "focusWhenAvailable".equals(action.getFunctor())) { // artifact name is an atomic term, parse to string
			if (action.getTerm(0).isAtom())
				opArgs[0] = "" + action.getTerm(0); // consider the name space in the art name
		}*/
		
		return new NameSpaceOp(action.getFunctor(), opArgs, action.getNS());
	}

	protected boolean bind(Object obj, Term term, ActionExec act) {
		try {
			Term t = lib.objectToTerm(obj);
			Unifier un = act.getIntention().peek().getUnif();
			// System.out.println("BINDING obj "+obj+" term "+t+" with "+term);
			return un.unifies(t, term);
		} catch (Exception ex) {
			return false;
		}
	}

	@Override
	public void sendMsg(Message m) throws Exception {
		// if content is an mapped object, use it
		if (m.getPropCont() instanceof Atom) {
			Object o = lib.getObject((Atom)m.getPropCont());
			if (o != null)
				m.setPropCont(o);
		}
		super.sendMsg(m);
	}
	
	protected Object getObject(Term t) {
		return lib.termToObject(t);
	}

	public JavaLibrary getJavaLib() {
		return lib;
	}

	protected void notifyActionSuccess(Op op, Structure action, ActionExec actionExec) {
		Object[] values = op.getParamValues();
		for (int i = 0; i < action.getArity(); i++) {
			if (action.getTerm(i).isVar()) { // isVar means is a variable AND is not bound (see Jason impl)
				try {
					boolean bound = bind(values[i], action.getTerm(i), actionExec);
					if (!bound) {
						// env.logger.severe("INTERNAL ERROR: binding failed "+values[i]+" "+action.getTerm(i));
						actionExec.setResult(false);
						Literal reason = ASSyntax.createLiteral("bind_param_error", action.getTerm(i), ASSyntax.createString(values[i]));
						actionExec.setFailureReason(reason, "Error binding parameters: " + action.getTerm(i) + " with " + values[i]);
						super.actionExecuted(actionExec);
						return;
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					return;
				}
			}
		}
		actionExec.setResult(true);
		super.actionExecuted(actionExec);
	}

	protected void notifyActionFailure(ActionExec actionExec, Term reason, String msg) {
		// logger.info("notified failure for "+actionExec.getActionTerm()+" - reason: "+reason);
		actionExec.setResult(false);
		Literal descr = null;
		if (reason != null) {
			descr = ASSyntax.createLiteral("env_failure_reason", reason);
		}
		actionExec.setFailureReason(descr, msg);
		super.actionExecuted(actionExec);
	}

	public boolean notifyCartagoEvent(CartagoEvent ev) {		
		// System.out.println("NOTIFIED "+ev.getId()+" "+ev.getClass().getCanonicalName());
		// logger.info("Notified event "+ev);
		getFirstAgArch().wake();
		return true; // true means that we want the event to be enqueued in the percept queue
	}

	/**
	 * Convert an observable event into a literal
	 */
	protected Literal obsEventToLiteral(Atom ns, ArtifactObsEvent p) {
		Tuple signal = p.getSignal();
		try {
			Literal struct = ASSyntax.createLiteral(ns, signal.getLabel(), lib.objectArray2termArray(signal.getContents()));
			struct.addAnnot(BeliefBase.TPercept);
			struct.addAnnot(OBS_EV_PERCEPT);
			addSourceAnnots(p.getArtifactId(), struct);
			// struct.addAnnot(ASSyntax.createStructure("time", id, ASSyntax.createNumber(p.getId())));
			return struct;
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "error creating literal for " + p.getSignal(), ex);
			return null;
		}
	}

	/**
	 * Convert an observable property into a literal
	 */
	protected JaCaLiteral obsPropToLiteral(Atom ns, ArtifactObsProperty prop, ArtifactId source) throws Exception {
		if (Character.isUpperCase(prop.getName().charAt(0)))
			logger.warning("Observable Property "+prop.getName()+" starts with upper case and will cause problems when perceived by the agentes.");
		JaCaLiteral struct = new JaCaLiteral(ns, prop.getName(), prop.getFullId());

		for (Object obj : prop.getValues()) {
			struct.addTerm(lib.objectToTerm(obj));
		}

		if (prop.getAnnots() != null)
			for (Object o: prop.getAnnots())
				struct.addAnnot(lib.objectToTerm(o));
		
		struct.addAnnot(BeliefBase.TPercept);
		struct.addAnnot(OBS_PROP_PERCEPT);
		//struct.addAnnot(ASSyntax.createStructure("obs_prop_id", ASSyntax.createString(prop.getFullId())));
		addSourceAnnots(source, struct);
		return struct;
	}

	private void addSourceAnnots(ArtifactId source, Literal struct) throws Exception {
		Term id = lib.objectToTerm(source);
		Term artName = null;
		try {
			artName = ASSyntax.parseTerm(source.getName());
		} catch (Exception e) {
			artName = ASSyntax.createAtom(source.getName().replaceAll("-", "_"));
		}
		//struct.addAnnot(ASSyntax.createStructure("source", id));
		struct.addAnnot(ASSyntax.createStructure("artifact_id", id));
		struct.addAnnot(ASSyntax.createStructure("artifact_name", artName));
		//struct.addAnnot(ASSyntax.createStructure("artifact_type", id, ASSyntax.createString(source.getArtifactType())));
		struct.addAnnot(ASSyntax.createStructure("workspace", ASSyntax.createString(source.getWorkspaceId().getFullName()), lib.objectToTerm(source.getWorkspaceId())));
	}
	
	class JaCaLiteral extends LiteralImpl {
		private static final long serialVersionUID = 1L;
		
		String obsPropId;
		public JaCaLiteral(Atom ns, String f, String opi) {
			super(ns, Literal.LPos, f);
			obsPropId = opi;
		}
		public JaCaLiteral(JaCaLiteral jl) {
			super(jl.getNS(), Literal.LPos, jl);
			obsPropId = jl.obsPropId;
		}
		public JaCaLiteral(JaCaLiteral jl, Unifier u) {
			super(jl, u);
			obsPropId = jl.obsPropId;
		}

		public String getObsPropId() {
			return obsPropId;
		}
		
		@Override
	    public boolean subjectToBUF() {
	        return false;
	    }
		
		@Override
		public Literal copy() {
			return new JaCaLiteral(this);
		}
		@Override
		public Term capply(Unifier u) {
	        return new JaCaLiteral(this, u);
		}
		
		boolean hasPropId(String id) {
			return obsPropId.equals(id);
		}
		
		@Override
		public boolean equalsAsStructure(Object p) {
			return p instanceof JaCaLiteral && this.obsPropId.equals( ((JaCaLiteral)p).obsPropId);
		}
		
		@Override
		public int hashCode() {
			return obsPropId.hashCode();
		}
	}

	// manuals

	protected void consultManual(Manual man) {
		// per JASDL: getJom e loadOntology
		System.out.println(">> CONSULT MANUAL << " + man.getURI());
	}

	public IAgentSession getEnvSession() {
		return envSession;
	}

	public Collection<WorkspaceId> getAllJoinedWsps() {
		try {
			return getEnvSession().getJoinedWorkspaces();
		} catch (CartagoException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static CAgentArch getCartagoAgArch(TransitionSystem ts) {
		AgArch arch = ts.getAgArch();
		while (arch != null) {
			if (arch instanceof CAgentArch)
				return (CAgentArch) arch;
			arch = arch.getNextAgArch();
		}
		return null;
	}

	public void addObsPropertiesBel(ArtifactId source, List<ArtifactObsProperty> props, Atom nsp) {
		for (ArtifactObsProperty prop : props) {
			addObsPropertiesBel(source, prop, nsp);
		}
	}
	public void addObsPropertiesBel(ArtifactId source, ArtifactObsProperty[] props, Atom nsp) {
		if (props != null)
			for (ArtifactObsProperty prop : props) 
				addObsPropertiesBel(source, prop, nsp);
	}	
	
	public void addObsPropertiesBel(ArtifactId source, ArtifactObsProperty prop, Atom nsp) {
		try {
			nsp = processSpecialOP(source, prop, nsp);

			if (nsp != null) {
				JaCaLiteral l = obsPropToLiteral(nsp, prop, source);
				if (belBase.add(l)) { // NB: we are bypassing BRF using this!
					Trigger te = new Trigger(TEOperator.add, TEType.belief, l.copy());
					getTS().updateEvents(new Event(te, Intention.EmptyInt));
					// logger.info("AGENT: "+getTS().getUserAgArch().getAgName()+" NEW BELIEF FOR OBS PROP: "+l1);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.warning("EXCEPTION - processing new obs prop " + prop + " artifact " + source + " for agent " + getTS().getAgArch().getAgName());
		}
	}

	private Atom processSpecialOP(ArtifactId source, ArtifactObsProperty prop, Atom nsp) throws Exception {
		String artType = source.getArtifactType();
		if (artType.equals(AgentBodyArtifact.class.getName())) {
			
			/* handling legacy - focused */

			if (prop.getName().equals("focusing")) {
			
				// translate string to atoms for focusing/6
				// focusing (ArtId, ArtName, ArtType, WspId, WspName, WspFullName)			
				
				prop.getValues()[0] = lib.objectToTerm(prop.getValue(0));
				prop.getValues()[1] = ASSyntax.parseTerm(prop.getValue(1).toString());
				prop.getValues()[3] = lib.objectToTerm(prop.getValue(3));
				prop.getValues()[4] = ASSyntax.parseTerm(prop.getValue(4).toString()); 
			}
			
			/*
			if (prop.getName().equals("focused")) {
				// focusing in handled by processFocusSucceeded
				prop.getValues()[0] = ASSyntax.parseTerm(prop.getValue(0).toString()); 
				prop.getValues()[1] = ASSyntax.parseTerm(prop.getValue(1).toString()); 
				prop.getValues()[2] = lib.objectToTerm(prop.getValue(2));
				
				if (prop.getValue(1).toString().startsWith("body_")) {
					return null;
				} else {
					// add artifact_type annot in the art name
					prop.getValues()[0] = new Atom(prop.getValues()[0].toString());
					Literal art = ASSyntax.createLiteral(prop.getValues()[1].toString());
					// discover type of the art
					String type = CartagoEnvironment.getInstance().getController(prop.getValues()[0].toString()).getArtifactInfo(prop.getValues()[1].toString()).getId().getArtifactType();
					art.addAnnot(ASSyntax.createStructure("artifact_type", ASSyntax.createString(type)));
					prop.getValues()[1] = art;
				}
			}
			*/
			
		} else if (artType.equals(AgentSessionArtifact.class.getName())) {
			if (prop.getName().equals("joinedWsp")) {
				
				// translate string to atoms for joinedWsp/3
				// joinedWsp(WspId, WspName, WspFullName)
				
				prop.getValues()[0] = lib.objectToTerm(prop.getValue(0));
				prop.getValues()[1] = ASSyntax.parseTerm(prop.getValue(1).toString()); // to consider the use of namespaces in the art id
				// the third parameter is a full name (String)
			}
		}
		return nsp;
	}

	public void removeObsPropertiesBel(ArtifactId source, List<ArtifactObsProperty> props, Atom nsp) {
		for (ArtifactObsProperty prop: props)
			removeObsPropertiesBel(source, prop, nsp);
	}
	public void removeObsPropertiesBel(ArtifactId source, ArtifactObsProperty[] props, Atom nsp) {
		if (props != null) 
			for (ArtifactObsProperty prop: props) 			
				removeObsPropertiesBel(source, prop, nsp);
	}

	public boolean removeObsPropertiesBel(ArtifactId source, ArtifactObsProperty prop, Atom nsp) {		
		try {
			nsp = processSpecialOP(source, prop, nsp);
			if (nsp == null)
				return false;
			Literal removedLit = obsPropToLiteral(nsp, prop, source);
			Literal asInBB     = belBase.contains(removedLit);
			if (belBase.remove(removedLit)) {
				Trigger te = new Trigger(TEOperator.del, TEType.belief, asInBB);
				getTS().updateEvents(new Event(te, Intention.EmptyInt));
				// logger.info("AGENT: "+getTS().getUserAgArch().getAgName()+" REMOVED BELIEF FOR OBS PROP: "+removedLit+" "+removedLit.getAnnots());
			}
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.warning("EXCEPTION - processing remove obs prop " + prop + " for agent " + getTS().getAgArch().getAgName());
		}
		return false;
	}

	/*private List<ArtifactId> focusedArtifacts(Atom nid) {
		List<ArtifactId> aids = new ArrayList<>();
		for (ArtifactId aid : mappings.keySet())
			if (mappings.get(aid).contains(nid))
				aids.add(aid);

		return aids;
	}*/
	
	
	private void writeObject(java.io.ObjectOutputStream stream) throws IOException {
        try {
        	focusedArts = computeFocusedArts();
        } catch (CartagoException e) {
            e.printStackTrace();
        }
        stream.defaultWriteObject();
    }
	private Set<ArtifactId> computeFocusedArts() throws CartagoException {
		Set<ArtifactId> farts = new HashSet<>();
    	for (WorkspaceId w: envSession.getJoinedWorkspaces()) {
    		ICartagoController ctrl = CartagoEnvironment.getInstance().getController(w.getFullName());
    		for (ArtifactId aid : ctrl.getCurrentArtifacts()) {
    			ArtifactInfo info = ctrl.getArtifactInfo(aid.getName());
                info.getObservers().forEach(y -> {
                    if (y.getAgentId().getAgentName().equals(getAgName())) {                        	
                    	farts.add(info.getId());
                    }
                });
            }
    	}
    	return farts;
	}
	public Collection<ArtifactId> getFocusedArtsBeforeSerialization() {
		return focusedArts;
	}
	
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		logger = Logger.getLogger("CAgentArch");		
	}
	
	private List<ArtifactId> focusedArtifacts(Atom nid) {
		List<ArtifactId> aids = new ArrayList<>();
		for (ArtifactId aid : mappings.keySet())
			if (mappings.get(aid).contains(nid))
				aids.add(aid);

		return aids;
	}
	
	@Override
	public String toString() {
		return "Cartago ag arch for "+getAgName();
	}
}