package main_test;

import cartago.CartagoEnvironment;
import cartago.util.BasicLogger;

public class SetupRemoteNodeWebInfr {
	
	public static void main(String[] args) throws Exception {			
		CartagoEnvironment env = CartagoEnvironment.getInstance();
		env.init();
		env.installInfrastructureLayer("web");
		env.startInfrastructureService("web");
		env.registerLogger("/main",new BasicLogger());  
		System.out.println("CArtAgO Node Ready.");
	}

}
