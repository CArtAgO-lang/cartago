package c4jtest;

import cartago.Artifact;
import cartago.OPERATION;
import cartago.ObsProperty;

/**
 *      Artifact that implements the auction. 
 */
public class AuctionArtEx1 extends Artifact {
    
    static final private String OPEN = "open";
    static final private String CLOSE = "closed";
    
    void init(String taskDs, int maxValue)  {
        // observable properties   
        defineObsProperty("task",          taskDs);
        defineObsProperty("maxValue",      maxValue);
        defineObsProperty("currentBid",    maxValue);
        defineObsProperty("currentWinner", "no_winner");    
        defineObsProperty("state",OPEN);
    }

    @OPERATION void clearAuction(){
    	getObsProperty("state").updateValue(CLOSE);
    }

    @OPERATION void bid(double bidValue) {
        ObsProperty state = getObsProperty("state");
        if (state.stringValue().equals(OPEN)){
	        ObsProperty opCurrentValue  = getObsProperty("currentBid");
			ObsProperty opMaxValue      = getObsProperty("maxValue");
			ObsProperty opCurrentWinner = getObsProperty("currentWinner");
	        if (bidValue < opCurrentValue.intValue() ||   // the bid is better than the previous
		 		(opCurrentWinner.stringValue().equals("no_winner") &&
				 opMaxValue.intValue()==(int)bidValue)) { // first bid exactly at starting value
					opCurrentValue.updateValue(bidValue);
					opCurrentWinner.updateValue(getOpUserName());
	        }
        } else {
        	failed("Auction not open","bid_failed","auction_closed");
        }
    }
    
}

