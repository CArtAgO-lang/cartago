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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import cartago.events.*;
import cartago.security.IWorkspaceSecurityManager;
import cartago.security.NullSecurityManager;
import cartago.security.SecurityException;
import cartago.security.AgentCredential;

/**
 * This class represents the core machinery of a workspace.
 * 
 * @author aricci 
 */
public class WorkspaceKernel  {

	private static final int NCONTROLLERS_DEFAULT = 20;

	private WorkspaceId   id;

	private java.util.concurrent.atomic.AtomicInteger artifactIds;
	private int ctxIds;

	private HashMap<String,AgentBody> agentBodyMap;

	private HashMap<String,ArtifactDescriptor> artifactMap;
	private HashMap<String,List<ArtifactDescriptor>> opMap;

	private HashMap<String,Manual> artManuals;

	private ArrayList<EnvironmentController> controllers;
	private int nBusyControllers;

	private ArrayBlockingQueue<OpExecutionFrame> opTodo;
	private AgentBody wspManager;
	
	private ICartagoLoggerManager logManager;
	private EventRegistry eventRegistry;

	private boolean isShutdown;

	private IWorkspaceSecurityManager securityManager;
	private final IWorkspaceSecurityManager DEFAULT_SECURITY_MANAGER = new NullSecurityManager();
	private final ICartagoLoggerManager DEFAULT_LOGGER_MANAGER = new CartagoLoggerManager();

	private LinkedList<ArtifactFactory> artifactFactories;
	private AbstractWSPRuleEngine wspRuleEngine;
	
	private AbstractWorkspaceTopology topology;
	
