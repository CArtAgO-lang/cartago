package test;

import cartago.*;
import cartago.tools.inspector.*;

public class HelloWorld {
	
	public static void main(String[] args) throws Exception {		
		CartagoService.startNode();
		//CartagoService.registerLogger("default",new BasicLogger());
		new HelloAgent("Michelangelo1","c1").start(); 
	}
}
