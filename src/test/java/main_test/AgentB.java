package main_test;
import cartago.*;
import cartago.util.agent.*;

public class AgentB extends Agent {
	
	public AgentB(String name) throws CartagoException {
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
	
			ArtifactId counter = null;
			while (true){
				try {
				  counter = lookupArtifact("c0");
				  break;
				} catch (Exception ex){
				  Thread.sleep(2000);
				}
			}
			do {
				doAction(counter, new Op("inc"));
				Thread.sleep(200);
			} while (true);
						
			
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}
}
