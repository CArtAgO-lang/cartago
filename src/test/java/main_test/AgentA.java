package main_test;
import cartago.*;
import cartago.util.agent.*;

public class AgentA extends Agent {
	
	public AgentA(String name) throws CartagoException {
		super(name);
	}
	
	public void run() {
		try {
			doAction(new Op("println","Hello, world! from "+getName()));
			log("done");
			ActionFeedback af = doActionAsync(new Op("println","Hello again! from "+getName()));
			log("done");
			af.waitForCompletion();
			if (af.succeeded()){
				log("succeded");
			} else {
				log("failed");
			}
			
			ArtifactId counter = makeArtifact("c0-"+getName(),"test.Counter");
				
			do {
				doAction(counter, new Op("inc"));
				Thread.sleep(1000);
			} while (true);
						
			
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}
}
