package c4jexamples;

import cartago.*;

public class TicketDispenser extends Artifact {

	void init(int n){
		defineObsProperty("available_tickets",n);
	}
	    
	@OPERATION void getATicket(){
		await("isAvailable");
		ObsProperty prop = getObsProperty("available_tickets");
		prop.updateValue(prop.intValue()-1);
	}

	@OPERATION void releaseTicket(){
		ObsProperty prop = getObsProperty("available_tickets");
		prop.updateValue(prop.intValue()+1);
	}
	
	@GUARD boolean isAvailable(){
		ObsProperty prop = getObsProperty("available_tickets");
		return prop.intValue() > 0;
	}
}