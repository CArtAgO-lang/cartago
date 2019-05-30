package main_test;

import cartago.CartagoEnvironment;
import cartago.util.BasicLogger;

public class HelloRemoteWorld {
	
	public static void main(String[] args) throws Exception {			
		CartagoEnvironment env = CartagoEnvironment.getInstance();
		env.initRoot();
		env.installInfrastructureLayer("rmi");
		env.startInfrastructureService("rmi");
		env.registerLogger("main",new BasicLogger());  
	}
}