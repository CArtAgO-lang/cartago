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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cartago.infrastructure.CartagoInfrastructureLayerException;
import cartago.infrastructure.ICartagoInfrastructureLayer;
import cartago.tools.inspector.Inspector;


/**
 * Entry point for working with CArtAgO Environment
 *  
 * @author aricci
 *
 */
public class CartagoEnvironment {

	public static String MAIN_WSP_NAME = "main";

	private static CartagoEnvironment instance;

	/* set of available infrastructure layers */
	private	Map<String,ICartagoInfrastructureLayer> infraLayers = new HashMap<String,ICartagoInfrastructureLayer>();
	private String defaultInfraLayer = "rmi";
	private HashMap<String,Inspector> debuggers = new HashMap<String,Inspector>();
	
	/* set of information about linked nodes - for linkability with remote artifacts */
	private List<LinkedNodeInfo> linkedNodes = new LinkedList<LinkedNodeInfo>();

	/* root workspace */
	private WorkspaceDescriptor rootWsp;
	
	/* wsp name */
	private HashMap<String, WorkspaceDescriptor> wspMap;
	
	/* singleton design */
	
	public static synchronized CartagoEnvironment getInstance() {
		if (instance == null) {
			instance = new CartagoEnvironment();
		}
		return instance;
	}
	
	
	private CartagoEnvironment() {
		wspMap = new HashMap<String, WorkspaceDescriptor>();
	}

	/**
	 * Init the Environment.
	 * 
	 * @throws CartagoException
	 */
	public void init() throws CartagoException {
		this.init(null);
	}

	/**
	 * Init the environment.
	 * 
	 * @param logger
	 * @throws CartagoException
	 */
	public void init(ICartagoLogger logger) throws CartagoException {
		if (rootWsp == null) {
			WorkspaceId wid = new WorkspaceId(MAIN_WSP_NAME); 
			rootWsp = new WorkspaceDescriptor(wid);
			Workspace wsp = new Workspace(wid, rootWsp, logger);
			rootWsp.setWorkspace(wsp);
			this.registerWSP("/"+MAIN_WSP_NAME, rootWsp);
		}
	}
	
	/**
	 * Get version.
	 * 
	 * @return
	 */
	public String getVersion() {
		return CARTAGO_VERSION.getID();
	}
	

	public WorkspaceDescriptor getRootWSP() {
		return rootWsp;
	}
	
	/**
	 * Shutdown the CArtAgO node.
	 * 
	 * @throws CartagoException
	 */
	public synchronized void shutdown() throws CartagoException {
		for (ICartagoInfrastructureLayer s:infraLayers.values()){
			try {
				s.shutdownLayer();
				if (s.isServiceRunning()){
					s.shutdownService();
				}
			} catch (Exception ex){
				ex.printStackTrace();
			}
		}
		infraLayers.clear();
	}
	
	/* wsp management */
	
	/**
	 * 
	 * Register a workspace.
	 * 
	 * @param logicalPath
	 * @param des
	 */
	public synchronized void registerWSP(String logicalPath, WorkspaceDescriptor des) {
		wspMap.put(logicalPath, des);
	}
	
	/**
	 * 
	 * Lookup for a workspace.
	 * 
	 * @param logicalPath
	 * @return
	 * @throws WorkspaceNotFoundException
	 */
	public synchronized WorkspaceDescriptor resolveWSP(String logicalPath) throws WorkspaceNotFoundException {
		WorkspaceDescriptor des = this.wspMap.get(logicalPath);
		if (des != null) {
			return des;
		} else {
			throw new WorkspaceNotFoundException();
		}
	}
	
	// agent session 
	
