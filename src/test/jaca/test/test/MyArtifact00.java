package test;

import cartago.*;

public class MyArtifact00 extends Artifact {

	void init(){
		defineObsProperty("p0",0);  
	}

	@OPERATION void op1(OpFeedbackParam<Integer> v){		
		getObsProperty("p0").updateValue(0);
		await("cond");
		int value = getObsProperty("p0").intValue();
		v.set(value);
	}
	
	@GUARD boolean cond(){
		int v = getObsProperty("p0").intValue();
		return v == 3;
	}
	
	@OPERATION void op2(){		
		int value = getObsProperty("p0").intValue();
		getObsProperty("p0").updateValue(value+1);
	}
	
}
