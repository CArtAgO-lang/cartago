package c4jexamples;

import cartago.*;
import java.util.*;

public class ContractNetBoard extends Artifact {

	private List<Bid> bids;
	private int bidId;
	
	void init(String taskDescr, long duration){
		this.defineObsProperty("task_description", taskDescr);
		long started = System.currentTimeMillis(); 
		this.defineObsProperty("created", started);
		long deadline = started + duration;
		this.defineObsProperty("deadline", deadline);
		this.defineObsProperty("state","open");
		bids = new ArrayList<Bid>();
		bidId = 0;
		this.execInternalOp("checkDeadline",duration);
	}
	
	@OPERATION void bid(String bid, OpFeedbackParam<Integer> id){
		if (getObsProperty("state").stringValue().equals("open")){
			bidId++;
			bids.add(new Bid(bidId,bid));
			id.set(bidId);
		} else {
			this.failed("cnp_closed");
		}
	}
	
	@OPERATION void award(Bid prop){
		this.defineObsProperty("winner", prop.getId());
	}

	@INTERNAL_OPERATION void checkDeadline(long dt){
		await_time(dt);
		getObsProperty("state").updateValue("closed");
		log("bidding stage closed.");
	}
	
	@OPERATION void getBids(OpFeedbackParam<Bid[]> bidList){
		await("biddingClosed");
		Bid[] vect = new Bid[bids.size()];
		int i = 0;
		for (Bid p: bids){
			vect[i++] = p;
		}
		bidList.set(vect);
	}
	
	@GUARD boolean biddingClosed(){
		return this.getObsProperty("state").stringValue().equals("closed");
	}

	static public class Bid {
		
		private int id;
		private String descr;
		
		public Bid(int id, String descr){
			this.descr = descr;
			this.id = id;
		}
		
		public int getId(){
			return id;
		}
		
		public String getDescr(){
			return descr;
		}
		
		public String toString(){
			return descr;
		}
	}
}