	/**
	 * Start a CArtAgO session in a local workspace.
	 * 
	 * @param wspName workspace to join
	 * @param cred agent credential
	 * @param eventListener listener to perceive workspace events
	 * @return
	 * @throws CartagoException
	 */
	public synchronized ICartagoSession startSession(String wspName, AgentCredential cred, ICartagoListener eventListener) throws CartagoException {
		Workspace wsp = this.resolveWSP("/"+wspName).getWorkspace();
		CartagoSession session = new CartagoSession(cred,null,eventListener);
		ICartagoContext startContext = wsp.joinWorkspace(cred, null, session);
		WorkspaceId wspId = startContext.getWorkspaceId();
		session.setInitialWorkspace(wspId, startContext);
		return session;
	}

	/**
	 * Start a CArtAgO session in a local workspace.
	 * 
	 * @param wspName workspace to join
	 * @param cred agent credential
	 * @param eventListener listener to perceive workspace events
	 * @return
	 * @throws CartagoException
	 */
	public synchronized ICartagoSession startSession(AgentCredential cred, ICartagoListener eventListener) throws CartagoException {
		return this.startSession(CartagoEnvironment.MAIN_WSP_NAME, cred, eventListener);
	}
	

	/**
	 * Start a CArtAgO session in a local workspace, returning a context
	 * 
	 * @param wspName workspace to join
	 * @param cred agent credential
	 * @param eventListener listener to perceive workspace events
	 * @return
	 * @throws CartagoException
	 */
	public synchronized ICartagoSession startSession(String wspName, AgentCredential cred) throws CartagoException {
		return this.startSession(wspName, cred, null);
	}

	/**
	 * Start a CArtAgO session in the main workspace, returning a context
	 * 
	 * @param wspName workspace to join
	 * @param cred agent credential
	 * @param eventListener listener to perceive workspace events
	 * @return
	 * @throws CartagoException
	 */
	public synchronized ICartagoSession startSession(AgentCredential cred) throws CartagoException {
		return startSession(CartagoEnvironment.MAIN_WSP_NAME,cred);
	}
	
	/**
	 * Start a working session in a remote workspace.
	 * 
	 * @param wspName workspace name
	 * @param wspAddress workspace address 
	 * @param protocol infrastructure protocol ("default" for default one)
	 * @param cred agent  credential
	 * @param eventListener listener to workspace events to be perceived by the agent
	 * @return a context for working inside the workspace
	 */
	public synchronized ICartagoSession startRemoteSession(String wspName, String wspAddress, String protocol, AgentCredential cred, ICartagoListener eventListener) throws CartagoException {
		if (wspName==null){
			wspName = "default";
		}
		
		if ((protocol == null) || (protocol.equals("default"))) protocol = defaultInfraLayer;

		//If the infrastructure protocol is not installed yet (agent joining a remote workspace
		//without installing a CArtAgO node ex. CArtAgO-WS agents..) we install it
		if (!infraLayers.containsKey(protocol)) {
			installInfrastructureLayer(protocol);
		}
		
		CartagoSession session = new CartagoSession(cred,null,eventListener);
		ICartagoContext startContext = joinRemoteWorkspace(wspName, wspAddress, protocol, cred, session);
		WorkspaceId wspId = startContext.getWorkspaceId();
		session.setInitialWorkspace(wspId, startContext);
		return session;
	}

	/**
	 * Start a working session in a remote workspace, returning a context
	 * 
	 * @param wspName workspace name
	 * @param wspAddress workspace address 
	 * @param protocol infrastructure protocol ("default" for default one)
	 * @param cred agent  credential
	 * @param eventListener listener to workspace events to be perceived by the agent
	 * @return a context for working inside the workspace
	 */
	public synchronized CartagoContext startRemoteSession(String wspName, String wspAddress, String protocol, AgentCredential cred) throws CartagoException {
		if (wspName==null){
			wspName = "default";
		}
		
		if ((protocol == null) || (protocol.equals("default"))) {
			protocol = defaultInfraLayer;
		}

		//If the infrastructure protocol is not installed yet (agent joining a remote workspace
		//without installing a CArtAgO node ex. CArtAgO-WS agents..) we install it
		if(!infraLayers.containsKey(protocol)) { 
			installInfrastructureLayer(protocol);
		}
		CartagoContext context = new CartagoContext(cred);
		ICartagoContext startContext = joinRemoteWorkspace(wspName, wspAddress, protocol, cred, context.getCartagoSession());
		WorkspaceId wspId = startContext.getWorkspaceId();
		context.getCartagoSession().setInitialWorkspace(wspId, startContext);
		return context;
	}
	

	
	//
	
