package test;

import cartago.*;

public class MyWSPRuleEngine extends AbstractWSPRuleEngine {

	private int nTotOp;
	private boolean created;
	private ArtifactId myCounter;
	
	public MyWSPRuleEngine(){
		nTotOp = 0;
		created = false;
	}
	
	protected void processActionRequest(OpRequestInfo desc){
		try {
			nTotOp++;
			log("requested "+desc.getOp()+" on "+desc.getTargetArtifactId()+" by "+desc.getAgentId()+" - tot op: "+nTotOp);
			
			// test API to create a new artifact / exec an op
			
			ArtifactId target = desc.getTargetArtifactId();
			Op op = desc.getOp();
			
			if (target.getName().equals("c0") && op.getName().equals("inc")){
			
				log("creating artifact...");
				if (!created){
					created = true;
					myCounter = this.makeArtifact("c1", "c4jtest.Counter");
				}
				log("created");
				
				this.execOp(myCounter, new Op("inc"));
				
			} 
			
			// test API to force the failure of current op
			
			if (target.getName().equals("c1")){
				
				desc.setFailed("This artifact is not for you", new Tuple("unavailable_artifact"));
			}
			
			// test API to access obs prop
			
			if (myCounter != null){
				ArtifactObsProperty prop = getArtifactObsProp(myCounter, "count");
				if (prop.intValue() > 2){
					log("the obs prop value of the new artifact is "+prop.intValue());
				}
			}
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}

	protected void processAgentJoinRequest(AgentJoinRequestInfo req){
		System.out.println("[WSPRULEMAN] agent requested to join "+req.getAgentId());
	}
	
    protected void processAgentQuitRequest(AgentQuitRequestInfo req) {
		System.out.println("[WSPRULEMAN] agent requested to quit "+req.getAgentId());
    }
    
    private void log(String msg){
    	System.out.println("[MyWspManRule] "+msg);
    }

}
