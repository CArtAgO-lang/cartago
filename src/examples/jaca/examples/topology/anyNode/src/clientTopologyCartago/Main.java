package clientTopologyCartago;

import cartago.CartagoService;
import cartago.util.BasicLogger;

public class Main 
{

	public Main() 
	{
		
	}
	
	public static void main(String[] args) throws Exception {
		String ip = "";
	    
	    
	    boolean log = false;
	    if(args.length < 3) //start central node
	    {
	    	System.out.println("Server");
	    	CartagoService.startNode("main");
	    	CartagoService.installInfrastructureLayer("default");
	    	CartagoService.installTopologyLayer("default", "localhost:8089");
	    	//CartagoService.startInfrastructureService("default", "localhost:8081", "main/test");
	    	CartagoService.startInfrastructureCentralNodeService("default", "localhost:8089");
	    	ip = "localhost:8089";
	    	
	    	//mount some workspaces for testing
	    	CartagoService.mount("main/x1");
	    	
	    	Thread.sleep(10000);
	    	
	    	
	    }
	    else
	    {
	    	System.out.println("Client");
	    	CartagoService.startNode(args[0]);
	    	CartagoService.installInfrastructureLayer("default");
	    	CartagoService.installTopologyLayer("default", args[1]);
	    	CartagoService.startInfrastructureService("default", args[2], args[0]);
	    	ip = args[2];
	    	if(args.length > 3)
	    		log = Boolean.parseBoolean(args[3]);
	    	
	    	
	    }
	    if(log)
	    	CartagoService.registerLogger("default",new BasicLogger());
	    //CartagoService.enableLinkingWithNode(id, support, address);
	    System.out.println("CArtAgO Node Ready on " + ip);	 
	    
	    CartagoService.printTree();
	  }


}