	/**
	 * Create a workspace.  
	 * 
	 * 
	 */
	WorkspaceKernel(WorkspaceId id, ICartagoLogger logger){		
		this.id=id;
		wspRuleEngine = null;
		isShutdown = false;
		eventRegistry = new EventRegistry();

		agentBodyMap = new HashMap<String,AgentBody>();	
		artifactMap = new HashMap<String,ArtifactDescriptor>();
		opMap = new HashMap<String,List<ArtifactDescriptor>>();
		artManuals = new HashMap<String,Manual>();
		opTodo = new ArrayBlockingQueue<OpExecutionFrame>(100);		
		artifactIds = new java.util.concurrent.atomic.AtomicInteger(0);
		
		artifactFactories = new LinkedList<ArtifactFactory>();
		artifactFactories.addFirst(new DefaultArtifactFactory());
		
		ctxIds = 0;
		wspManager = new AgentBody(new AgentId("workspace-manager", UUID.randomUUID().toString(), ctxIds++, "WorkspaceManager",id),this,null);

			
		securityManager = DEFAULT_SECURITY_MANAGER;
		logManager = DEFAULT_LOGGER_MANAGER;		

		nBusyControllers = 0;
		controllers = new ArrayList<EnvironmentController>();
		for (int i=0; i<NCONTROLLERS_DEFAULT; i++){				
			EnvironmentController controller = new EnvironmentController(this,opTodo);
			controllers.add(controller);
			controller.start();
		}

		if (logger!=null){  
			logManager.registerLogger(logger);
		}
		// creating the basic set of artifacts

		try {
			makeArtifact(wspManager.getAgentId(),"workspace", "cartago.WorkspaceArtifact", new ArtifactConfig(this));
			makeArtifact(wspManager.getAgentId(),"node", "cartago.NodeArtifact", new ArtifactConfig(this));
			makeArtifact(wspManager.getAgentId(),"manrepo","cartago.ManRepoArtifact",new ArtifactConfig(this));
			makeArtifact(wspManager.getAgentId(),"console","cartago.tools.Console",ArtifactConfig.DEFAULT_CONFIG);
			makeArtifact(wspManager.getAgentId(),"blackboard","cartago.tools.TupleSpace",ArtifactConfig.DEFAULT_CONFIG);

			/*
			String src = this.loadManualSrc("cartago/Workspace.man");
			if (src!=null){
				createManual(wspManager.getAgentId(), src);
			}
			*/
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}	


	/**
	 * Gets environment name
	 * 
	 * @return name
	 */
	public WorkspaceId getId(){
		return id;
	}

	ICartagoContext getWSPManager(){
		return wspManager;
	}
	
	public void setSecurityManager(cartago.security.IWorkspaceSecurityManager man){
		this.securityManager = man;
	}

	public IWorkspaceSecurityManager getSecurityManager(){
		return this.securityManager;
	}

	public void setLoggerManager(ICartagoLoggerManager man){
		this.logManager = man;
	}

	public ICartagoLoggerManager getLoggerManager(){
		return logManager;
	}

	public ICartagoContext joinWorkspace(AgentCredential cred, ICartagoCallback eventListener) throws CartagoException {
		String roleName = cred.getRoleName();
		if (roleName == null || roleName.equals("")){
			roleName = securityManager.getDefaultRoleName();
		}
		synchronized (agentBodyMap){
			AgentBody context = agentBodyMap.get(cred.getGlobalId());
			if (context != null){
				throw new SecurityException("Duplicate identity.");
			}
			ctxIds++;
			AgentId userId = new AgentId(cred.getId(), cred.getGlobalId(), ctxIds, roleName,id);				
			boolean joinOK = true;
			String failureMsg = "no msg";
			
			if (wspRuleEngine!=null){
				AgentJoinRequestInfo req = new AgentJoinRequestInfo(userId);
				wspRuleEngine.processAgentJoinRequest(req);
				if (req.isFailed()){
					joinOK = false;
					failureMsg = req.getFailureMsg();
				}
			} 
			if (joinOK){
				context = new AgentBody(userId, this, eventListener); 
				agentBodyMap.put(userId.getGlobalId(),context);

				/* creating the body and focusing on it */
				ArtifactId bodyId = makeAgentBodyArtifact(context);				
				List<ArtifactObsProperty> props = focus(userId, null, eventListener, bodyId);
				notifyFocusCompleted(eventListener, -1, null, null, bodyId, props);
				
				long time = System.currentTimeMillis();
				if (logManager.isLogging()){
					logManager.agentJoined(time, userId);
				}
				return context;
			} else {
				throw new CartagoException(failureMsg);
			}
		}
	}

	/*  EXPERIMENTAL: support for CArtAgO topology */

	private ArtifactId makeAgentBodyArtifact(AgentBody body){
		try {
			String name = body.getAgentId().getAgentName()+"-body";
			ArtifactId id = makeArtifact(wspManager.getAgentId(),name,"cartago.AgentBodyArtifact",new ArtifactConfig(body));
			ArtifactDescriptor des = null;
			synchronized (artifactMap){
				des = artifactMap.get(name);
				body.setBodyArtifact((AgentBodyArtifact) des.getArtifact());
			}		
			return id;
		} catch (Exception ex){
			ex.printStackTrace();
			return null;
		}
	}
	
	
	public void bindAgentBodyArtifact(AgentId agentId, AgentBodyArtifact art){
		synchronized (agentBodyMap){
			AgentBody body = agentBodyMap.get(agentId.getGlobalId());
			 if (body != null){
				 body.setBodyArtifact(art);
			 }
		}
	}
	
	public ArtifactId getAgentBodyArtifact(AgentId agentId){
		synchronized (agentBodyMap){
			AgentBody body = agentBodyMap.get(agentId.getGlobalId());
			 if (body != null){
				 return body.getAgentBodyArtifact().getId();
			 } else {
				 return null;
			 }
		}
	}

	public void notifyAgentPositionOrRadiusChange(AgentId agentId) throws CartagoException {
		if (topology != null){
			//log(">>  agent "+agentId+" position changed.");
			AgentBody body = null;
			synchronized (agentBodyMap){
				 body = agentBodyMap.get(agentId.getGlobalId());
			}
			if (body != null){
				List<ObservableArtifactInfo> newFocused = new LinkedList<ObservableArtifactInfo>();
				List<ObservableArtifactInfo> noMoreFocused = new LinkedList<ObservableArtifactInfo>();
	
				ArrayList<ObservableArtifactInfo> obsArtifactInfoList = new ArrayList<ObservableArtifactInfo>();
				AgentId userId = body.getAgentId();
				ICartagoCallback callback = body.getCallback();
				double observingRadius = body.getAgentBodyArtifact().getObservingRadius();
				AbstractWorkspacePoint agentPos = body.getAgentBodyArtifact().getPosition();
				synchronized(artifactMap){
					for (ArtifactDescriptor des: artifactMap.values()){
						Artifact art = des.getArtifact();
						//log(">> checking for observability of "+art.getId()+" for "+userId);
						AbstractWorkspacePoint arPos = art.getPosition();
						if (arPos != null){
							double distance = topology.getDistance(agentPos, arPos);
							if (distance < observingRadius && distance < art.getObservabilityRadius()){
								//log(">> "+userId+" can observe "+art.getId());
								List<ArtifactObsProperty> obs = des.getAdapter().readProperties();
								des.addObserver(userId, null, callback);
								obsArtifactInfoList.add(new ObservableArtifactInfo(des,obs));
							} else {
								/* remove the observer if present */
								des.removeObserver(userId);
							}
						}
					}
				}
				body.updateObsArtifactListWith(obsArtifactInfoList, noMoreFocused, newFocused);
				/*
				log("KERNEL: body "+body.getAgentId()+" no more observing: ");
				for (ObservableArtifactInfo info: noMoreFocused){
					log("- "+info.getTargetArtifact());
				}
				*/
				ObsArtListChangedEvent ev = eventRegistry.makeObsArtListChangedEvent(newFocused, noMoreFocused);
				callback.notifyCartagoEvent(ev);
			}
		}
	}
	
	public void notifyArtifactPositionOrRadiusChange(ArtifactId id) throws CartagoException {
		if (topology!=null){
			ArtifactDescriptor des = null;
			synchronized (artifactMap){
				des = artifactMap.get(id.getName());
			}
			if (des != null){
				AbstractWorkspacePoint artifactPos = des.getArtifact().getPosition();
				synchronized (agentBodyMap){
					double observabilityRadius = des.getAdapter().getObservabilityRadius();
					for (AgentBody ag: agentBodyMap.values()){
						AgentBodyArtifact ar = ag.getAgentBodyArtifact();
						if (ar != null){
							List<ObservableArtifactInfo> newFocused = new LinkedList<ObservableArtifactInfo>();
							List<ObservableArtifactInfo> noMoreFocused = new LinkedList<ObservableArtifactInfo>();
							double distance = topology.getDistance(ar.getPosition(), artifactPos);
							if (distance < observabilityRadius && distance < ar.getObservingRadius()){
								if (!ag.isObserving(id)){
									//System.out.println("new obs by: "+ag.getAgentId());
									ag.addFocusedArtifacts(des);
									List<ArtifactObsProperty> obs = des.getAdapter().readProperties();
									des.addObserver(ag.getAgentId(), null, ag.getCallback());
									newFocused.add(new ObservableArtifactInfo(des,obs));
								}
							} else {
								if (ag.isObserving(id)){
									ag.removeFocusedArtifacts(des);
									List<ArtifactObsProperty> obs = des.getAdapter().readProperties();
									//System.out.println("no more obs in: "+ag.getAgentId()+" obs "+obs);
									des.removeObserver(ag.getAgentId());
									noMoreFocused.add(new ObservableArtifactInfo(des,obs));
								}
							}
							if (newFocused.size() > 0 || noMoreFocused.size() > 0){
								ObsArtListChangedEvent ev = eventRegistry.makeObsArtListChangedEvent(newFocused, noMoreFocused);
								ag.getCallback().notifyCartagoEvent(ev);
							}
						}
					}
				}
			}
		}
	}


	public void setWSPTopology(AbstractWorkspaceTopology topology){
		this.topology = topology;
	}
	
	/* EXPERIMENTAL - topology - end. */
	
	public void quitAgent(AgentId userId) throws CartagoException {
		boolean quitOk = true;
		String failureMsg = "no msg";
		
		if (wspRuleEngine!=null){
			AgentQuitRequestInfo req = new AgentQuitRequestInfo(userId);
			wspRuleEngine.processAgentQuitRequest(req);
			if (req.isFailed()){
				quitOk = false;
				failureMsg = req.getFailureMsg();
			}
		}
		if (quitOk){
			synchronized (agentBodyMap){
				AgentBody body = agentBodyMap.remove(userId.getGlobalId());
				if (body==null){
					throw new CartagoException("User not in workspace.");
				}
				if (body.getAgentBodyArtifact()!=null){
					List<ArtifactObsProperty>  props = this.stopFocus(userId, body.getCallback(), body.getAgentBodyArtifact().getId());
					notifyStopFocusCompleted(body.getCallback(), -1, null, null, body.getAgentBodyArtifact().getId(),props);
					disposeArtifact(userId, body.getAgentBodyArtifact().getId());
				}
				long time = System.currentTimeMillis();
				if (logManager.isLogging()){
					logManager.agentQuit(time, userId);
				}
			}
			synchronized (artifactMap){
				for (ArtifactDescriptor des: artifactMap.values()){
					des.removeObserver(userId);
				}
			}
		} else {
			throw new CartagoException(failureMsg);
		}
	}

	//
	
	public void addArtifactFactory(ArtifactFactory factory){
		synchronized (artifactFactories){
			artifactFactories.addFirst(factory);
		}
	}

	public boolean removeArtifactFactory(String name){
		synchronized (artifactFactories){
		Iterator<ArtifactFactory> it = artifactFactories.iterator();
			while (it.hasNext()){
				ArtifactFactory cl = it.next();
				if (cl.getName().equals(name)){
					it.remove();
					return true;
				}
			}
			return false;
		}
	}

	//

	public ArtifactId makeArtifact(AgentId userId, String name, String template, ArtifactConfig config) throws ArtifactAlreadyPresentException, UnknownArtifactTemplateException, ArtifactConfigurationFailedException {
		ArtifactId id = null;
		AbstractArtifactAdapter adapter = null;
		ArtifactDescriptor des = null;
		synchronized (artifactMap){
			des = artifactMap.get(name);
			if (des!=null){
				throw new ArtifactAlreadyPresentException(name,this.id.getName());
			}
		}
		Artifact artifact = makeArtifact(template);
		try {
			int freshid = artifactIds.incrementAndGet();
			id = new ArtifactId(name, freshid,template,this.id, userId);
			//log("NEW ID CREATED: "+id+" for "+name);

			artifact.bind(id,userId,this);	
			// initArtifact(artifact,config);

			adapter = artifact.getAdapter();				
			ArtifactDescriptor desc = new ArtifactDescriptor(artifact, userId, adapter);

			synchronized (artifactMap){
				artifactMap.put(name,desc);
			}

			try {
				adapter.initArtifact(config);
				List<OpDescriptor> ops = desc.getAdapter().getOperations();
				synchronized (opMap){
					for (OpDescriptor op: ops){
						List<ArtifactDescriptor> list = opMap.get(op.getKeyId());
						if (list == null){
							list = new ArrayList<ArtifactDescriptor>();
						}	
						list.add(desc);
						opMap.put(op.getKeyId(), list);
						//log("registering operation - key: "+op+" artifact: "+desc.getArtifact().getId());
					}
				}
				
				if (logManager.isLogging()){
					logManager.artifactCreated(System.currentTimeMillis(),id,userId);
				}	

				//log(">>>> CREATE "+name+" --> "+id +" "+ desc);
				return id;

			} catch (Exception ex){
				synchronized (artifactMap){
					artifactMap.remove(name);
				}
				throw new ArtifactConfigurationFailedException(template);
			}

		} catch (Exception ex){
			//ex.printStackTrace();
			throw new ArtifactConfigurationFailedException(template);
		}
	}

	private Artifact makeArtifact(String template) throws UnknownArtifactTemplateException {
			synchronized (artifactFactories){
				for (ArtifactFactory factory: artifactFactories){
					try {
						return factory.createArtifact(template);
					} catch (Exception ex){
					}
				}
			}
			throw new UnknownArtifactTemplateException("template: "+template);
	}


	/**
	 * Destroy an artifact
	 * 
	 * @param id artifact identifier
	 * @throws CartagoException if errors occurred in disposing the artifact
	 */
	public void disposeArtifact(AgentId uid, ArtifactId id) throws CartagoException {
		ArtifactDescriptor des 	= null;
		synchronized (artifactMap){
			des = artifactMap.remove(id.getName());
			if (des==null){
				throw new ArtifactNotAvailableException();
			}
			synchronized (opMap){
				Iterator<List<ArtifactDescriptor>> it = opMap.values().iterator();
				while (it.hasNext()){
					List<ArtifactDescriptor> descList = it.next();
					Iterator<ArtifactDescriptor> it2 = descList.iterator();
					while (it2.hasNext()){
						ArtifactDescriptor desc = it2.next();
						if (desc.getArtifact().getId().equals(id)){
							it2.remove();
						}
					}
					if (descList.isEmpty()){
						it.remove();
					}
				}
			}
		}
		//log(">>>> DISPOSE "+id.getName()+" --> "+retid +" "+ desid);
		try {
			List<ArtifactObsProperty> obs = des.getAdapter().readProperties();
			FocussedArtifactDisposedEvent ev = eventRegistry.makeFocussedArtifactDisposedEvent(id,obs);
			des.notifyObservers(ev);
			if (logManager.isLogging()){
				logManager.artifactDisposed(System.currentTimeMillis(),id,uid);
			}	
			des.getArtifact().dispose();
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}

	public String[] getArtifactList(){
		synchronized (artifactMap){
			Set<Map.Entry<String,ArtifactDescriptor>> set = artifactMap.entrySet();
			List<String> list = new LinkedList<String>();
			for (Map.Entry<String,ArtifactDescriptor> e: set){
				list.add(e.getKey());
			}
			String[] ids = new String[list.size()];
			ids = list.toArray(ids);
			return ids;
		}
	}

	public ArtifactId[] getArtifactIdList(){
		synchronized (artifactMap){
			Set<Map.Entry<String,ArtifactDescriptor>> set = artifactMap.entrySet();
			List<ArtifactId> list = new LinkedList<ArtifactId>();
			for (Map.Entry<String,ArtifactDescriptor> e: set){
				list.add(e.getValue().getArtifact().getId());
			}
			ArtifactId[] ids = new ArtifactId[list.size()];
			ids = list.toArray(ids);
			return ids;
		}
	}

	public boolean isArtifactPresent(String name){
		synchronized (artifactMap){
			return artifactMap.get(name)!=null;
		}
	}

	public ArtifactId lookupArtifact(AgentId userId, String name) throws UnknownArtifactException, ArtifactNotAvailableException{
		synchronized (artifactMap){
			ArtifactDescriptor des = artifactMap.get(name);
			if (des == null){
				throw new ArtifactNotAvailableException();
			}
			return des.getArtifact().getId();
		}
	}

	public ArtifactId lookupArtifactByType(AgentId userId, String type) throws UnknownArtifactException, ArtifactNotAvailableException{
		synchronized (artifactMap){
			for (ArtifactDescriptor des: artifactMap.values()){
				if (des.getArtifactType().equals(type)){
					return des.getArtifact().getId();
				}
			}
			throw new ArtifactNotAvailableException();
		}
	}

	//
	
	public void execOp(long actionId, AgentId userId, ICartagoCallback ctx, ArtifactId aid, Op op, long timeout, IAlignmentTest test) throws CartagoException  {
		execOp(actionId, userId, ctx, aid, null, op, timeout, test);
	}

	public boolean execOp(long actionId, AgentId userId, ICartagoCallback ctx, String name, Op op, long timeout, IAlignmentTest test) throws CartagoException  {
		return execOp(actionId, userId, ctx, null, name, op, timeout, test);
	}

	public boolean execOp(long actionId, AgentId userId, ICartagoCallback ctx, Op op, long timeout, IAlignmentTest test) throws CartagoException  {
		return execOp(actionId, userId, ctx, null, null, op, timeout, test);
	}
	
	private boolean execOp(long actionId, AgentId userId, ICartagoCallback ctx, ArtifactId arId, String arName, Op op, long timeout, IAlignmentTest test) throws CartagoException  {
		if (isShutdown){
			throw new CartagoException("Workspace shutdown.");
		}
		String name = arName;
		if (arId != null){
			name = arId.getName();
		} 
		ArtifactDescriptor des = null;
		if (name != null){
			synchronized(artifactMap){
				des = artifactMap.get(name);
				if (des == null){
					try {
						ActionFailedEvent ev = eventRegistry.makeActionFailedEvent(actionId, "Artifact Not Available",new Tuple("artifact_not_available",name),op); 					
						ctx.notifyCartagoEvent(ev);
						return false;
					} catch (Exception ex){
						ex.printStackTrace();
						throw new CartagoException("exec op exception.");
					}
				}
			}
		} else {
			ArtifactId aid = null;
			synchronized(opMap){
				List<ArtifactDescriptor> list = opMap.get(Artifact.getOpKey(op.getName(), op.getParamValues().length));
				if (list == null){
					// try with var args
					String opsign = Artifact.getOpKey(op.getName(), -1);
					//log("use - try with varags: "+opsign+" -- "+op);
					list = opMap.get(opsign);
					if (list == null){
						try {
							ActionFailedEvent ev = eventRegistry.makeActionFailedEvent(actionId, "Artifact Not Available",new Tuple("artifact_not_available",aid),op); 					
							ctx.notifyCartagoEvent(ev);
							return false;
						} catch (Exception ex){
							ex.printStackTrace();
							throw new CartagoException("exec op exception.");
						}
					}
				}
				// if only one artifact has that operation, no problems...
				if (list.size() == 1){
					des = list.get(0);
				} else {
					// first we check for artifacts created by the agent
					for (ArtifactDescriptor desc: list){
						if (desc.getAgentCreator().equals(userId)){
							des = desc;
							break;
						}
					}
					if (des == null){
						// then artifacts focussed by the agent
						for (ArtifactDescriptor desc: list){
							if (desc.isObservedBy(userId)){
								des = desc;
								break;
							}
						}
					}
					if (des == null){
						des = list.get(0);
					}
				}
				if (des == null){
					try {
						ActionFailedEvent ev = eventRegistry.makeActionFailedEvent(actionId, "Artifact Not Available",new Tuple("artifact_not_available",aid),op); 					
						ctx.notifyCartagoEvent(ev);
						return false;
					} catch (Exception ex){
						ex.printStackTrace();
						throw new CartagoException("exec op exception.");
					}
				} 
			}
		}

		ArtifactId aid = des.getArtifact().getId();
		
		if (logManager.isLogging()){
			logManager.opRequested(System.currentTimeMillis(), userId, aid, op);
			//logManager.logActionUseExecuted(System.currentTimeMillis(),userId,aid,op.getName());
		}					

		if (this.wspRuleEngine == null){
			boolean allowed = securityManager.canDoAction(userId, aid , op);
			if (allowed) {	
				OpId oid = des.getAdapter().getFreshId(op.getName(),userId);
				OpExecutionFrame info = new OpExecutionFrame(this,oid,ctx, actionId, userId, aid,op,timeout,test);
				try {
					opTodo.put(info);
					return true;
				} catch (Exception ex){
					ex.printStackTrace();
					throw new CartagoException("exec op exception.");
				}
			} else {
				try {
					ActionFailedEvent ev = eventRegistry.makeActionFailedEvent(actionId, "Security exception",new Tuple("security_exception",userId,aid),op);
					ctx.notifyCartagoEvent(ev);
					return false;
				} catch (Exception ex){
					ex.printStackTrace();
					throw new CartagoException("exec op exception.");
				}
			}
		} else {
			OpRequestInfo request = new OpRequestInfo(actionId, userId, aid, op);
			wspRuleEngine.processActionRequest(request);
			if (!request.isFailed()){
				OpId oid = des.getAdapter().getFreshId(request.getOp().getName(),userId);
				OpExecutionFrame frame = new OpExecutionFrame(this,oid,ctx, actionId, userId, aid, request.getOp(), timeout, test);
				try {
					opTodo.put(frame);
					return true;
				} catch (Exception ex){
					ex.printStackTrace();
					throw new CartagoException("exec op exception.");
				}
			} else {
				try {
					ActionFailedEvent ev = eventRegistry.makeActionFailedEvent(actionId, request.getFailureMsg(),request.getFailureDesc(),op);
					ctx.notifyCartagoEvent(ev);
					return false;
				} catch (Exception ex){
					ex.printStackTrace();
					throw new CartagoException("exec op exception.");
				}
			}
		}
	}	
	
	// *****  @EXPERIMENTAL - external controller support  
	
	public void setWSPRuleEngine(AbstractWSPRuleEngine man){
		this.wspRuleEngine = man;
	}
	
	boolean wspRuleManExecOp(ArtifactId aid, Op op) {
		ArtifactDescriptor des = null;
		AgentId id = wspManager.getAgentId();
		synchronized(artifactMap){
			des = artifactMap.get(aid.getName());
			if (des == null){
				return false;
			}
		}
		OpId oid = des.getAdapter().getFreshId(op.getName(),id);
		OpExecutionFrame info = new OpExecutionFrame(this,oid, null, -2, id, aid,op,-1,null); // The value -2 in the parameter actionId points to actions triggered by AbstractWSPRuleEngine
		try {
			opTodo.put(info);
		} catch (Exception ex){
			ex.printStackTrace();
			return false;
		}		
		return true;
	}

	ArtifactObsProperty wspRuleManReadObsProperty(ArtifactId id, String propName) {
		try {
			synchronized (artifactMap){
				ArtifactDescriptor des = artifactMap.get(id.getName());
				if (des!=null) {
					IArtifactAdapter ad = des.getAdapter();		
					return ad.readProperty(propName);
				} else {
					return null;
				}
			}		
		} catch (Exception ex){
			return null;
		}
	}
	
	
	// ******* @EXPERIMENTAL end.
	
	
	// focus action

	private void logState(){
		log("DUMP -- WSP "+getId());
		for (ArtifactDescriptor des: artifactMap.values()){
			log("Artifact: "+des.getArtifact().getId());
		}
	}
	
	
	public List<ArtifactObsProperty> focus(AgentId userId, IEventFilter filter, ICartagoCallback ctx, ArtifactId aid) throws CartagoException {
		ArtifactDescriptor des = null;
		synchronized(artifactMap){
			des = artifactMap.get(aid.getName());
			if (des==null){
				//log("ART NOT FOUND "+aid.getName());
				//logState();
				throw new ArtifactNotAvailableException();
			}
		}
		try {
			List<ArtifactObsProperty> obs = des.getAdapter().readProperties();
			des.addObserver(userId, filter, ctx);
			synchronized (agentBodyMap){
				AgentBody body = agentBodyMap.get(userId.getGlobalId());
				if (body!=null){
					body.addFocusedArtifacts(des);
				}
			}
			if (logManager.isLogging()){
				logManager.artifactFocussed(System.currentTimeMillis(), userId, aid, filter);
			}
			return obs;
		} catch (Exception ex){
			ex.printStackTrace();
			throw new ArtifactNotAvailableException();
		}
	}

	
	public List<ArtifactObsProperty> stopFocus(AgentId userId, ICartagoCallback ctx, ArtifactId aid) throws CartagoException{
		ArtifactDescriptor des = null;
		synchronized(artifactMap){
			des = artifactMap.get(aid.getName());
			if (des==null){
				throw new ArtifactNotAvailableException();
			}
		}
		des.removeObserver(userId);
		synchronized (agentBodyMap){
			AgentBody body = agentBodyMap.get(userId.getGlobalId());
			if (body!=null){
				body.removeFocusedArtifacts(des);
			}
		}

		if (logManager.isLogging()){
			logManager.artifactNoMoreFocussed(System.currentTimeMillis(), userId, aid);
		}
		List<ArtifactObsProperty> obs = des.getAdapter().readProperties();
		return obs;
	}

	// 

	/*
	public void execObserveProperty(long actionId, AgentId userId, ICartagoCallback ctx, ArtifactId aid, String propName) throws CartagoException, ArtifactNotAvailableException  {
		boolean allow = securityManager.canObserve(userId, aid, propName);
		if (allow) {
			ArtifactDescriptor des = null;
			//log("***PRE-dispatchOpExec");
			synchronized(artifactMap){
				des = artifactMap.get(aid.getName());
				if (des == null){
					try {
						ObservePropFailedEvent ev = eventRegistry.makeObservePropFailedEvent(actionId, aid, "Artifact Not Available",new Tuple("artifact_not_available",aid)); 										
						ctx.notifyCartagoEvent(ev);
						return;
					} catch (Exception ex){
						ex.printStackTrace();
						throw new CartagoException("focus exception.");
					}
				}
			}
			ArtifactObsProperty prop = des.getAdapter().readProperty(propName);
			try {
				ObservePropSucceededEvent ev = eventRegistry.makeObservePropSucceededEvent(actionId, aid, prop); 					
				ctx.notifyCartagoEvent(ev);
			} catch (Exception ex){
				try {
					ObservePropFailedEvent ev = eventRegistry.makeObservePropFailedEvent(actionId, aid, "Security exception",new Tuple("security_exception",userId,aid)); 					
					ctx.notifyCartagoEvent(ev);
				} catch (Exception ex1){
					ex1.printStackTrace();
					throw new CartagoException("observe prop. exception.");
				}
			}			
		} else {
			try {
				ObservePropFailedEvent ev = eventRegistry.makeObservePropFailedEvent(actionId, aid, "Security exception",new Tuple("security_exception",userId,aid)); 					
				ctx.notifyCartagoEvent(ev);
			} catch (Exception ex1){
				ex1.printStackTrace();
				throw new CartagoException("observe prop. exception.");
			}
		}
	}
	 */

	public void linkArtifacts(AgentId userId, ArtifactId artifactOutId, String artifactOutPort, ArtifactId artifactInId) throws CartagoException  {
		ArtifactDescriptor des = null;
		synchronized(artifactMap){
			des = artifactMap.get(artifactOutId.getName());
			if (des == null){
				throw new ArtifactNotAvailableException();
			}
		}
		des.getAdapter().linkTo(artifactInId, artifactOutPort);
		if (logManager.isLogging()){
			logManager.artifactsLinked(System.currentTimeMillis(), userId, artifactOutId, artifactInId);
		}
	}

	//

	public OpId doInternalOp(ArtifactId aid, Op op) throws InterruptedException, OpRequestTimeoutException, OperationUnavailableException, ArtifactNotAvailableException, CartagoException  {
		ArtifactDescriptor des = null;
		synchronized(artifactMap){
			des = artifactMap.get(aid.getName());
		}
		if (des!=null){
			OpId oid = des.getAdapter().getFreshId(op.getName(),wspManager.getAgentId());
			OpExecutionFrame info = new OpExecutionFrame(this,oid,aid,op);
			opTodo.put(info);
			return oid;
		} else {
			throw new ArtifactNotAvailableException();
		}
	}



	public boolean hasOperation(ArtifactId aid, Op op) throws NoArtifactException {
		ArtifactDescriptor des = null; 
		synchronized (artifactMap){
			des = artifactMap.get(aid.getName());
			if (des == null){
				throw new NoArtifactException(op.getName());
			}
			return des.getAdapter().hasOperation(op);
		}
	}

	public ArtifactId getArtifact(String name) {
		ArtifactDescriptor des = null; 
		synchronized (artifactMap){
			des = artifactMap.get(name);
			if (des != null){
				return des.getArtifact().getId();
			} else {
				return null;
			}
		}
	}


	// Linked op

	public OpId execInterArtifactOp(ICartagoCallback evListener, long callbackId, AgentId userId, ArtifactId srcId, ArtifactId targetId, Op op, long timeout, IAlignmentTest test) throws CartagoException  {
		ArtifactDescriptor des = null;
		synchronized(artifactMap){
			des = artifactMap.get(targetId.getName());
			if (des == null){
				throw new ArtifactNotAvailableException();
			}
		}

		OpId oid = des.getAdapter().getFreshId(op.getName(),userId);
		OpExecutionFrame info = new OpExecutionFrame(this, oid, evListener, callbackId,  userId, targetId, op, timeout, test);
		try {
			opTodo.put(info);
			return oid;
		} catch (Exception ex){
			throw new CartagoException("execInterArtifactOp failed: "+ex);
		}
	}
	/*
	public OpId execMakeLinkedArtifact(IOpCallback srcCallback, long callbackId, AgentId userId, ArtifactId srcId, ArtifactId targetId, Op op, long timeout, IAlignmentTest test) throws CartagoException  {
		this.execMakeArtifact(ctx, name, template, config, address)
	}*/

	// MANUALS management

	/*
	public Manual getArtifactManual(AgentId userId, ArtifactId aid) throws ArtifactNotAvailableException, CartagoException {
		ArtifactDescriptor des = null;
		synchronized(artifactMap){
			des = artifactMap.get(aid);
		}
		if (des!=null){
			return des.getAdapter().getManual();
		} else {
			throw new ArtifactNotAvailableException();
		}
	}*/

	public Manual getManual(AgentId userId, String manualName) throws ManualNotAvailableException {		
		synchronized (artManuals){
			Manual man = artManuals.get(manualName);
			if (man == null){
				throw new ManualNotAvailableException(manualName);
			} else {
				return man;
			}
		}
	}

	/*
	public Manual getArtifactManual(AgentId userId, String artType) throws CartagoException {
		try {
			synchronized (artManuals){
				// currently the artifact type is used as manual name 
				Manual man = artManuals.get(artType);
				if (man == null){
					String src = loadManualSrc(Artifact.getManualSrcFile(artType));
					if (src!=null){
						man = Manual.parse(src);
					} else {
						man = Manual.EMPTY_MANUAL;
					}
					return man;
				} else {
					return man;
				}
			}
		} catch (Exception ex){
			throw new InvalidManualException(artType);
		}
	}*/

	public Manual registerManual(String artifactType, String uri, String src) throws Exception {
		if (src!=null){
			synchronized (artManuals){
				Manual man = artManuals.get(artifactType);
				if (man != null){
					return man;
				} else {
					try {
						// man = Manual.parse(uri,src);
						man = new Manual(artifactType, new java.net.URI(uri));
						man.setSource(src);
						artManuals.put(artifactType, man);
						return man;
					} catch (Exception ex){
						ex.printStackTrace();
						throw new InvalidManualException(artifactType);
					}
				}
			}
		} else {
			return Manual.EMPTY_MANUAL;
		}
	}

	public boolean removeManual(String name)  {
		synchronized (artManuals){
			Manual man = artManuals.remove(name);
			return man != null;
		}
	}

	// artifact side	

	private void serveOperation(OpExecutionFrame info) {
		ArtifactId aid = info.getTargetArtifactId();
		IArtifactAdapter adapter = null;
		ArtifactDescriptor des;
		//log("here1 "+info);
		synchronized(artifactMap){
			//log("here2 "+info);
			des = artifactMap.get(aid.getName());
			adapter = des.getAdapter();
			nBusyControllers++;
			if (nBusyControllers>=controllers.size()){
				this.addControllers(10);
			}
		}
		//log("here3"+info);
		try {
			adapter.doOperation(info);
		} catch (Exception ex){
			ex.printStackTrace();
		}
		synchronized(artifactMap){
			nBusyControllers--;
		}
	}

	/**
	 * Extends the set of controllers used to serve operation execution
	 * 
	 * @param n number of controllers to be added
	 */
	public void addControllers(int n){
		for (int i=0; i<n; i++){				
			EnvironmentController controller = new EnvironmentController(this,opTodo);
			controllers.add(controller);
			controller.start();
		}
	}

	/**
	 * Shutdown the workspace.
	 * 
	 * This includes disposal of artifacts.
	 * 
	 */
	public void shutdown(){
		isShutdown = true;
		synchronized(artifactMap){
			//log("here2 "+info);
			final Collection<ArtifactDescriptor> list = artifactMap.values();
			final CountDownLatch latch = new CountDownLatch(1);
			new Thread(){
				public void run(){
					for (ArtifactDescriptor des: list){
						des.getArtifact().dispose();
					}
					latch.countDown();
				}
			}.start();
			try {
				latch.await(20, TimeUnit.SECONDS);
			} catch (InterruptedException ex){
				log("Disposal of artifacts not completed in 20 seconds.");
			}
		}
		for (EnvironmentController c: controllers){
			c.stopActivity();
		}
	}

	//

	public void notifyObsEventToAgent(ArtifactId sourceId, AgentId target, Tuple signal, ArtifactObsProperty[] changed, ArtifactObsProperty[] added, ArtifactObsProperty[] removed){
		try {
			if (logManager.isLogging()){
				logManager.newPercept(System.currentTimeMillis(), sourceId, signal, added, removed, changed);
			}
		} catch (Exception ex){}
		ArtifactObsEvent ev = eventRegistry.makeObsEvent(sourceId, signal, changed, added, removed);
		ArtifactDescriptor des = null;		
		synchronized(artifactMap){
			des = artifactMap.get(sourceId.getName());
		}
		if (des!=null){
			des.notifyObserver(target,ev);
		}
	}


	public void notifyObsEvent(ArtifactId sourceId, Tuple signal, ArtifactObsProperty[] changed, ArtifactObsProperty[] added, ArtifactObsProperty[] removed){
		try {
			if (logManager.isLogging()){
				logManager.newPercept(System.currentTimeMillis(), sourceId, signal, added, removed, changed);
			}
		} catch (Exception ex){}
		ArtifactObsEvent ev = eventRegistry.makeObsEvent(sourceId, signal, changed, added, removed);
		ArtifactDescriptor des = null;		
		synchronized(artifactMap){
			des = artifactMap.get(sourceId.getName());
		}
		if (des!=null){
			des.notifyObservers(ev);
			if (wspRuleEngine!=null){ //informing the changes in observable properties to the AbstractWSPRuleEngine
				if(changed != null){
					wspRuleEngine.processObsPropertyChanged(sourceId, changed);
				}			
				if(added != null){
					wspRuleEngine.processObsPropertyAdded(sourceId, added);
				}
				if(removed != null){
					wspRuleEngine.processObsPropertyRemoved(sourceId, removed);
				}
			}
		}

		if(signal!=null){ //informing the signal the AbstractWSPRuleEngine
			if (wspRuleEngine!=null){ 
				wspRuleEngine.processSignal(signal);
			}
		}
	}

	
	public void notifyActionCompleted(ICartagoCallback listener, long actionId, ArtifactId aid, Op op) {
		ActionSucceededEvent ev = eventRegistry.makeActionSucceededEvent(actionId, aid, op);
		listener.notifyCartagoEvent(ev);
	}

	
	public void notifyActionCompleted(ICartagoCallback listener, long actionId, ArtifactId aid, Op op, AgentId userId) {			
		ActionSucceededEvent ev = eventRegistry.makeActionSucceededEvent(actionId, aid, op);
		if(wspRuleEngine!=null){ //informing the action completed to the AbstractWSPRuleEngine.
			wspRuleEngine.processActionCompleted(ev, userId);		   
		}
		listener.notifyCartagoEvent(ev);						
	}

	public void notifyActionFailed(ICartagoCallback listener, long actionId, Op op, String failureMsg, Tuple failureReason) {
		ActionFailedEvent ev = eventRegistry.makeActionFailedEvent(actionId, failureMsg, failureReason, op);
		listener.notifyCartagoEvent(ev);
	}

	public void notifyFocusCompleted(ICartagoCallback listener, long actionId, ArtifactId aid, Op op, ArtifactId target, List<ArtifactObsProperty> props) {
		ActionSucceededEvent ev = eventRegistry.makeFocusActionSucceededEvent(actionId, aid, op, target, props);
		listener.notifyCartagoEvent(ev);
	}


	public void notifyStopFocusCompleted(ICartagoCallback listener, long actionId, ArtifactId aid, Op op, ArtifactId target, List<ArtifactObsProperty> props) {
		ActionSucceededEvent ev = eventRegistry.makeStopFocusActionSucceededEvent(actionId, aid, op, target, props);
		listener.notifyCartagoEvent(ev);
	}

	public void notifyJoinWSPCompleted(ICartagoCallback listener, long actionId, ArtifactId aid, Op op, WorkspaceId wspId, ICartagoContext ctx) {
		ActionSucceededEvent ev = eventRegistry.makeJoinWSPSucceededEvent(actionId, aid, op, wspId, ctx);
		listener.notifyCartagoEvent(ev);
	}

	public void notifyQuitWSPCompleted(ICartagoCallback listener, long actionId, ArtifactId aid, Op op, WorkspaceId wspId) {
		ActionSucceededEvent ev = eventRegistry.makeQuitWSPSucceededEvent(actionId, aid, op, wspId);
		listener.notifyCartagoEvent(ev);
	}

	public void notifyConsultManualCompleted(ICartagoCallback listener, long actionId, ArtifactId aid, Op op, Manual man) {
		ActionSucceededEvent ev = eventRegistry.makeConsultManualSucceededEvent(actionId, aid, op, man);
		listener.notifyCartagoEvent(ev);
	}


	//

	protected void log(String st){
		System.out.println("[ENV] "+st);
	}


	HashMap<String,AgentBody> getCurrentAgentContexts(){
		return this.agentBodyMap;
	}

	public void removeGarbageBody(AgentBody ctx){
		try {
			quitAgent(ctx.getAgentId());
		} catch (Exception ex){
			ex.printStackTrace();
			log("CONTEXT TO REMOVE NOT FOUND: "+id);
		}
	}

	public ICartagoController getController(){
		return new CartagoController(this);
	}

	// interface for CartagoControllers

	private ArtifactId[] getCurrentArtifacts(){
		synchronized(artifactMap){
			java.util.Collection<ArtifactDescriptor> set = artifactMap.values();
			ArtifactId[] ids = new ArtifactId[set.size()];
			int index = 0;
			for (ArtifactDescriptor des: set){
				ids[index++] = des.getArtifact().getId();
			}
			return ids;
		}
	}

	
	private AgentId[] getCurrentAgents(){
		synchronized(agentBodyMap){
			java.util.Collection<AgentBody> set = agentBodyMap.values();
			AgentId[] ids = new AgentId[set.size()];
			int index = 0;
			for (AgentBody b: set){
				ids[index++] = b.getAgentId();
			}
			return ids;
		}
	}

	private boolean removeArtifact(String artifactName){
		synchronized (artifactMap){
			ArtifactDescriptor des = artifactMap.remove(artifactName);
			if (des!=null) {
				try {
					Class c = des.getArtifact().getClass();
					Method[] ms = c.getDeclaredMethods();
					for (Method m: ms){
						if (m.getName().equals("dispose")){
							m.setAccessible(true);
							m.invoke(des.getArtifact(), new Object[]{});
							break;
						}
					}				
					if (logManager.isLogging()){
						logManager.artifactDisposed(System.currentTimeMillis(),des.getArtifact().getId(),null);
					}	
				} catch (Exception ex){
					//ex.printStackTrace();
				}
				return true;
			} else {
				return false;
			}
		}
	}

	
	private boolean removeAgent(String globalId){
		synchronized (agentBodyMap){
			AgentBody body = agentBodyMap.remove(globalId);
			if (body != null){
				if (body.getAgentBodyArtifact()!=null){
					try {
						disposeArtifact(this.wspManager.getAgentId(), body.getAgentBodyArtifact().getId());
					} catch (Exception ex){}
				}
				synchronized (artifactMap){
					for (ArtifactDescriptor des: artifactMap.values()){
						des.removeObserver(body.getAgentId());
					}
				}
				return true;
			} else {
				return false;
			}
		}
	}

	private ArtifactInfo getArtifactInfo(String artifactName) throws CartagoException  {
		synchronized (artifactMap){
			ArtifactDescriptor des = artifactMap.get(artifactName);
			IArtifactAdapter ad = des.getAdapter();
			
			if (des!=null) {
				ArtifactInfo info = new ArtifactInfo( 
						des.getAgentCreator(),
						des.getArtifact().getId(),
						ad.getOperations(),
						ad.readProperties(),
						ad.getOpInExecution(),
						ad.getManual());
				
				return info;
			} else {
				throw new CartagoException();
			}
		}
	}
	
	public String loadManualSrc(String fname) {
		try {
			InputStream is = null;
			is = this.getClass().getClassLoader().getResourceAsStream(fname);
			try {
				if (is == null){
					is = new FileInputStream(new File(fname));
				}
			} catch (Exception ex){
				log("current path: "+new File(".").getAbsolutePath());
				log("locating the manual "+fname+" in local 'manuals' folder");
				String[] sts = fname.split("/");
				is = new FileInputStream(new File("manuals"+File.separator+sts[sts.length-1]));
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			StringBuffer src = new StringBuffer();
			String line = br.readLine();
			while (line!=null){
				src.append(line);
				line = br.readLine();						
			}				
			return src.toString();
		} catch (Exception ex){
			ex.printStackTrace();
			System.err.println("Manual source not found: "+fname);
			return null;

		}
	}



	/**
	 * Environment Controller to serve operation execution
	 * requested by agents
	 * 
	 * @author aricci
	 *
	 */
	class EnvironmentController extends Thread {

		private ArrayBlockingQueue<OpExecutionFrame> opBuffer;
		private WorkspaceKernel env;
		private boolean stopped;
		private int nfailures;

		public EnvironmentController(WorkspaceKernel env, ArrayBlockingQueue<OpExecutionFrame> opBuffer){
			this.env = env;
			this.opBuffer = opBuffer;
			nfailures = 0;
		}

		public synchronized void stopActivity(){
			stopped = true;
			this.interrupt();
		}

		public synchronized boolean isStopped(){
			return stopped;
		}

		public void run(){
			stopped = false;
			while (!isStopped()){
				try {
					OpExecutionFrame item = opBuffer.take();		
					//log("New job to do: "+item.getOpId());
					item.setServingThread(Thread.currentThread());
					env.serveOperation(item);
					//nfailures = 0;
				} catch (Exception ex){
					//ex.printStackTrace();
					//env.log("[ENV-CONTROLLER] uncaught operation exception: "+ex);
					/*
					 nfailures++;
					if (nfailures>10){
						break;
					}*/
				}
			}

			// stop requested, consuming existing items in the buffer 

			while (!opBuffer.isEmpty()){
				try {
					OpExecutionFrame item = opBuffer.poll();		
					if (item!=null){
						item.setServingThread(Thread.currentThread());
						env.serveOperation(item);
					}
				} catch (Exception ex){
				}
			}

			// env.log("[ENV-CONTROLLER] shutdown");

		}
	}

	class CartagoController implements ICartagoController {

		private WorkspaceKernel env;

		public CartagoController(WorkspaceKernel env){
			this.env = env;
		}

		public ArtifactId[] getCurrentArtifacts() throws CartagoException {
			return env.getCurrentArtifacts();
		}

		public AgentId[] getCurrentAgents() throws CartagoException {
			return env.getCurrentAgents();
		}

		public boolean removeArtifact(String artifactName) throws CartagoException {
			return env.removeArtifact(artifactName);
		}

		public boolean removeAgent(String globalId) throws CartagoException {
			return env.removeAgent(globalId);
		}

		public ArtifactInfo getArtifactInfo(String artifactName) throws CartagoException {
			return env.getArtifactInfo(artifactName);
		}

	}

}