	/**
	 * Join a remote workspace - called by CArtAgO node.
	 * 
	 * @param address IP address of the node
	 * @param wspName workspace name
	 * @param address address of the workspace
	 * @param protocol infrastructure protocol used to contact the node ("default" for default one)
	 * @param cred agent credentials - (es: cartago.security.UserIdCredential(String agentName))
	 * @param eventListener listener for events to be perceived by the agent 
	 * @return a context to act inside the workspace
	 * @throws cartago.security.SecurityException
	 * @throws CartagoException
	 */
	synchronized ICartagoContext joinRemoteWorkspace(String wspName, String address, String protocol, AgentCredential cred, ICartagoCallback eventListener) throws cartago.security.SecurityException, CartagoException{
		try {
			if ((protocol == null) || (protocol.equals("default"))){
				protocol = defaultInfraLayer;
			} 
			ICartagoInfrastructureLayer service = infraLayers.get(protocol);
			ICartagoContext ctx = service.joinRemoteWorkspace(wspName, address, cred, eventListener);
			/*
			LinkedNodeInfo nodeInfo = new LinkedNodeInfo(ctx.getWorkspaceId().getNodeId(), protocol, address);
			boolean exists = false;
			for(LinkedNodeInfo tempNodeInfo : linkedNodes) {
				if(tempNodeInfo.equals(nodeInfo)) {
					exists = true;
					break;
				}
			}
			if(!exists) {
				linkedNodes.add(nodeInfo);
				// System.out.println("[CartagoService] ADDED NODE INFO: "+nodeInfo.getAddress());
			}
			*/
			return ctx;
		} catch (CartagoInfrastructureLayerException ex) {
			ex.printStackTrace();
			throw new CartagoException("Join "+wspName+"@"+address+" failed ");
		}
	}
	
	synchronized ICartagoContext joinRemoteWorkspace(WorkspaceDescriptor wdes, AgentCredential cred, ICartagoCallback eventListener) throws cartago.security.SecurityException, CartagoException{
		String wspName = wdes.getId().getFullName();
		String protocol = wdes.getProtocol();
		String address = wdes.getAddress();
		try {
			if ((protocol == null) || (protocol.equals("default"))){
				protocol = defaultInfraLayer;
			} 
			ICartagoInfrastructureLayer service = infraLayers.get(protocol);
			ICartagoContext ctx = service.joinRemoteWorkspace(wspName, address, cred, eventListener);
			/*
			LinkedNodeInfo nodeInfo = new LinkedNodeInfo(ctx.getWorkspaceId()., protocol, address);
			boolean exists = false;
			for(LinkedNodeInfo tempNodeInfo : linkedNodes) {
				if(tempNodeInfo.equals(nodeInfo)) {
					exists = true;
					break;
				}
			}
			if(!exists) {
				linkedNodes.add(nodeInfo);
				// System.out.println("[CartagoService] ADDED NODE INFO: "+nodeInfo.getAddress());
			}
			*/
			return ctx;
		} catch (CartagoInfrastructureLayerException ex) {
			ex.printStackTrace();
			throw new CartagoException("Join "+wspName+"@"+address+" failed ");
		}
	}	

