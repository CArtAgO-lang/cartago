package examples;
import cartago.*;
import cartago.events.*;
import cartago.util.agent.*;

public class MyAgent extends Agent {
	
	private String artName;
	
	public MyAgent(String name, String artName) throws CartagoException {
		super(name);
		this.artName = artName;
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
			
			final ArtifactId counter = makeArtifact(artName,"test.Counter");
			focus(counter);
			//focus(counter, new cartago.events.SignalFilter(new String[]{"tick"}));			
			doAction(new Op("inc"));
			doAction(new Op("inc"));			
			log("start perceiving...");
			Percept p  = waitForPercept();
		    log("percept 1: "+p);
		    p  = waitForPercept();
		    log("percept 2: "+p);
		    
		    
			log("reading obs prop: "+getObsProperty("count").intValue());
			
			stopFocus(counter);
			log("stop focus.");

			doAction(new Op("inc"));
			doAction(new Op("inc"));
			
			do {
				p = waitForPercept();
				log("new percept: "+p);
			} while (!p.hasSignal());
									
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}
}
