package test;

import cartago.*;

public class Counter extends Artifact {

	void init(){
		defineObsProperty("count",0);
	}
	    
	@OPERATION void inc(){
		ObsProperty prop = getObsProperty("count");
		prop.updateValue(prop.intValue()+1);
		signal("tick");
	}
	
}