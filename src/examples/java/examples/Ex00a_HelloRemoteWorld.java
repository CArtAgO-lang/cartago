package examples;

import cartago.*;
import cartago.util.BasicLogger;

public class Ex00a_HelloRemoteWorld {
	
	public static void main(String[] args) throws Exception {			
		CartagoEnvironment.startEnvironment();
		CartagoEnvironment.installInfrastructureLayer("default");
		CartagoEnvironment.startInfrastructureService("default");
		CartagoEnvironment.registerLogger("main",new BasicLogger());  
		System.out.println("CArtAgO Node Ready.");
	}

}
