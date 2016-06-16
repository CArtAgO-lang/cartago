package test;

import cartago.CartagoService;
import cartago.util.BasicLogger;

public class HelloRemoteWorld {
	
	public static void main(String[] args) throws Exception {			
		CartagoService.startNode();
		CartagoService.installInfrastructureLayer("rmi");
		CartagoService.startInfrastructureService("rmi");
		CartagoService.registerLogger("default",new BasicLogger());  
	}
}