package acme;

import cartago.CartagoEnvironment;
import cartago.utils.BasicLogger;

public class LaunchRemoteNodeWebInfr {

	public static void main(String[] args) throws Exception {			
		CartagoEnvironment env = CartagoEnvironment.getInstance();
		env.init("myMAS");
		env.installInfrastructureLayer("web");
		env.startInfrastructureService("web");
		env.registerLogger("/main",new BasicLogger());  
		System.out.println("CArtAgO Node Ready.");
	}

}