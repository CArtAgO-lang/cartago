package examples;

import cartago.*;
import cartago.security.*;

public class Ex00a_HelloWorld {
	
	public static void main(String[] args) throws Exception {		
		CartagoEnvironment.startEnvironment();
		CartagoContext ctx = CartagoEnvironment.startSession("main", new AgentIdCredential("agent-0"));
		ctx.doAction(new Op("println","Hello, world!"));
	}
}
