package examples;

import cartago.*;
import cartago.security.*;

public class Ex00a_HelloWorld {
	
	public static void main(String[] args) throws Exception {		
		CartagoService.startNode();
		ICartagoSession session = CartagoService.startSession("main", new AgentIdCredential("agent-0"), null);
		session.doAction(new Op("println","Hello, world!"), null, -1);
	}
}
