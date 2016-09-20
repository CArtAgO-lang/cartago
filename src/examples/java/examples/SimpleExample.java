package examples;

import cartago.*;

public class SimpleExample {
	
	public static void main(String[] args) throws Exception {		
		CartagoService.startNode();

		CartagoContext ctx = CartagoService.startSession(new AgentIdCredential("simulator"));
		WorkspaceId wid = ctx.getJoinedWspId(CartagoService.MAIN_WSP_NAME);
		ctx.makeArtifact(wid, "myart", "test.Counter");

		CartagoService.createWorkspace("mywsp");
				
		CartagoService.enableDebug("mywsp");

		/* getting a session to work inside the env */
		WorkspaceId wid1 = ctx.joinWorkspace("mywsp");
		
		/* directly using the session for working inside the env */
		ctx.makeArtifact(wid1, "myart1", "test.Counter");
		
		/* using an aux agent class */
		new MyAgent("Michelangelo","myart").start(); 
		
		
	}
}
