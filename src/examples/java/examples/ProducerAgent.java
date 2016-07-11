package examples;

import cartago.ArtifactId;
import cartago.CartagoException;
import cartago.Op;
import cartago.util.agent.Agent;

public class ProducerAgent extends Agent {

	private int numItemsProduced;
	
	public ProducerAgent(String agentName) {
		super(agentName);
	}
	public void run() {
		try {
			String item;
			ArtifactId buffId; 
			try {
				/* we try to create the artifact ...*/
				buffId = makeArtifact("buffer", "examples.BoundedBuffer", new Object[]{8});
			} catch (CartagoException ex){
				/* if the makeArtifact fails it means that an artifact with that name already exists
				 * (i.e. some other producer agent has already created it) -> buffer lookup */
				buffId = lookupArtifact("buffer");
			}

			/* buffer found, continuously producing items */
			while (true){
				/* production of a new item */
				item = produceItem(numItemsProduced);
				/* the new item is added into the bounded buffer */
				doAction(buffId, new Op("put", item));
				/* logging */
				doAction(new Op("println", "Item " + item + " produced"));
			}
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}

	/* dummy method for producing an item */
	private String produceItem(Object obj){
		return (++numItemsProduced) + "-of-"+getAgentName();
	}
}