package examples;

import cartago.*;
import cartago.security.*;

public class Ex00a_HelloRemoteWorldClient {
	
	public static void main(String[] args) throws Exception {		
		CartagoContext ctx = CartagoService.startRemoteSession("main", "localhost", "default", 
				new AgentIdCredential("agent-0"));
		ctx.doAction(new Op("println","Hello, world!"));
	}
}
