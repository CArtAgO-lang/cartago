package c4jason;
import jason.asSyntax.Literal;
import jason.environment.Environment;

import java.util.logging.Level;
import java.util.logging.Logger;

import cartago.AbstractWorkspaceTopology;
import cartago.CartagoException;
import cartago.CartagoNode;
import cartago.CartagoService;
import cartago.ICartagoSession;
import cartago.tools.inspector.Inspector;

/**
 * Jason Environment Class enabling access to CArtAgO environments.
 * 
 * @author aricci
 *
 */
public class CartagoEnvironment extends Environment {

	private static CartagoEnvironment instance;
	private String wspName;
	private String wspAddress;
	
	private boolean local;		// an existing local node (false -> to be created, because not existing)
	private boolean remote;		// an existing remote node
	private boolean infrastructure;	// not existing, enable infrastructure service
    private boolean standalone; // default case, install a node
    
	private String serviceType;
	
	static Logger logger = Logger.getLogger(CartagoEnvironment.class.getName());

	public void init(String[] args) {
		logger.setLevel(Level.WARNING);
		wspName = cartago.CartagoNode.MAIN_WSP_NAME;
		
		infrastructure = false;
		local = false;
		remote = false;
		standalone = true;
		serviceType = "default";
		
		if (args.length > 0){
			if (args[0].equals("standalone")){
				standalone = true;
			} else if (args[0].equals("local")){
				local = true;
				standalone = false;
			} else if (args[0].equals("infrastructure")){
				infrastructure = true;
				standalone = false;
			} else if (args[0].equals("remote")){
				remote = true;
				standalone = false;
			} else {
				throw new IllegalArgumentException("Unknown argument: "+args[0]+" (should be local, remote or infrastructure)");
			}
		}

		if (standalone){
			try {
				 Inspector insp = new Inspector();
				 insp.start();
				 CartagoService.startNode(insp.getLogger());
				 
				// CartagoService.startNode();
				
				CartagoService.installInfrastructureLayer("default");
				checkProtocols(args);
				logger.info("CArtAgO Environment - standalone setup succeeded.");
			} catch (Exception ex){
				logger.severe("CArtAgO Environment - standalone setup failed.");
				ex.printStackTrace();
			}
		} else if (infrastructure){
			try {
				CartagoService.startNode();
				checkProtocols(args);
				int nserv = checkServices(args);
				/*
				 * We install the default infrastructure layer only if not 
				 * already installed by one of the service parameter  
				 */
				if (!CartagoService.isInfrastructureLayerInstalled("default")){
					CartagoService.installInfrastructureLayer("default");
					logger.info("CArtAgO Environment - default infrastructure layer installed.");
				}
				if (nserv == 0){
					CartagoService.startInfrastructureService("default");
					logger.info("CArtAgO Environment - default infrastructure service installed.");
				}
				
				logger.info("CArtAgO Environment - infrastructure setup succeeded.");
			} catch (Exception ex){
				logger.severe("CArtAgO Environment - infrastructure setup failed.");
				ex.printStackTrace();
			}
		} else if (remote) {
			if (args.length > 1){
				wspName = args[1];
				wspAddress = args[2];
			}
			try {
				CartagoService.installInfrastructureLayer("default");
				checkProtocols(args);
				logger.info("CArtAgO Environment - remote setup succeeded - Joining a remote workspace: "+wspName+"@"+wspAddress);
			} catch (Exception ex){
				logger.severe("CArtAgO Environment - remote setup failed.");
				ex.printStackTrace();
			}
		} else if (local){
			if (args.length > 1){
				wspName = args[1];
			}
			logger.info("CArtAgO Environment - local setup succeeded - Joining a local workspace: "+wspName);
		}		
		instance = this;
		
	}

	
	private int checkProtocols(String[] args){
		int np = 0;
			for (int i = 1; i < args.length; i++){
				String prot = args[i];
				try {
					//System.out.println("Protocol: "+prot);
					prot = prot.replace('\'', '\"');
					//System.out.println("Protocol: "+prot);
					Literal l = Literal.parseLiteral(prot);
					if (l.getFunctor().equals("protocol")){
						String protocol = l.getTerm(0).toString();
						CartagoService.installInfrastructureLayer(protocol);
						logger.info("Installed protocol "+protocol);
						np++;
					}
				} catch (Exception ex){
					ex.printStackTrace();
				}
			}
		return np;
	}

	private int checkServices(String[] args){
		int ns = 0;
		for (int i = 1; i < args.length; i++){
			String prot = args[i];
			try {
				//System.out.println("Service: "+prot);
				prot = prot.replace('\'', '\"');
				//System.out.println("Service: "+prot);
				Literal l = Literal.parseLiteral(prot);
				if (l.getFunctor().equals("service")){
					String serviceType = l.getTerm(0).toString();
					String address = null;
					if (l.getArity() > 1){
						address = l.getTerm(1).toString();
						if (address.startsWith("\"")){
							address = address.substring(1);
						}
						if (address.endsWith("\"")){
							address = address.substring(0,address.length()-1);
						}
					}

					/*
					 * Install the infrastructure layer for the protocol if not yet installed
					 * before installing the service
					 */
					
					if(!CartagoService.isInfrastructureLayerInstalled(serviceType)){
						CartagoService.installInfrastructureLayer(serviceType);
					}
					
					if (address == null){
						CartagoService.startInfrastructureService(serviceType);
						logger.info("Installed service "+serviceType);
					} else {
						CartagoService.startInfrastructureService(serviceType,address);
						logger.info("Installed service "+serviceType+" at "+address);
					}
					ns++;
				}
			} catch (Exception ex){
				ex.printStackTrace();
			}
		}
		return ns;
}
	
	/**
	 * Get the instance of this environment.
	 * 
	 * @return
	 */
	public static CartagoEnvironment getInstance(){
		return instance;
	}
	
	/**
	 * Join an agent to the default workspace of the node
	 * 
	 * @param agName agent node
	 * @param arch agent arch. class
	 * @return the interface to act inside the workspace
	 * @throws Exception
	 */
	public ICartagoSession startSession(String agName, CAgentArch arch) throws Exception {
		if (wspAddress == null){ 
			ICartagoSession context = CartagoService.startSession(wspName,new cartago.security.AgentIdCredential(agName),arch);
			logger.info("NEW AGENT JOINED: "+agName);
			return context;
		} else {
			ICartagoSession context = CartagoService.startRemoteSession(wspName,wspAddress,serviceType, new cartago.security.AgentIdCredential(agName),arch);
			return context;
		}
	}


	@Override
	public void stop() {
		super.stop();
		if (!local && !remote){
			try {
				CartagoService.shutdownNode();
			} catch (CartagoException e) {
				e.printStackTrace();
			}
		}
	}


}

