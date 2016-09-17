package examples;

import cartago.*;
import cartago.security.AgentIdCredential;

public class SimpleExample {
	
	public static void main(String[] args) throws Exception {		
		CartagoService.startNode();
		
		CartagoService.createWorkspace("mywsp");
				
		/* getting a session to work inside the env */
		ICartagoSession session = CartagoService.startSession("mywsp", new AgentIdCredential("simulator"), null);
		WorkspaceId wid = session.getJoinedWspId("mywsp");
		
		/* directly using the session for working inside the env */
		session.makeArtifact(wid, "myart", "test.Counter");
		
		/* using an aux agent class */
		new MyAgent("Michelangelo","myart").start(); 
		
	}
}
