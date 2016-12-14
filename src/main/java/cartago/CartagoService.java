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

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cartago.infrastructure.CartagoInfrastructureLayerException;
import cartago.infrastructure.ICartagoInfrastructureLayer;
import cartago.tools.inspector.Inspector;
import cartago.util.agent.ActionFeedbackQueue;

import cartago.topology.WorkspaceTree;
import cartago.topology.TopologyException;
import cartago.infrastructure.topology.ICartagoInfrastructureTopologyLayer;


/**
 * Entry point for working with CArtAgO.
 *  
 * @author aricci, mguidi
 *
 */
public class CartagoService {

	public static String MAIN_WSP_NAME = "main";
	/* singleton CArtAgO node */
	private static CartagoNode instance;
        private static  ICartagoInfrastructureTopologyLayer topologyService;
    // toknow if the infrastructure layer should be used
        private static boolean local = true;
	/* set of available infrastructure layers */
	private static Map<String,ICartagoInfrastructureLayer> infraLayers = new HashMap<String,ICartagoInfrastructureLayer>();
	private static String defaultInfraLayer = "rmi";
	private static HashMap<String,Inspector> debuggers = new HashMap<String,Inspector>();
	
	/* set of information about linked nodes - for linkability with remote artifacts */
	private static List<LinkedNodeInfo> linkedNodes = new LinkedList<LinkedNodeInfo>();


    public static void printTree()
    {
	WorkspaceTree wt = CartagoService.instance.getTree();
	wt.printTree();
    }
    
    public static synchronized void mount(String mountPoint) throws CartagoException
    {
	if(CartagoService.topologyService == null)
	    {
		throw new CartagoException("Infrastructure topology layer not installed");
	    }
	try
	    {
		CartagoService.topologyService.mount(mountPoint);
	    }
	catch(TopologyException ex)
	    {
		ex.printStackTrace();
		throw new CartagoException("Mount failed");
	    }
    }
    
	public static String getVersion() {
		return CARTAGO_VERSION.getID();
	}
	/**
	 * Start a CArtAgO node.
	 * 
	 * @throws CartagoException
	 */
    public static synchronized NodeId startNode(String wspPath) throws CartagoException {
		if (instance == null){
		    instance = new CartagoNode(wspPath);
		}
		return instance.getId();
	}



        public static synchronized NodeId startNode() throws CartagoException {
		if (instance == null){
		    instance = new CartagoNode();
		}
		return instance.getId();
	}

	/**
	 * Start a CArtAgO node.
	 * 
	 * @throws CartagoException
	 */
	public static synchronized NodeId startNode(ICartagoLogger logger) throws CartagoException {
		if (instance != null){
			throw new CartagoNodeNotActiveException();
		} else {
			instance = new CartagoNode(logger);
		}
		return instance.getId();
	}
	
	/**
	 * Check if the CArtAgO node is on.
	 * @return
	 */
	public static synchronized boolean isNodeActive(){
		return instance!=null;
	}

	/**
	 * Shutdown the CArtAgO node.
	 * 
	 * @throws CartagoException
	 */
	public static synchronized void shutdownNode() throws CartagoException {
		if (instance != null){
			instance.shutdown();
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
			instance = null;
		} 
		else {
			throw new CartagoNodeNotActiveException();
		}
	}
	
	/**
	 * 
	 * Creates a workspace on the node instance 
	 * @param name workspace name
	 * @param log
	 * @throws CartagoException
	 */
	public static  void createWorkspace(String name, ICartagoLogger log) throws CartagoException {
		if (instance != null){
			instance.createWorkspace(name,log);
		} else {
			throw new CartagoNodeNotActiveException();
		}
	}

	/**
	 * 
	 * Creates a workspace on the node instance 
	 * @param name workspace name
	 * @throws CartagoException
	 */
	public static synchronized void createWorkspace(String name) throws CartagoException {
		if (instance != null){
			instance.createWorkspace(name);
		} else {
			throw new CartagoNodeNotActiveException();
		}
	}
		
	
	// infrastructure layer management


