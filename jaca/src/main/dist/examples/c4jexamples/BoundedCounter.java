package c4jexamples;

import cartago.*;

public class BoundedCounter extends Artifact {

	private int max;
	
	void init(int max){
		defineObsProperty("count",0);
		this.max = max;
	}
	    
	@OPERATION void inc(){
		ObsProperty prop = getObsProperty("count");
		if (prop.intValue() < max){
			prop.updateValue(prop.intValue()+1);
			signal("tick");
		} else {
			failed("inc failed","inc_failed","max_value_reached",max);
		}
	}
}