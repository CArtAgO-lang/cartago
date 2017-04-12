package jaca;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import cartago.AgentBodyArtifact;
import cartago.AgentId;
import cartago.ArtifactDescriptor;
import cartago.ArtifactId;
import cartago.ArtifactObsProperty;
import cartago.CartagoEvent;
import cartago.CartagoException;
import cartago.CartagoNode;
import cartago.CartagoService;
import cartago.IAlignmentTest;
import cartago.ICartagoCallback;
import cartago.ICartagoController;
import cartago.ICartagoSession;
import cartago.Manual;
import cartago.ObservableArtifactInfo;
import cartago.Op;
import cartago.OpDescriptor;
import cartago.Tuple;
import cartago.WorkspaceId;
import cartago.WorkspaceKernel;
import cartago.events.ActionFailedEvent;
import cartago.events.ActionSucceededEvent;
import cartago.events.ArtifactObsEvent;
import cartago.events.ConsultManualSucceededEvent;
import cartago.events.FocusSucceededEvent;
import cartago.events.FocussedArtifactDisposedEvent;
import cartago.events.JoinWSPSucceededEvent;
import cartago.events.ObsArtListChangedEvent;
import cartago.events.StopFocusSucceededEvent;
import jason.architecture.AgArch;
import jason.asSemantics.ActionExec;
import jason.asSemantics.Event;
import jason.asSemantics.Intention;
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
import jason.asSyntax.parser.ParseException;
import jason.bb.BeliefBase;

public class CAgentArch extends AgArch implements cartago.ICartagoListener {

	static protected final Term OBS_PROP_PERCEPT = ASSyntax.createStructure("percept_type", ASSyntax.createAtom("obs_prop"));
	static protected final Term OBS_EV_PERCEPT = ASSyntax.createStructure("percept_type", ASSyntax.createAtom("obs_ev"));

	private HashMap<ArtifactId, HashSet<Atom>> mappings = new HashMap<ArtifactId, HashSet<Atom>>();
	static private final List<String> DEF_OPS = Arrays.asList( 
		      "makeArtifact","removeArtifactFactory","addArtifactFactory","lookupArtifactByType","lookupArtifact","focusWhenAvailable",
		      "disposeArtifact","quitWorkspace","linkArtifacts","stopFocus","getCurrentArtifacts","focus","init","getRoleList","setSecurityManager",
		      "addRole","removeRole","addRolePolicy","removeRolePolicy","setDefaultRolePolicy","out","in","inp","rd","rdp","joinRemoteWorkspace",
		      "getNodeId","enableLinkingWithNode","shutdownNode","crash","joinWorkspace","createWorkspace","print","println");

	protected ICartagoSession envSession;

	// actions that have been executed and wait for a completion events
	protected ConcurrentHashMap<Long, PendingAction> pendingActions;

	// each agent has its own Java object map
	protected JavaLibrary lib;

	protected Logger logger;

	// private boolean firstManualFetched;

	// short cuts
	protected jason.bb.BeliefBase belBase;
	protected jason.asSemantics.Agent agent;

	List<WorkspaceId> allJoinedWsp = new ArrayList<WorkspaceId>(); // used in stopAg to quit this workspaces

	public CAgentArch() {
		super();
		pendingActions = new ConcurrentHashMap<Long, PendingAction>();

		logger = Logger.getLogger("CAgentArch");
		lib = new JavaLibrary();
	}

	public ICartagoSession getSession() {
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
		String agentName = getTS().getUserAgArch().getAgName();
		try {
			this.agent = getTS().getAg();
			this.belBase = agent.getBB();

			envSession = CartagoEnvironment.getInstance().startSession(agentName, this);

			allJoinedWsp.addAll(envSession.getJoinedWorkspaces());
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.warning("[CARTAGO] WARNING: No default workspace found for " + agentName);
		}
	}