    //it is not an actual infrastructure layer as it depends on an infrastructureLayer that should be already installed. This should be installed in every node
    public static synchronized void installTopologyLayer(String type, String centralNodeAddress) throws CartagoException
    {
	if (type.equals("default"))
	    {
		type = defaultInfraLayer;
	    }

	ICartagoInfrastructureTopologyLayer service;
	ICartagoInfrastructureLayer supportingService = infraLayers.get(type);
	if (CartagoService.topologyService == null && supportingService  != null)
	    {
		try
		    {
			Class<ICartagoInfrastructureTopologyLayer> serviceClass = (Class<ICartagoInfrastructureTopologyLayer>) Class.forName("cartago.infrastructure."+type+".topology.CartagoInfrastructureTopologyLayer");
			service = serviceClass.newInstance();
			CartagoService.topologyService = service;
			service.setCentralNodeAddress(centralNodeAddress);
		    }
		catch (Exception ex)
		    {
			ex.printStackTrace();
			throw new CartagoException("Invalid infrastructure layer: "+type);
		    }
	    }
	else
	    {
		throw new CartagoException("Infrastructure layer "+type+"/topology cannot be installed");
	    }
    }	


    
	/**
	 * Install a CArtAgO infrastructure layer, to enable interaction with remote nodes.
	 * 
	 * @param type name of the layer, which typically corresponds to the protocol adopted. Use "default" to specify default type. 
	 * @throws CartagoException if the installation fails
	 */
	public static synchronized void installInfrastructureLayer(String type) throws CartagoException {
	       CartagoService.local = false;
		if (type.equals("default")){
			type = defaultInfraLayer;
		}
		ICartagoInfrastructureLayer service = infraLayers.get(type);
		if (service == null){
			try {
				Class<ICartagoInfrastructureLayer> serviceClass = (Class<ICartagoInfrastructureLayer>) Class.forName("cartago.infrastructure."+type+".CartagoInfrastructureLayer");
				service = serviceClass.newInstance();
				infraLayers.put(type, service);
			} catch (Exception ex){
				ex.printStackTrace();
				throw new CartagoException("Invalid infrastructure layer: "+type);
			}
		} else {
			throw new CartagoException("Infrastructure layer "+type+"already installed");
		}
	}	

	/**
	 * Change the default infrastructure layer name
	 * 
	 * @param name infrastructure layer name
	 */
	public static void setDefaultInfrastructureLayer(String name){
		defaultInfraLayer = name;
	}
	
	// service management

