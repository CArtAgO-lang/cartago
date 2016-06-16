package examples;

import cartago.ArtifactId;
import cartago.CartagoException;
import cartago.Op;
import cartago.OpFeedbackParam;
import cartago.util.agent.Agent;

public class ConsumerAgent extends Agent {

	public ConsumerAgent(String agentName) {
		super(agentName);
	}
	
	public void run() {
		ArtifactId buffId = null;
		while (buffId==null){
			/* loop waiting for the buffer artifact to be available (i.e. created by a producer).
			 * 
			 * The focusWhenAvailable(artName) operation of the WorkspaceArtifact could have been 
			 * used as well (that op WAITS for the availability of an artifact with the name specified
			 * and the focuses it). Once the focusWhenAvailable terminates we are sure that the 
			 * artifact is available, hence the lookupArtifact can be invoked without the need to incapsulate
			 * it inside an endless loop.  
			 *  */
			try {
				buffId = lookupArtifact("buffer");
			} catch (CartagoException ex){
				/* buffer not ready yet... we wait some time*/
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {}
			}
		}

		try {
			OpFeedbackParam<Object> item = new OpFeedbackParam<Object>();
			while (true){
				/* continuously consuming items */
				
				/* retrieval of a new element from the buffer */
				doAction(buffId, new Op("get", item));
				/* consuming the item */
				consumeItem(item.get());
			}
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}
	
	/* dummy method for consuming an item */
	private void consumeItem(Object obj){
		try {
			doAction(new Op("println", "[" + this.getAgentName() + "] Consuming item " + obj));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