	synchronized WorkspaceDescriptor createRemoteWorkspace(String wspName, WorkspaceDescriptor parent) throws cartago.security.SecurityException, CartagoException{
		/*
		String wspName = wdes.getId().getFullName();
		String protocol = wdes.getProtocol();
		String address = wdes.getAddress();
		try {
			if ((protocol == null) || (protocol.equals("default"))){
				protocol = defaultInfraLayer;
			} 
			ICartagoInfrastructureLayer service = infraLayers.get(protocol);
			ICartagoContext ctx = service.joinRemoteWorkspace(wspName, address, cred, eventListener);
			LinkedNodeInfo nodeInfo = new LinkedNodeInfo(ctx.getWorkspaceId()., protocol, address);
			boolean exists = false;
			for(LinkedNodeInfo tempNodeInfo : linkedNodes) {
				if(tempNodeInfo.equals(nodeInfo)) {
					exists = true;
					break;
				}
			}
			if(!exists) {
				linkedNodes.add(nodeInfo);
				// System.out.println("[CartagoService] ADDED NODE INFO: "+nodeInfo.getAddress());
			}
			return ctx;
		} catch (CartagoInfrastructureLayerException ex) {
			ex.printStackTrace();
			throw new CartagoException("Join "+wspName+"@"+address+" failed ");
		}*/
		throw new IllegalArgumentException("Not implemented");
	}	
	
	
	
	
	/**
	 * Exec a linked operation - called by artifacts
	 * 
	 * @param callback
	 * @param callbackId
	 * @param userId
	 * @param srcId
	 * @param targetId
	 * @param op
	 * @param timeout
	 * @param test
	 * @return
	 * @throws RemoteException
	 * @throws CartagoException
	 */
	OpId execRemoteInterArtifactOp(ICartagoCallback callback, long callbackId, AgentId userId, ArtifactId srcId, ArtifactId targetId, Op op, long timeout, IAlignmentTest test) throws CartagoInfrastructureLayerException, CartagoException {
		/*
		try {
			for (LinkedNodeInfo info: linkedNodes){
				if (targetId.getWorkspaceId().getNodeId().equals(info.getNodeId())){
					String support = info.getProtocol();
					if (support==null || support.equals("default")){
						support = defaultInfraLayer;
					}
					ICartagoInfrastructureLayer service = infraLayers.get(support);
					return service.execRemoteInterArtifactOp(callback, callbackId, userId, srcId, targetId, info.getAddress(), op, timeout, test);
				}
			}
			throw new CartagoException("Inter-artifact op failed: target node not linked");
		} catch (Exception ex){
			ex.printStackTrace();
			throw new CartagoException("Inter-artifact op failed: "+ex.getLocalizedMessage());
		}
		*/
		throw new IllegalArgumentException("not implemented.");
	}
	
	
	/**
	 * Create a workspace inside the node.
	 * 
	 * @param name workspace name
	 * @return
	 *//*
	public synchronized CartagoWorkspace createWorkspace(String name, AbstractWorkspaceTopology topology) throws CartagoException {
		CartagoWorkspace wsp = wsps.get(name);		
		if (wsp==null){
			WorkspaceId wid = new WorkspaceId(name,nodeId); 
			wsp = new CartagoWorkspace(wid,this);
			wsp.setTopology(topology);
			wsps.put(name, wsp);
			return wsp;
		} else {
			throw new CartagoException("workspace already created");
		}
	}*/	
	
	/* factory management */
	
	/**
	 * Add an artifact factory for artifact templates
	 * 
	 * @param wspName workspace name
	 * @param factory artifact factory
	 * @throws CartagoException
	 */
	public synchronized void addArtifactFactory(String wspName, ArtifactFactory factory) throws CartagoException {
		this.resolveWSP(wspName).getWorkspace().addArtifactFactory(factory);
	}
	
	/**
	 * Remove an existing class loader for artifacts
	 * @param wspName workspace name
	 * @param name id of the artifact factory
	 * @throws CartagoException
	 */
	public synchronized void removeArtifactFactory(String wspName, String name) throws CartagoException {
		this.resolveWSP(wspName).getWorkspace().removeArtifactFactory(name);
	}