    //this is only excecuted by the central node
    public static synchronized void startInfrastructureCentralNodeService(String type, String address) throws CartagoException
    {
	if(CartagoService.topologyService == null)
	    throw new CartagoException("Infrastructure layer "+type+".topology not installed");
	
	startInfrastructureService(type, address);

	try
	    {
		if (type.equals("default"))
		    type = defaultInfraLayer;
		//CartagoService.topologyService.setCentralNodeAddress(address);
		ICartagoInfrastructureLayer service = infraLayers.get(type);
		WorkspaceId rootWspId = service.getMainWorkspace(address);
		NodeId nId =  service.getNodeAt(address);
		CartagoService.topologyService.startTopologyService(address, rootWspId, nId);
		
		
	    }
	catch(Exception ex)
	    {
		throw new CartagoException("Infrastructure layer service failure: "+type+".topology");
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
    public static synchronized void startInfrastructureService(String type, String address) throws CartagoException {
		if (type.equals("default")){
			type = defaultInfraLayer;
		}
		ICartagoInfrastructureLayer service = infraLayers.get(type);
		if (service != null){		    
			try {
				service.startService(instance,address);
				
				//check topology service and mount node
				
				
			} catch (Exception ex){
				throw new CartagoException("Infrastructure layer service failure: "+type);
			}
		} else {
			throw new CartagoException("Infrastructure layer "+type+"not installed");
		}
	}

    //topology version
    public static synchronized void startInfrastructureService(String type, String address, String defaultWspMountPath) throws CartagoException {
	if (type.equals("default")){
	    type = defaultInfraLayer;
	}
	ICartagoInfrastructureLayer service = infraLayers.get(type);
	if (service != null){
	    if(CartagoService.topologyService == null)
		{
		    throw new CartagoException("Infrastructure layer service failure: "+type+".topology");
		}
	    try {
		service.startService(instance,address);

		//the order of the mounting is important on deployment
		CartagoService.topologyService.mountNode(defaultWspMountPath, address);
				
				
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
	public static synchronized void startInfrastructureService(String type) throws CartagoException {
		startInfrastructureService(type,"");
	}
	
	// start session methods
	
	/**
	 * Start a CArtAgO session in a local workspace.
	 * 
	 * @param wspName workspace to join
	 * @param cred agent credential
	 * @param eventListener listener to perceive workspace events
	 * @return
	 * @throws CartagoException
	 */
	public static synchronized ICartagoSession startSession(String wspName, AgentCredential cred, ICartagoListener eventListener) throws CartagoException {
		if (instance != null){
			if (wspName==null){
				wspName = CartagoService.MAIN_WSP_NAME;
			}
			CartagoWorkspace wsp = instance.getWorkspace(wspName);
			if (wsp == null){
				throw new CartagoException("Unknown workspace "+wspName);
			} else {			
				CartagoSession session = new CartagoSession(cred,null,eventListener);
				ICartagoContext startContext = wsp.join(cred,session);
				WorkspaceId wspId = startContext.getWorkspaceId();
				session.setInitialContext(wspId, startContext);
				return session;
			}
		} else {
			throw new CartagoNodeNotActiveException();
		}
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
	public static synchronized ICartagoSession startRemoteSession(String wspName, String wspAddress, String protocol, AgentCredential cred, ICartagoListener eventListener) throws CartagoException {
		if (wspName==null){
		    wspName = "default"; //Xavier: I think it shoud be main
		}
		
		if ((protocol == null) || (protocol.equals("default"))) protocol = defaultInfraLayer;

		//If the infrastructure protocol is not installed yet (agent joining a remote workspace
		//without installing a CArtAgO node ex. CArtAgO-WS agents..) we install it
		if(!infraLayers.containsKey(protocol)) installInfrastructureLayer(protocol);
		
		CartagoSession session = new CartagoSession(cred,null,eventListener);
		ICartagoContext startContext = joinRemoteWorkspace(wspName, wspAddress, protocol, cred, session);
		WorkspaceId wspId = startContext.getWorkspaceId();
		session.setInitialContext(wspId, startContext);
		return session;
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
	public static synchronized CartagoContext startSession(String wspName, AgentCredential cred) throws CartagoException {
		if (instance != null){
			if (wspName==null){
				wspName = CartagoService.MAIN_WSP_NAME;
			}
			CartagoWorkspace wsp = instance.getWorkspace(wspName);
			if (wsp == null){
				throw new CartagoException("Unknown workspace "+wspName);
			} else {			
				CartagoContext context = new CartagoContext(cred,wspName);
				// WorkspaceId wspId = context.getJoinedWspId(wspName);
				// context.getCartagoSession().setInitialContext(wspId, context.getCartagoSession().get());
				return context;
			}
		} else {
			throw new CartagoNodeNotActiveException();
		}
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
	public static synchronized CartagoContext startSession(AgentCredential cred) throws CartagoException {
		return startSession(CartagoService.MAIN_WSP_NAME,cred);
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
	public static synchronized CartagoContext startRemoteSession(String wspName, String wspAddress, String protocol, AgentCredential cred) throws CartagoException {
		if (wspName==null){
		    wspName = "default"; //Xavier: main?
		}
		
		if ((protocol == null) || (protocol.equals("default"))) protocol = defaultInfraLayer;

		//If the infrastructure protocol is not installed yet (agent joining a remote workspace
		//without installing a CArtAgO node ex. CArtAgO-WS agents..) we install it
		if(!infraLayers.containsKey(protocol)) { 
			installInfrastructureLayer(protocol);
		}
		CartagoContext context = new CartagoContext(cred);
		ICartagoContext startContext = joinRemoteWorkspace(wspName, wspAddress, protocol, cred, context.getCartagoSession());
		WorkspaceId wspId = startContext.getWorkspaceId();
		context.getCartagoSession().setInitialContext(wspId, startContext);
		return context;
	}
	

	//
	
	/**
	 * Join a remote workspace - called by CArtAgO node.
	 * 
	 * @param wspName workspace name
	 * @param IP address of the workspace
	 * @param protocol infrastructure protocol used to contact the node ("default" for default one)
	 * @param cred agent credentials - (es: cartago.security.UserIdCredential(String agentName))
	 * @param eventListener listener for events to be perceived by the agent 
	 * @return a context to act inside the workspace
	 * @throws cartago.security.SecurityException
	 * @throws CartagoException
	 */
	static synchronized ICartagoContext joinRemoteWorkspace(String wspName, String address, String protocol, AgentCredential cred, ICartagoCallback eventListener) throws cartago.security.SecurityException, CartagoException{
		try {
			if ((protocol == null) || (protocol.equals("default"))){
				protocol = defaultInfraLayer;
			} 
			ICartagoInfrastructureLayer service = infraLayers.get(protocol);
			ICartagoContext ctx = service.joinRemoteWorkspace(wspName, address, cred, eventListener);
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
			return ctx;
		} catch (CartagoInfrastructureLayerException ex) {
			ex.printStackTrace();
			throw new CartagoException("Join "+wspName+"@"+address+" failed ");
		}
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
	static OpId execRemoteInterArtifactOp(ICartagoCallback callback, long callbackId, AgentId userId, ArtifactId srcId, ArtifactId targetId, Op op, long timeout, IAlignmentTest test) throws CartagoInfrastructureLayerException, CartagoException {
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
	}
	
	/**
	 * Add an artifact factory for artifact templates
	 * 
	 * @param wspName workspace name
	 * @param factory artifact factory
	 * @throws CartagoException
	 */
	public static synchronized void addArtifactFactory(String wspName, ArtifactFactory factory) throws CartagoException {
		if (instance != null){
			if (wspName==null){
				wspName = CartagoService.MAIN_WSP_NAME;
			}
			instance.getWorkspace(wspName).getKernel().addArtifactFactory(factory);
		} else {
			throw new CartagoNodeNotActiveException();
		}
	}
	
	/**
	 * Remove an existing class loader for artifacts
	 * @param wspName workspace name
	 * @param name id of the artifact factory
	 * @throws CartagoException
	 */
	public static synchronized void removeArtifactFactory(String wspName, String name) throws CartagoException {
		if (instance != null){
			if (wspName==null){
				wspName = CartagoService.MAIN_WSP_NAME;
			}
			instance.getWorkspace(wspName).getKernel().removeArtifactFactory(name);
		} else {
			throw new CartagoNodeNotActiveException();
		}
	}

	/**
	 * Register a new logger for CArtAgO Workspace Kernel events
	 * 
	 * @param wspName
	 * @param logger
	 * @throws CartagoException
	 */
	public static synchronized void registerLogger(String wspName, ICartagoLogger logger) throws CartagoException {
		if (instance != null){
			instance.getWorkspace(wspName).registerLogger(logger);
		} else {
			throw new CartagoNodeNotActiveException();
		}
	}

	/**
	 * Enable debugging of a CArtAgO Workspace 
	 * 
	 * @param wspName
	 * @throws CartagoException
	 */
	public static synchronized void enableDebug(String wspName) throws CartagoException {
		if (instance != null){
			 Inspector insp = debuggers.get(wspName);
			 if (insp == null){
				 CartagoWorkspace wsp = instance.getWorkspace(wspName);
				 insp = new Inspector();
				 insp.start();
				 wsp.registerLogger(insp.getLogger());
				 debuggers.put(wspName, insp);
			 }
		} else {
			throw new CartagoNodeNotActiveException();
		}
	}
	
	/**
	 * Disable debugging of a CArtAgO Workspace 
	 * 
	 * @param wspName
	 * @throws CartagoException
	 */
	public static synchronized void disableDebug(String wspName) throws CartagoException {
		if (instance != null){
			 Inspector insp = debuggers.remove(wspName);
			 if (insp != null){
				 CartagoWorkspace wsp = instance.getWorkspace(wspName);
				 wsp.unregisterLogger(insp.getLogger());
			 }
		} else {
			throw new CartagoNodeNotActiveException();
		}
	}
	
	
	/**
	 * Register a new logger for a remote CArtAgO Workspace 
	 * 
	 * @param wspName
	 * @param logger
	 * @throws CartagoException
	 */
	public static synchronized void registerLoggerToRemoteWsp(String wspName, String address, String protocol, ICartagoLogger logger) throws CartagoException {
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
	public static synchronized void unregisterLogger(String wspName, ICartagoLogger logger) throws CartagoException {
		if (instance != null){
			instance.getWorkspace(wspName).unregisterLogger(logger);
		} else {
			throw new CartagoNodeNotActiveException();
		}
	}	
	
	/**
	 * Enable linking to the specified node
	 * 
	 * @param id node id
	 * @param support
	 * @param address
	 */
	public static void enableLinkingWithNode(NodeId id, String support, String address){
		linkedNodes.add(new LinkedNodeInfo(id,support,address));
	}

	/**
	 * Getting a controller.
	 * 
	 * @param wspName
	 * @return
	 * @throws CartagoException
	 */
	public static ICartagoController  getController(String wspName) throws CartagoException {
		if (instance != null){
			return instance.getWorkspace(wspName).getController();
		} else {
			throw new CartagoNodeNotActiveException();
		}
	}
	
	public static boolean isInfrastructureLayerInstalled(String protocol){
		if(protocol.equals("default")) protocol = defaultInfraLayer;
		return infraLayers.get(protocol)!=null;
	}
	
	/**
	 * Install CArtAgO Node with RMI infrastructure service.
	 * 
	 */
	public static void main(String[] args){
		try {
			System.out.println("CArtAgO Infrastructure v."+CARTAGO_VERSION.getID()+ " - DISI, University of Bologna, Italy.");
			CartagoService.startNode("main");
			CartagoService.installInfrastructureLayer("default");
			if (hasOption(args,"-infra")){
				String address = getParam(args,"-address");
				if (address!=null){
					CartagoService.startInfrastructureService("default",address);
				} else {
					CartagoService.startInfrastructureService("default");
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

    public static boolean isLocal()
    {
	return CartagoService.local;
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