	@Override
	public void act(ActionExec actionExec) {
		// logger.info("NEW ACTION  "+actionExec.getActionTerm()+" agent: "+this.getAgName());
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
				long actId   = Long.MIN_VALUE;
				if (aid != null) {
					actId = envSession.doAction(aid, op, test, timeout);
				} else if (wspId != null) {
					if (artName != null) {
						actId = envSession.doAction(wspId, artName, op, test, timeout);
					} else {
						actId = envSession.doAction(wspId, op, test, timeout);
					}
				} else if (wspName != null) {
					if (artName != null) {
						actId = envSession.doAction(wspName, op, artName, test, timeout);
					} else {
						actId = envSession.doAction(wspName, op, test, timeout);
					}

				} else {
					if (artName != null) {
						actId = envSession.doAction(op, artName, test, timeout);
					} else {
						if (DEF_OPS.contains(op.getName())) { // predefined CArtAgO operation
							actId = envSession.doAction(op, test, timeout); // default operations go to workspace
						} else { 
							// User defined operation
							outer: for (ArtifactId aid1 : focusedArtifacts(action.getNS())) {// iterates artifacts focused using nsp associated with the action
								ICartagoController c;
								try {
									c = CartagoService.getController(aid1.getWorkspaceId().getName());
								} catch (CartagoException e) {
									// can be ignored (?)
									c = null;
								}
								if (c != null) {
									for (OpDescriptor o : c.getArtifactInfo(aid1.getName()).getOperations()) {
										if (o.getOp().getName().equals(op.getName())) { // if artifact aid1 implements op then
											actId = envSession.doAction(aid1, op, test, timeout); //
											break outer; // action executes a corresponding op in only one artifact
										}
									}
								}
							}
							// TODO: decide wheter to try this (in all wkspaces!)
							if (actId == Long.MIN_VALUE) {
								// try as before name spaces
								actId = envSession.doAction(op,test,timeout);
							}
						}
					}
				}

				if (actId != Long.MIN_VALUE) {
					PendingAction pa = new PendingAction(actId, action, (ActionExec) actionExec);
					pendingActions.put(actId, pa);
					// getLogger().info("Agent "+agName+" executed op: "+op.getName()+" on artifact "+aid);
				} else {
					String msg = "Action failed: " + actionExec.getActionTerm()+". No artifact in namespace " + action.getNS() + " implements operation " + op;
					logger.warning(msg);
					Term reasonTerm = Literal.parseLiteral("action_failed(" + action + ",no_art("+action.getNS()+",\""+op+"\"))");
					Literal reason = ASSyntax.createLiteral("env_failure", reasonTerm);
					notifyActionFailure(actionExec, reason, msg);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.warning("Op " + action + " on artifact " + aid + "(artifact_name= " + artName + ") by " + this.getAgName() + " failed - op: " + action);
			Term reasonTerm = Literal.parseLiteral("action_failed(" + action + ",generic_error)");
			Literal reason = ASSyntax.createLiteral("env_failure", reasonTerm);
			String msg = "Action failed: " + actionExec.getActionTerm() + ". "+ex.getMessage();
			notifyActionFailure(actionExec, reason, msg);
		}
	}

	public List<Literal> perceive() {
		super.perceive();
		if (envSession == null) // the init isn't finished yet...
			return null;

		try {
			CartagoEvent evt = envSession.fetchNextPercept();

			while (evt != null) {
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
		return null;
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
				allJoinedWsp.add(((JoinWSPSucceededEvent) ev).getWorkspaceId());
			} else if (ev instanceof ConsultManualSucceededEvent) {
				this.consultManual(((ConsultManualSucceededEvent) ev).getManual());
			}
		}
	}

	private void perceiveStopFocus(StopFocusSucceededEvent ev1) throws CartagoException, NoSuchFieldException, IllegalAccessException {
		// removeObsPropertiesBel(ev1.getTargetArtifact(), ev1.getObsProperties());
		Atom nsp = ((NameSpaceOp) ev1.getOp()).getNS();
		removeObsPropertiesBel(ev1.getTargetArtifact(), ev1.getObsProperties(), nsp);
		mappings.get(ev1.getTargetArtifact()).remove(nsp);
		// The Observer is added again
		if (!mappings.get(ev1.getTargetArtifact()).isEmpty()) {
			String wspName = ev1.getTargetArtifact().getWorkspaceId().getName();
			for (AgentId ag : CartagoService.getController(wspName).getCurrentAgents()) // to get the agentId from agName
				if (ag.getAgentName().equals(getAgName())) {
					Field f = CartagoService.class.getDeclaredField("instance"); // 1
					f.setAccessible(true); // 2
					CartagoNode node = (CartagoNode) f.get(CartagoService.class); // 3
					WorkspaceKernel kernel = node.getWorkspace(wspName).getKernel(); // 4
					f = kernel.getClass().getDeclaredField("artifactMap"); // 5
					f.setAccessible(true); // 6
					ArtifactDescriptor des = ((HashMap<String, ArtifactDescriptor>) f.get(kernel)).get(ev1.getTargetArtifact().getName()); // 'des' is what i want!
					des.addObserver(ag, null, (ICartagoCallback) envSession);
					break;
				}
		} else
			mappings.remove(ev1.getTargetArtifact());
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
					new Atom(ev.getTargetArtifact().getWorkspaceId().getName()),
					artName, 
					lib.objectToTerm(ev.getTargetArtifact()));
			l.addAnnot(BeliefBase.TPercept);