	/**
	 * Register a new logger for CArtAgO Workspace Kernel events
	 * 
	 * @param wspName
	 * @param logger
	 * @throws CartagoException
	 */
	public synchronized void registerLogger(String wspName, ICartagoLogger logger) throws CartagoException {
		this.resolveWSP(wspName).getWorkspace().registerLogger(logger);
	}

	/* debugging */
	
	/**
	 * Enable debugging of a CArtAgO Workspace 
	 * 
	 * @param wspName
	 * @throws CartagoException
	 */
	public synchronized void enableDebug(String wspName) throws CartagoException {
		Inspector insp = debuggers.get(wspName);
		 if (insp == null){
			insp = new Inspector();
			insp.start();
			registerLogger(wspName, insp.getLogger());
			debuggers.put(wspName, insp);
		}
	}
	
	/**
	 * Disable debugging of a CArtAgO Workspace 
	 * 
	 * @param wspName
	 * @throws CartagoException
	 */
	public synchronized void disableDebug(String wspName) throws CartagoException {
			 Inspector insp = debuggers.remove(wspName);
			 if (insp != null){
				 Workspace wsp = this.resolveWSP(wspName).getWorkspace();
				 wsp.unregisterLogger(insp.getLogger());
			 }
	}
	
	
	/* loggers */
	
	/**
	 * Register a new logger for a remote CArtAgO Workspace 
	 * 
	 * @param wspName
	 * @param logger
	 * @throws CartagoException
	 */
	public synchronized void registerLoggerToRemoteWsp(String wspName, String address, String protocol, ICartagoLogger logger) throws CartagoException {
		try {
			if ((protocol == null) || (protocol.equals("default"))){
				protocol = defaultInfraLayer;
			}
			ICartagoInfrastructureLayer service = infraLayers.get(protocol);
			service.registerLoggerToRemoteWsp(wspName, address, logger);
		} catch (CartagoInfrastructureLayerException ex) {
			ex.printStackTrace();
			throw new CartagoException("Registering logger at "+wspName+"@"+address+" failed ");
		}
	}
	
	
	/**
	 * 
	 * Unregister a logger 
	 * 
	 * @param wspName
	 * @param logger
	 * @throws CartagoException
	 */
	public synchronized void unregisterLogger(String wspName, ICartagoLogger logger) throws CartagoException {
		this.resolveWSP(wspName).getWorkspace().unregisterLogger(logger);
	}	
	
	/**
	 * Enable linking to the specified node
	 * 
	 * @param id node id
	 * @param support
	 * @param address
	 */
	public synchronized void enableLinkingWithNode(NodeId id, String support, String address){
		linkedNodes.add(new LinkedNodeInfo(id,support,address));
	}

	/**
	 * Getting a controller.
	 * 
	 * @param wspName
	 * @return
	 * @throws CartagoException
	 */
	public synchronized ICartagoController  getController(String wspName) throws CartagoException {
		return this.resolveWSP(wspName).getWorkspace().getController();
	}
	
	public boolean isInfrastructureLayerInstalled(String protocol){
		if (protocol.equals("default")) {
			protocol = defaultInfraLayer;
			return true;
		} else {
			return infraLayers.get(protocol)!=null;
		}
	}
	
	
	// service management

	/**
	 * Install a CArtAgO infrastructure layer, to enable interaction with remote nodes.
	 * 
	 * @param type name of the layer, which typically corresponds to the protocol adopted. Use "default" to specify default type. 
	 * @throws CartagoException if the installation fails
	 */
	public synchronized void installInfrastructureLayer(String type) throws CartagoException {
		if (type.equals("default")){
			type = defaultInfraLayer;
		}
		ICartagoInfrastructureLayer layer = infraLayers.get(type);
		if (layer == null){
			try {
				Class<ICartagoInfrastructureLayer> serviceClass = (Class<ICartagoInfrastructureLayer>) Class.forName("cartago.infrastructure."+type+".CartagoInfrastructureLayer");
				layer = serviceClass.newInstance();
				infraLayers.put(type, layer);
			} catch (Exception ex){
				ex.printStackTrace();
				throw new CartagoException("Invalid infrastructure layer: "+type);
			}
		} else {
			throw new CartagoException("Infrastructure layer "+type+"already installed");
		}
	}	

