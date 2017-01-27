package jaca;

import jason.asSyntax.Literal;
import jason.environment.Environment;

import java.util.logging.Level;
import java.util.logging.Logger;

import jaca.CAgentArch;
import jaca.CartagoEnvironment;
import cartago.CartagoException;
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
    private boolean debug;
	private String serviceType;
    //private WorkspaceTree tree;

    private String hostPort = "localhost:20100";
    
	static Logger logger = Logger.getLogger(CartagoEnvironment.class.getName());

	public void init(String[] args) {

		logger.setLevel(Level.WARNING);
		wspName = cartago.CartagoService.MAIN_WSP_NAME;
		//tree = new WorkspaceTree(); //create topology tree
		
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

		/* current supported options:
		 * -debug
		 */

		String host = this.hostPort.split(":")[0];
		String port = this.hostPort.split(":")[1];
		for (String opt: args)
		    {
			if (opt.startsWith("-"))
			    {
				if (opt.equals("-debug")){
				    debug = true;
				}
				else
				    {
					throw new IllegalArgumentException("Unknown option: "+opt);
				    }
			    }

			if(opt.startsWith("host"))
			    {
				host = opt.split("=")[1].trim();
			    }
			else if(opt.startsWith("port"))
			    {
				port = opt.split("=")[1].trim();    
			    }			   			
		    }

		this.hostPort = host + ":" + port;
		System.out.println(hostPort);
		
		if (standalone){
			try {
			    //create main
			    // if (debug){
			    //Inspector insp = new Inspector();
					 //insp.start();
					 //CartagoService.startNode(insp.getLogger());
					 //	 }

			        CartagoService.startNode("main");
				CartagoService.installInfrastructureLayer("default");
				CartagoService.installTopologyLayer("default", this.hostPort);
				CartagoService.startInfrastructureCentralNodeService("default", this.hostPort);				
				checkProtocols(args);
				logger.info("CArtAgO Environment - standalone setup succeeded.");
			} catch (Exception ex){
				logger.severe("CArtAgO Environment - standalone setup failed.");
				ex.printStackTrace();
			}
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
							CartagoService.installInfrastructureLayer(protocol);
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
			ICartagoSession context = CartagoService.startSession(wspName,new cartago.AgentIdCredential(agName),arch);
			logger.info("NEW AGENT JOINED: "+agName);
			return context;
		} else {
		    
		    /*ICartagoSession context = CartagoService.startRemoteSession(wspName,wspAddress,serviceType, new cartago.AgentIdCredential(agName),arch);
		      return context;*/
		    return null;
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

