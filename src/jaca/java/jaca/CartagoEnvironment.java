package jaca;

import jason.asSyntax.Literal;
import jason.environment.Environment;

import java.util.logging.Level;
import java.util.logging.Logger;

import jaca.CAgentArch;
import jaca.CartagoEnvironment;
import cartago.CartagoException;
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
    private boolean debug;
	private String serviceType;
	
	static Logger logger = Logger.getLogger(CartagoEnvironment.class.getName());

	public void init(String[] args) {
		logger.setLevel(Level.WARNING);
		wspName = cartago.CartagoEnvironment.MAIN_WSP_NAME;
		
		infrastructure = false;
		local = false;
		remote = false;
		standalone = true;
		debug = false;
		serviceType = "default";

		/*
		 * Arguments include also "options", whose prefix is "-"
		 * Options can be specified in any position. 
		 */
		if (args.length > 0){
			if (!args[0].startsWith("-")){
				
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
		}

		/* current supported options:
		 * -debug
		 */
		for (String opt: args){
			if (opt.startsWith("-")){
				if (opt.equals("-debug")){
					debug = true;
				} else {
					throw new IllegalArgumentException("Unknown option: "+opt);
				}
			}
		}
		
		cartago.CartagoEnvironment env = cartago.CartagoEnvironment.getInstance();

		if (standalone){
			try {
				if (debug){
					 Inspector insp = new Inspector();
					 insp.start();
					 env.init(insp.getLogger());
				} else {				 
					env.init();
				}
				env.installInfrastructureLayer("default");
				checkProtocols(args);
				logger.info("CArtAgO Environment - standalone setup succeeded.");
			} catch (Exception ex){
				logger.severe("CArtAgO Environment - standalone setup failed.");
				ex.printStackTrace();
			}
		} else if (infrastructure){
			try {
				env.init();
				checkProtocols(args);
				int nserv = checkServices(args);
				/*
				 * We install the default infrastructure layer only if not 
				 * already installed by one of the service parameter  
				 */
				if (!env.isInfrastructureLayerInstalled("default")){
					env.installInfrastructureLayer("default");
					logger.info("CArtAgO Environment - default infrastructure layer installed.");
				}
				if (nserv == 0){
					env.startInfrastructureService("default");
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
				env.installInfrastructureLayer("default");
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
				if (!args[i].startsWith("-")){
					String prot = args[i];
					try {
						//System.out.println("Protocol: "+prot);
						prot = prot.replace('\'', '\"');
						//System.out.println("Protocol: "+prot);
						Literal l = Literal.parseLiteral(prot);
						if (l.getFunctor().equals("protocol")){
							String protocol = l.getTerm(0).toString();
							cartago.CartagoEnvironment.getInstance().installInfrastructureLayer(protocol);
							logger.info("Installed protocol "+protocol);
							np++;
						}
					} catch (Exception ex){
						ex.printStackTrace();
					}
				}
			}
		return np;
	}

	private int checkServices(String[] args){
		int ns = 0;
		cartago.CartagoEnvironment env = cartago.CartagoEnvironment.getInstance();
		for (int i = 1; i < args.length; i++){
			if (!args[i].startsWith("-")){
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
						
						if(!env.isInfrastructureLayerInstalled(serviceType)){
							env.installInfrastructureLayer(serviceType);
						}
						
						if (address == null){
							env.startInfrastructureService(serviceType);
							logger.info("Installed service "+serviceType);
						} else {
							env.startInfrastructureService(serviceType,address);
							logger.info("Installed service "+serviceType+" at "+address);
						}
						ns++;
					}
				} catch (Exception ex){
					ex.printStackTrace();
				}
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
			ICartagoSession context = cartago.CartagoEnvironment.getInstance().startSession(wspName,new cartago.AgentIdCredential(agName),arch);
			logger.info("NEW AGENT JOINED: "+agName);
			return context;
		} else {
			ICartagoSession context = cartago.CartagoEnvironment.getInstance().startRemoteSession(wspName,wspAddress,serviceType, new cartago.AgentIdCredential(agName),arch);
			return context;
		}
	}


	@Override
	public void stop() {
		super.stop();
		if (!local && !remote){
			try {
				cartago.CartagoEnvironment.getInstance().shutdown();
			} catch (CartagoException e) {
				e.printStackTrace();
			}
		}
	}


}

