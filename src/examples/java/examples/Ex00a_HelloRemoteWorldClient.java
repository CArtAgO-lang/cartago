package examples;

import cartago.*;
import cartago.security.*;

public class Ex00a_HelloRemoteWorldClient {
	
	public static void main(String[] args) throws Exception {		
		CartagoService.startNode();
		// CartagoContext ctx = CartagoService.startRemoteSession("main", "localhost", "default", new AgentIdCredential("agent-0"));
		CartagoContext ctx = CartagoService.startRemoteSession("main", "localhost", "web", new AgentIdCredential("agent-0"));
		ctx.doAction(new Op("println","Hello, world!"));
	}
}
