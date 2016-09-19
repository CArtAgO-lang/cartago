package examples;

import cartago.*;

public class SimpleExample {
	
	public static void main(String[] args) throws Exception {		
		CartagoService.startNode();
		
		CartagoService.createWorkspace("mywsp");
				
		CartagoService.enableDebug("mywsp");

		/* getting a session to work inside the env */
		CartagoContext ctx = CartagoService.startSession("mywsp", new AgentIdCredential("simulator"));
		WorkspaceId wid = ctx.getJoinedWspId("mywsp");
		
		/* directly using the session for working inside the env */
		ctx.makeArtifact(wid, "myart", "test.Counter");
		
		/* using an aux agent class */
		new MyAgent("Michelangelo","myart").start(); 
		
		
	}
}
