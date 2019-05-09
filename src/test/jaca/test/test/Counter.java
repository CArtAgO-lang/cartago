package test;
import cartago.*;

public class Counter extends Artifact {
	
	void init(){
		defineObsProperty("count",0);
	}
	    
	@OPERATION void inc(){
		ObsProperty prop = getObsProperty("count");
		log("prop "+prop);
		int newValue = prop.intValue() + 1;
		prop.updateValue(newValue);
		signal("incremented");
		log("counter incremented: "+newValue);
	}

	@OPERATION void inc(int dt){
		ObsProperty prop = getObsProperty("count");
		log("prop "+prop);
		int newValue = prop.intValue() + dt;
		prop.updateValue(newValue);
	}

}
