package main_test;

import cartago.CartagoService;
import cartago.util.BasicLogger;

public class SetupRemoteNodeWebInfr {
	
	public static void main(String[] args) throws Exception {			
		CartagoService.startNode();
		CartagoService.installInfrastructureLayer("web");
		CartagoService.startInfrastructureService("web");
		CartagoService.registerLogger("main",new BasicLogger());  
		System.out.println("CArtAgO Node Ready.");
	}

}