	/* infrastructure management */
	
	/**
	 * Change the default infrastructure layer name
	 * 
	 * @param name infrastructure layer name
	 */
	public void setDefaultInfrastructureLayer(String name){
		defaultInfraLayer = name;
	}

	
	/**
	 * Start a CArtAgO infrastructure service, to allow remote agents to work on this node using the specified protocol.
	 * 
	 * Before starting the service, the corresponding infrastructure layer should have been already installed.
	 * 
	 * @param type the type of service to start. Use "default" to specify default type.
	 * @param address address of the service. 
	 * @throws CartagoException if the start fails
	 */
	public synchronized void startInfrastructureService(String type, String address) throws CartagoException {
		if (type.equals("default")){
			type = defaultInfraLayer;
		}
		ICartagoInfrastructureLayer layer = infraLayers.get(type);
		if (layer != null){
			try {
				layer.startService(address);
			} catch (Exception ex){
				throw new CartagoException("Infrastructure layer service failure: "+type);
			}
		} else {
			throw new CartagoException("Infrastructure layer "+type+"not installed");
		}
	}	

	/**
	 * Start a CArtAgO infrastructure service, to allow remote agents to work on this node using the specified protocol.
	 * 
	 * Before starting the service, the corresponding infrastructure layer should have been already installed.
	 * 
	 * @param type the type of service to start. Use "default" to specify default type.
	 * @param address address of the service. 
	 * @throws CartagoException if the start fails
	 */
	public synchronized void startInfrastructureService(String type) throws CartagoException {
		startInfrastructureService(type,"");
	}
	
	
	
	/**
	 * Install CArtAgO Node with RMI infrastructure service.
	 * 
	 */
	public static void main(String[] args){
		try {
			System.out.println("CArtAgO Infrastructure v."+CARTAGO_VERSION.getID()+ " - DISI, University of Bologna, Italy.");
			CartagoEnvironment env = CartagoEnvironment.getInstance();
			env.init();
			env.installInfrastructureLayer("default");
			if (hasOption(args,"-infra")){
				String address = getParam(args,"-address");
				if (address!=null){
					env.startInfrastructureService("default",address);
				} else {
					env.startInfrastructureService("default");
				}
			}
			System.out.println("Ready.");
		} catch (Exception ex){
			System.err.println("Execution Failed: "+ex.getMessage());
		}
	}
	
	private static boolean hasOption(String[] args, String arg){
		for (int i = 0; i<args.length; i++){
			if (args[i].equals(arg) && i<args.length-1){
				return true;
			} 
		}
		return false;
	}

	private static String getParam(String[] args, String arg){
		for (int i = 0; i<args.length; i++){
			if (args[i].equals(arg) && i<args.length-1){
				return args[i+1];
			} 
		}
		return null;
	}
	

	/**
	 * Keeps information about the CArtAgO nodes that can host linked remote artifacts
	 * 
	 * @author aricci
	 *
	 */
	static class LinkedNodeInfo {

		NodeId nodeId;
		String protocol;
		String address;
		
		public LinkedNodeInfo(NodeId nodeId, String protocol, String address){
			this.nodeId = nodeId;
			this.protocol = protocol;
			this.address = address;
		}
	
		
		public NodeId getNodeId() {
			return nodeId;
		}

		public String getProtocol() {
			return protocol;
		}

		public String getAddress() {
			return address;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj != null  && (obj instanceof LinkedNodeInfo)){
				LinkedNodeInfo node = ((LinkedNodeInfo) obj);
				if (node.getNodeId().equals(getNodeId()) && node.getProtocol().equals(this.getProtocol())){
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
	}
	
}
