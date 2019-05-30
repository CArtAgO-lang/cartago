package cartago;

import java.util.LinkedList;
import java.util.List;

import cartago.*;
import cartago.events.*;
import cartago.util.agent.*;
import cartago.util.agent.ObsPropMap;

/**
 * Basic utility class  to interact with CArtAgO from Java
 * without creating agents
 * 
 * Create a work session inside a CArtAgO environment.
 * 
 * @author aricci, the_dark
 *
 */
public class CartagoContext {

	private ICartagoSession session;
	private CartagoListener agentCallback;

	private ActionFeedbackQueue actionFeedbackQueue;
	private ObsEventQueue obsEventQueue; 

	private ObsPropMap obsPropMap;

	private static AgentCredential credential;
	private static String agentRole;
	
	private WorkspaceId implicitWspId;

	private final static IEventFilter firstEventFilter = new IEventFilter(){
		public boolean select(ArtifactObsEvent ev){
			return true;
		}
	}; 

	/**
	 * Create a new work session inside the environment (default workspace)
	 * 
	 * @param name agent name
	 */
	public CartagoContext(AgentCredential cred){
		super();
		agentCallback = new CartagoListener();
		actionFeedbackQueue = new ActionFeedbackQueue();
		obsEventQueue = new ObsEventQueue();
		obsPropMap = new ObsPropMap();
		credential = cred;
		agentRole = "";
		try {
			session = (CartagoSession) CartagoEnvironment.getInstance().startSession(CartagoEnvironment.ROOT_WSP_DEFAULT_NAME, credential, agentCallback);
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}

	/**
	 * Create a new work session inside the environment 
	 * 
	 * @param name agent name
	 * @param workspaceName workspace name
	 */
	public CartagoContext(AgentCredential cred, String workspaceName){
		super();
		agentCallback = new CartagoListener();
		actionFeedbackQueue = new ActionFeedbackQueue();
		obsEventQueue = new ObsEventQueue();
		obsPropMap = new ObsPropMap();
		credential = cred;
		agentRole = "";
		try {
			session = CartagoEnvironment.getInstance().startSession(workspaceName, credential, agentCallback);
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}
		
	/**
	 * Create a new work session inside a remote environment 
	 * 
	 * @param name agent name
	 * @param workspaceName workspace name
	 * @param workspaceHost workspace host
	 *//*
	public CartagoContext(AgentCredential cred, String workspaceName, String workspaceHost) {
		super();
		agentCallback = new CartagoListener();
		actionFeedbackQueue = new ActionFeedbackQueue();
		obsEventQueue = new ObsEventQueue();
		obsPropMap = new ObsPropMap();
		credential = cred;
		agentRole = "";
		try {
			session = CartagoEnvironment.getInstance().startRemoteSession(workspaceName, workspaceHost, "default", credential, agentCallback);
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}*/
	
	CartagoSession getCartagoSession(){
		return (CartagoSession) session;
	}
	
	/**
	 * Get the value of a property
	 * 
	 * @param name
	 * @return
	 */
	public cartago.util.agent.ArtifactObsProperty getObsProperty(String name){
		return obsPropMap.getByName(name);
	}
	
	/**
	 * Execute an action, without specifying the target artifact (non blocking)
	 * 
	 * @param op
	 * @return 
	 * @throws CartagoException
	 */
	public ActionFeedback doActionAsync(Op op) throws CartagoException {
		long id = session.doAction(op, implicitWspId, null, -1);
		ActionFeedback res = new ActionFeedback(id,actionFeedbackQueue);
		return res;
	}

	/**
	 * Execute an action, specifying the target artifact (non blocking)
	 * 
	 * @param aid
	 * @param op
	 * @return 
	 * @throws CartagoException
	 */
	public ActionFeedback doActionAsync(ArtifactId aid, Op op, long timeout) throws CartagoException {
		long id = session.doAction(aid, op, null, timeout);
		ActionFeedback res = new ActionFeedback(id,actionFeedbackQueue);
		return res;
	}

	/**
	 * Execute an action, without specifying the target artifact (non blocking) waiting for the provided timeout
	 * for the action to complete
	 * 
	 * @param op
	 * @param timeout
	 * @return 
	 * @throws CartagoException
	 */
	public ActionFeedback doActionAsync(Op op, long timeout) throws CartagoException {
		long id = session.doAction(op, implicitWspId, null, timeout);
		ActionFeedback res = new ActionFeedback(id,actionFeedbackQueue);
		return res;
	}


	/**
	 * Execute an action, without specifying the target artifact (blocking, until action completion) waiting until the provided timeout.
	 * The action is considered failed if not completed during the provided timeout.
	 * 
	 * @param op
	 * @return 
	 * @throws CartagoException
	 */
	public void doAction(Op op, long timeout) throws ActionFailedException, CartagoException {
		long id = session.doAction(op, implicitWspId, null, timeout);
		ActionFeedback res = new ActionFeedback(id,actionFeedbackQueue);
		try {
			res.waitForCompletion();
		} catch (Exception ex){
			throw new CartagoException();
		}
		boolean failed = false;
		try {
			if (res.failed()){
				failed = true;
			} else {
				Op retOp = res.getOp();
				// if it is a remote op
				if (retOp != op){
					Object[] params = op.getParamValues();
					Object[] newParams = retOp.getParamValues();
					for (int i = 0; i < params.length; i++){
						if (params[i] instanceof OpFeedbackParam<?>){
							((OpFeedbackParam<?>)params[i]).copyFrom(((OpFeedbackParam<?>)newParams[i]));
						}
					}
				}				
			}
		} catch (Exception ex){
			ex.printStackTrace();
			throw new CartagoException();
		}
		if (failed){
			ActionFailedEvent ev = (ActionFailedEvent)res.getActionEvent();
			throw new ActionFailedException(ev.getFailureMsg(),ev.getFailureDescr());
		}
	}

	/**
	 * Execute an action, without specifying the target artifact (blocking, until action completion)
	 * 
	 * @param op
	 * @return 
	 * @throws CartagoException
	 */
	public void doAction(Op op) throws ActionFailedException, CartagoException {
		this.doAction(op, -1);
	}

	/**
	 * Execute an action, specifying the target artifact (blocking, until action completion) waiting until the provided timeout.
	 * The action is considered failed if not completed during the provided timeout.
	 * 
	 * @param aid
	 * @param op
	 * @param timeout
	 * @return 
	 * @throws CartagoException
	 */
	public void doAction(ArtifactId aid, Op op, long timeout) throws ActionFailedException, CartagoException {
		long id = session.doAction(aid, op, null, timeout);
		ActionFeedback res = new ActionFeedback(id,actionFeedbackQueue);
		try {
			res.waitForCompletion();
		} catch (Exception ex){
			throw new CartagoException();
		}
		boolean failed = false;
		try {
			if (res.failed()){
				failed = true;
			} else {
				Op retOp = res.getOp();
				// if it is a remote op
				if (retOp != op){
					Object[] params = op.getParamValues();
					Object[] newParams = retOp.getParamValues();
					for (int i = 0; i < params.length; i++){
						if (params[i] instanceof OpFeedbackParam<?>){
							((OpFeedbackParam<?>)params[i]).copyFrom(((OpFeedbackParam<?>)newParams[i]));
						}
					}
				}				
			}
		} catch (Exception ex){
			ex.printStackTrace();
			throw new CartagoException();
		}
		if (failed){
			ActionFailedEvent ev = (ActionFailedEvent)res.getActionEvent();
			throw new ActionFailedException(ev.getFailureMsg(),ev.getFailureDescr());
		}
	}

	/**
	 * Execute an action, specifying the target artifact (blocking, until action completion)
	 * 
	 * @param op
	 * @return 
	 * @throws CartagoException
	 */
	public void doAction(ArtifactId aid, Op op) throws ActionFailedException, CartagoException {
		this.doAction(aid,op,-1);
	}
	
	/**
	 * Execute an action, specifying the target workspace (blocking, until action completion) waiting until the provided timeout.
	 * The action is considered failed if not completed during the provided timeout.
 	 *
	 * @param wspId
	 * @param op
	 * @param timeout
	 * @throws ActionFailedException
	 * @throws CartagoException
	 */
	public void doAction(Op op, WorkspaceId wspId, long timeout) throws ActionFailedException, CartagoException {
		long id = session.doAction(op, wspId, null, timeout);
		ActionFeedback res = new ActionFeedback(id,actionFeedbackQueue);
		try {
			res.waitForCompletion();
		} catch (Exception ex){
			throw new CartagoException();
		}
		boolean failed = false;
		try {
			if (res.failed()){
				failed = true;
			} else {
				Op retOp = res.getOp();
				// if it is a remote op
				if (retOp != op){
					Object[] params = op.getParamValues();
					Object[] newParams = retOp.getParamValues();
					for (int i = 0; i < params.length; i++){
						if (params[i] instanceof OpFeedbackParam<?>){
							((OpFeedbackParam<?>)params[i]).copyFrom(((OpFeedbackParam<?>)newParams[i]));
						}
					}
				}				
			}
		} catch (Exception ex){
			ex.printStackTrace();
			throw new CartagoException();
		}
		if (failed){
			ActionFailedEvent ev = (ActionFailedEvent)res.getActionEvent();
			throw new ActionFailedException(ev.getFailureMsg(),ev.getFailureDescr());
		}
	}

	/**
	 * Execute an action, specifying the target workspace (blocking, until action completion).
	 * @param wspId
	 * @param op
	 * @throws ActionFailedException
	 * @throws CartagoException
	 */
	public void doAction(Op op, WorkspaceId wspId) throws ActionFailedException, CartagoException {
		this.doAction(op, wspId, -1);
	}

	/**
	 * Fetch a percept - non blocking, null if no percept
	 * 
	 * @return
	 * @throws InterruptedException
	 */
	public Percept fetchPercept() throws InterruptedException {
		ArtifactObsEvent ev = obsEventQueue.fetch(firstEventFilter);
		if (ev != null) {
			return new Percept(ev);
		} else {
			return null;
		}
	}

	/**
	 * Fetch a percept specifying a filter - non blocking, null if no percept
	 * 
	 * @return
	 * @throws InterruptedException
	 */
	public Percept fetchPercept(IEventFilter filter) throws InterruptedException {
		ArtifactObsEvent ev = obsEventQueue.fetch(filter);
		if (ev != null) {
			return new Percept(ev);
		} else {
			return null;
		}
	}

	/**
	 * Block until a percept is perceived
	 * @return
	 * @throws InterruptedException
	 */
	public Percept waitForPercept() throws InterruptedException {
		ArtifactObsEvent ev = obsEventQueue.waitFor(firstEventFilter);
		return new Percept(ev);
	}

	/**
	 * Block until a percept satisfying a filter is perceived
	 * 
	 * @param filter
	 * @return
	 * @throws InterruptedException
	 */
	public Percept waitForPercept(IEventFilter filter) throws InterruptedException {
		ArtifactObsEvent ev = obsEventQueue.waitFor(filter);
		return new Percept(ev);
	}

	// Utility methods

	/**
	 * Join a workspace
	 * 
	 * @param wspName wsp name
	 * @param cred agent credential
	 */
	public WorkspaceId joinWorkspace(String wspName, AgentCredential cred) throws CartagoException {
		OpFeedbackParam<WorkspaceId> res = new OpFeedbackParam<WorkspaceId>();
		try{
			doAction(new Op("joinWorkspace", wspName, cred, res), -1);
		} catch (Exception ex){
			throw new CartagoException();
		}
		return res.get();
	}

	/**
	 * Join a remote workspace
	 * 
	 * @param wspName wsp name
	 * @param address address
	 * @param roleName role
	 * @param cred agent credentials
	 * @return
	 * @throws CartagoException
	 */
	public WorkspaceId joinRemoteWorkspace(String wspName, String address, String roleName, AgentCredential cred)  throws CartagoException {
		OpFeedbackParam<WorkspaceId> res = new OpFeedbackParam<WorkspaceId>();
		try{
			doAction(new Op("joinRemoteWorkspace", address, wspName, roleName, cred, res));
		} catch (Exception ex){
			throw new CartagoException();
		}
		return res.get();
	}

	/**
	 * Get the artifact id given its name
	 * 
	 * @param artifactName
	 * @return
	 * @throws CartagoException
	 */
	public ArtifactId lookupArtifact(String artifactName) throws CartagoException {
		OpFeedbackParam<ArtifactId> res = new OpFeedbackParam<ArtifactId>();
		try{
			doAction(new Op("lookupArtifact", artifactName, res));
		} catch (Exception ex){
			throw new CartagoException();
		}
		return res.get();
	}

	/**
	 * Get the artifact id given its name
	 * 
	 * @param artifactName name of the artifact
	 * @param wid workspace where to look at
	 * @return
	 * @throws CartagoException
	 */
	public ArtifactId lookupArtifact(WorkspaceId wid, String artifactName) throws CartagoException {
		OpFeedbackParam<ArtifactId> res = new OpFeedbackParam<ArtifactId>();
		try{
			doAction(new Op("lookupArtifact", artifactName, res), wid);
		} catch (Exception ex){
			throw new CartagoException();
		}
		return res.get();
	}
	
	/**
	 * Make a new artifact instance
	 * 
	 * @param artifactName logic name
	 * @param templateName type
	 * @return
	 * @throws CartagoException
	 */
	public ArtifactId makeArtifact(String artifactName, String templateName) throws CartagoException {
		OpFeedbackParam<ArtifactId> res = new OpFeedbackParam<ArtifactId>();
		try{
			doAction(new Op("makeArtifact", artifactName, templateName, new Object[0], res));
		} catch (Exception ex){
			throw new CartagoException();
		}
		return res.get();
	}

	
	/**
	 * Make a new artifact instance
	 * 
	 * @param artifactName logic name
	 * @param templateName type
	 * @return
	 * @throws CartagoException
	 */
	public ArtifactId makeArtifact(String artifactName, String templateName, Object[] params) throws CartagoException {
		OpFeedbackParam<ArtifactId> res = new OpFeedbackParam<ArtifactId>();
		try{
			doAction(new Op("makeArtifact", artifactName, templateName, params, res));
		} catch (Exception ex){
			throw new CartagoException();
		}
		return res.get();
	}


	/**
	 * Dispose an existing artifact
	 * 
	 * @param artifactId
	 * @throws CartagoException
	 */
	public void disposeArtifact(ArtifactId artifactId) throws CartagoException {
		try{
			doAction(new Op("disposeArtifact", artifactId));
		} catch (Exception ex){
			throw new CartagoException(); 
		}
	}

	/**
	 * Start observing an artifact
	 * 
	 * @param artifactId
	 * @throws CartagoException
	 */
	public void focus(ArtifactId artifactId) throws CartagoException {
		try{
			doAction(new Op("focus", artifactId));
		} catch (Exception ex){
			throw new CartagoException();
		}
	}

	/**
	 * Start observing an artifact
	 * 
	 * @param artifactId
	 * @throws CartagoException
	 */
	public void focus(ArtifactId artifactId, IEventFilter filter) throws CartagoException {
		try{
			doAction(new Op("focus", artifactId, filter));
		} catch (Exception ex){
			throw new CartagoException();
		}
	}
	
	/**
	 * Stop observing an artifact
	 * 
	 * @param artifactId
	 * @throws CartagoException
	 */
	public void stopFocus(ArtifactId artifactId) throws CartagoException {
		try{
			doAction(new Op("stopFocus", artifactId));
		} catch (Exception ex){
			throw new CartagoException();
		}
	}

	public void log(String msg){
		System.out.println("["+credential.getId()+"] "+msg);
	}

	public String getName() {
		return credential.getId();
	}


	/**
	 * Join a workspace
	 * 
	 * @param wspName wsp name
	 * @param cred agent credential
	 */
	public WorkspaceId joinWorkspace(String wspName) throws CartagoException {
		OpFeedbackParam<WorkspaceId> res = new OpFeedbackParam<WorkspaceId>();
		try{
			doAction(new Op("joinWorkspace", wspName, credential, res));
		} catch (Exception ex){
			throw new CartagoException();
		}
		return res.get();
	}

	/**
	 * Join a remote workspace
	 * 
	 * @param wspName wsp name
	 * @param address address
	 * @param roleName role
	 * @param cred agent credentials
	 * @return
	 * @throws CartagoException
	 */
	public WorkspaceId joinRemoteWorkspace(String wspName, String address)  throws CartagoException {
		OpFeedbackParam<WorkspaceId> res = new OpFeedbackParam<WorkspaceId>();
		try{
			doAction(new Op("joinRemoteWorkspace", address, wspName, agentRole, credential, res), -1);
		} catch (Exception ex){
			throw new CartagoException();
		}
		return res.get();
	}


	/**
	 * Make a new artifact instance in a specific workspace
	 * 
	 * @param artifactName logic name
	 * @param templateName type
	 * @param wid where to create the artifact
	 * @return
	 * @throws CartagoException
	 */
	public ArtifactId makeArtifact(WorkspaceId wid, String artifactName, String templateName) throws CartagoException {
		OpFeedbackParam<ArtifactId> res = new OpFeedbackParam<ArtifactId>();
		try{
			doAction(new Op("makeArtifact", artifactName, templateName, new Object[0], res), wid, -1);
		} catch (Exception ex){
			ex.printStackTrace();
			throw new CartagoException();
		}
		return res.get();
	}

	
	/**
	 * Make a new artifact instance 
	 * 
	 * @param artifactName logic name
	 * @param templateName type
	 * @param wid where to create the artifact
	 * @return
	 * @throws CartagoException
	 */
	public ArtifactId makeArtifact(WorkspaceId wid, String artifactName, String templateName, Object[] params) throws CartagoException {
		OpFeedbackParam<ArtifactId> res = new OpFeedbackParam<ArtifactId>();
		try{
			doAction(new Op("makeArtifact", artifactName, templateName, params, res), wid, -1);
		} catch (Exception ex){
			throw new CartagoException();
		}
		return res.get();
	}

	public List<WorkspaceId> getJoinedWorkspaces() throws CartagoException {
		return session.getJoinedWorkspaces();
	}

	// Utility methods

	public WorkspaceId getJoinedWspId(String wspName) throws CartagoException {
		
		for (WorkspaceId id: session.getJoinedWorkspaces()) {
			if (id.getName().equals(wspName)) {
				return id;
			}
		}
		throw new CartagoException("Workspace not joined.");
	}

	class CartagoListener implements ICartagoListener {

		public CartagoListener(){
		}

		public boolean notifyCartagoEvent(CartagoEvent ev) {
			//log("received event: "+ev);
			try {
				if (ev instanceof CartagoActionEvent){
					actionFeedbackQueue.add((CartagoActionEvent)ev);
					if (ev instanceof FocusSucceededEvent){
						FocusSucceededEvent ev1 = (FocusSucceededEvent) ev;
						obsPropMap.addProperties(ev1.getTargetArtifact(),ev1.getObsProperties());
					} else if (ev instanceof StopFocusSucceededEvent){
						StopFocusSucceededEvent ev1 = (StopFocusSucceededEvent) ev;
						obsPropMap.removeProperties(ev1.getTargetArtifact());
					}
				} else if (ev instanceof ArtifactObsEvent){
					obsEventQueue.add((ArtifactObsEvent)ev);
					ArtifactObsEvent ev1 = (ArtifactObsEvent) ev;
					cartago.ArtifactObsProperty[] added = ev1.getAddedProperties();
					cartago.ArtifactObsProperty[] changed = ev1.getChangedProperties();
					cartago.ArtifactObsProperty[] removed = ev1.getRemovedProperties();
					if (added!=null){
						for (cartago.ArtifactObsProperty prop: added){
							obsPropMap.add(ev1.getArtifactId(),prop);
						}
					}
					if (changed != null){
						for (cartago.ArtifactObsProperty prop: changed){
							obsPropMap.updateProperty(ev1.getArtifactId(),prop);
						}
					}
					if (removed != null){
						for (cartago.ArtifactObsProperty prop: removed){
							obsPropMap.remove(prop);
						}
					}
				}
			} catch (Exception ex){
				ex.printStackTrace();
			}
			return false;
		}
	}

	
}