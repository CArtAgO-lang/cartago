package main_test;

import cartago.*;
import cartago.tools.inspector.*;

public class HelloWorld {
	
	public static void main(String[] args) throws Exception {		
		CartagoEnvironment env = CartagoEnvironment.getInstance();
		env.init();
				//CartagoService.registerLogger("main",new BasicLogger());
		new HelloAgent("Michelangelo1","c1").start(); 
	}
}
