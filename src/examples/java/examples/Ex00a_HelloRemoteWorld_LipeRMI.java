package examples;

import cartago.*;
import cartago.util.BasicLogger;

public class Ex00a_HelloRemoteWorld_LipeRMI {
	
	public static void main(String[] args) throws Exception {			
		CartagoService.startNode();
		CartagoService.installInfrastructureLayer("lipermi");
		CartagoService.startInfrastructureService("lipermi");
		CartagoService.registerLogger("main",new BasicLogger());  
		System.out.println("CArtAgO Node Ready.");
	}

}