			agent.addBel(l);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		getArchInfraTier().wake();
		return true; // true means that we want the event to be enqueued in the percept queue
	}

	/**
	 * Convert an observable event into a literal
	 */
	protected Literal obsEventToLiteral(Atom ns, ArtifactObsEvent p) {
		Tuple signal = p.getSignal();
		try {
			Literal struct = ASSyntax.createLiteral(ns, signal.getLabel(), lib.objectArray2termArray(signal.getContents()));
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

		if ("obligation".equals(prop.getName()) || "prohibition".equals(prop.getName()) || "permission".equals(prop.getName())) {
			struct.addAnnot(ASSyntax.createStructure("norm", new Atom(prop.getValue(4).toString())));
			struct.delTerm(4);
		}
		
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
			artName = ASSyntax.createAtom(source.getName());
		}
		//struct.addAnnot(ASSyntax.createStructure("source", id));
		struct.addAnnot(ASSyntax.createStructure("artifact_id", id));
		struct.addAnnot(ASSyntax.createStructure("artifact_name", id, artName));
		//struct.addAnnot(ASSyntax.createStructure("artifact_type", id, ASSyntax.createString(source.getArtifactType())));
		struct.addAnnot(ASSyntax.createStructure("workspace", id, ASSyntax.createAtom(source.getWorkspaceId().getName()), lib.objectToTerm(source.getWorkspaceId())));
	}
	
	class JaCaLiteral extends LiteralImpl {
		String obsPropId;
		public JaCaLiteral(Atom ns, String f, String opi) {
			super(ns, Literal.LPos, f);
			obsPropId = opi;
		}
		public JaCaLiteral(JaCaLiteral jl) {
			super(jl.getNS(), Literal.LPos, jl);
			obsPropId = jl.obsPropId;
		}
		@Override
		public Literal copy() {
			return new JaCaLiteral(this);
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

	public ICartagoSession getEnvSession() {
		return envSession;
	}

	public List<WorkspaceId> getAllJoinedWsps() {
		return allJoinedWsp;
	}

	public static CAgentArch getCartagoAgArch(TransitionSystem ts) {
		AgArch arch = ts.getUserAgArch().getFirstAgArch();
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
			logger.warning("EXCEPTION - processing new obs prop " + prop + " artifact " + source + " for agent " + getTS().getUserAgArch().getAgName());
		}
	}

	private Atom processSpecialOP(ArtifactId source, ArtifactObsProperty prop, Atom nsp) throws Exception {
		if (source.getArtifactType().equals(AgentBodyArtifact.class.getName())) {
			// translate string to atoms for focused/3
			if ("focused".equals(prop.getName())) {
				// focused in handled by processFocusSucceeded
				return null;
				/*if (prop.getValue(1).toString().endsWith("-body")) {
					return null;
				} else {
					// add artifact_type annot in the art name
					prop.getValues()[0] = new Atom(prop.getValues()[0].toString());
					Literal art = ASSyntax.createLiteral(prop.getValues()[1].toString());
					// discover type of the art
					String type = CartagoService.getController(prop.getValues()[0].toString()).getArtifactInfo(prop.getValues()[1].toString()).getId().getArtifactType();
					art.addAnnot(ASSyntax.createStructure("artifact_type", ASSyntax.createString(type)));
					prop.getValues()[1] = art;
				}*/
			} else if ("joined".equals(prop.getName())) {
				prop.getValues()[0] = ASSyntax.parseTerm(prop.getValue(0).toString()); // to consider the use of namespaces in the art id
				prop.getValues()[1] = lib.objectToTerm(prop.getValue(1));
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
			logger.warning("EXCEPTION - processing remove obs prop " + prop + " for agent " + getTS().getUserAgArch().getAgName());
		}
		return false;
	}

	private List<ArtifactId> focusedArtifacts(Atom nid) {
		List<ArtifactId> aids = new ArrayList<ArtifactId>();
		for (ArtifactId aid : mappings.keySet())
			if (mappings.get(aid).contains(nid))
				aids.add(aid);

		return aids;
	}
}