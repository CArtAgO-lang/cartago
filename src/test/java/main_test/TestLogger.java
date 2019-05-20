package main_test;

import cartago.CartagoEnvironment;
import cartago.tools.inspector.*;

public class TestLogger {
	
	public static void main(String[] args) throws Exception {		
		Inspector insp = new Inspector();
	    insp.start();
	    CartagoEnvironment env = CartagoEnvironment.getInstance();
		env.init(insp.getLogger());
		new AgentA("Michelangelo1").start(); 
		Thread.sleep(1000);
		new AgentA("Michelangelo2").start(); 
		Thread.sleep(2000);
		new AgentA("Michelangelo3").start(); 
		Thread.sleep(2000);
		new AgentA("Michelangelo4").start(); 
	}
}
