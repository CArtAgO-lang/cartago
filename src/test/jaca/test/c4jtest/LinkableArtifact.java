package c4jtest;

import cartago.*;

public class LinkableArtifact extends Artifact {
	
	int count;
	
	void init(){
		count = 0;
		defineObsProperty("aprop","hello");
	}
	    
	@LINK void inc(){
    	log("inc invoked.");
		count++;
	}

	@LINK void setProp(String st){
		getObsProperty("aprop").updateValue(st);
	}
	
	@LINK void getValue(OpFeedbackParam<Integer> v){
		log("getValue invoked");
		v.set(count);
	}
}
