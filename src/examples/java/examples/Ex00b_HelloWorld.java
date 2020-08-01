package examples;

import cartago.CartagoEnvironment;
import cartago.util.BasicLogger;

public class Ex00b_HelloWorld {
	
	public static void main(String[] args) throws Exception {		
		CartagoEnvironment.startEnvironment();
		// CartagoService.registerLogger("main",new BasicLogger());
		new HelloAgent("Michelangelo", "counter").start(); 
	}
}
