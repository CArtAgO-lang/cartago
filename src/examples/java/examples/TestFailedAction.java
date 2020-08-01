package examples;

import cartago.*;
import cartago.util.agent.*;

public class TestFailedAction {
	
	public static void main(String[] args) throws Exception {		
		CartagoEnvironment.startEnvironment();
		CartagoContext ctx = new CartagoContext(new AgentIdCredential("agent-0"));
		
		ArtifactId id = ctx.makeArtifact("test", "examples.ArtifactWithFailure");
		try {
			ctx.doAction(id, new Op("testFail"));
		} catch (ActionFailedException ex){
			String msg = ex.getFailureMsg();
			Tuple descr = ex.getFailureDescr();
			System.out.println("Failure msg: "+msg);
			System.out.println("Description tuple: "+descr.getLabel());
			for (Object obj: descr.getContents()){
				System.out.println("arg: "+obj);
			}
			
		}
		
	}
}
