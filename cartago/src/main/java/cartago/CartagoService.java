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
import cartago.security.AgentCredential;


/**
 * Entry point for working with CArtAgO.
 *  
 * @author aricci, mguidi
 *
 */
public class CartagoService {

	/* singleton CArtAgO node */
	private static CartagoNode instance;

	/* set of available infrastructure layers */
	private static Map<String,ICartagoInfrastructureLayer> infraLayers = new HashMap<String,ICartagoInfrastructureLayer>();
	private static String defaultInfraLayer = "rmi";
	
	/* set of information about linked nodes - for linkability with remote artifacts */
	private static List<LinkedNodeInfo> linkedNodes = new LinkedList<LinkedNodeInfo>();
			
	public static String getVersion() {
		return CARTAGO_VERSION.getID();
	}
	/**
	 * Start a CArtAgO node.
	 * 
	 * @throws CartagoException
	 */
	public static synchronized void startNode() throws CartagoException {
		if (instance != null){
			throw new CartagoNodeNotActiveException();
		} else {
			instance = new CartagoNode();
		}
	}

	/**
	 * Start a CArtAgO node.
	 * 
	 * @throws CartagoException
	 */
	public static synchronized void startNode(ICartagoLogger logger) throws CartagoException {
		if (instance != null){
			throw new CartagoNodeNotActiveException();
		} else {
			instance = new CartagoNode(logger);
		}
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
	
	// infrastructure layer management

	/**
	 * Install a CArtAgO infrastructure layer, to enable interaction with remote nodes.
	 * 
	 * @param type name of the layer, which typically corresponds to the protocol adopted. Use "default" to specify default type. 
	 * @throws CartagoException if the installation fails
	 */
	public static synchronized void installInfrastructureLayer(String type) throws CartagoException {
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
				wspName = CartagoNode.MAIN_WSP_NAME;
			}
			CartagoWorkspace wsp = instance.getWorkspace(wspName);
			if (wsp == null){
				throw new CartagoException("Unknown workspace "+wspName);
			} else {			
				CartagoSession session = new CartagoSession(eventListener);
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
			wspName = "default";
		}
		
		if ((protocol == null) || (protocol.equals("default"))) protocol = defaultInfraLayer;

		//If the infrastructure protocol is not installed yet (agent joining a remote workspace
		//without installing a CArtAgO node ex. CArtAgO-WS agents..) we install it
		if(!infraLayers.containsKey(protocol)) installInfrastructureLayer(protocol);
		
		CartagoSession session = new CartagoSession(eventListener);
		ICartagoContext startContext = joinRemoteWorkspace(wspName, wspAddress, protocol, cred, session);
		WorkspaceId wspId = startContext.getWorkspaceId();
		session.setInitialContext(wspId, startContext);
		return session;
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
				wspName = CartagoNode.MAIN_WSP_NAME;
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
				wspName = CartagoNode.MAIN_WSP_NAME;
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
			CartagoService.startNode();
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
