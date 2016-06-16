package c4jexamples;

import cartago.*;

public class LinkableArtifact extends Artifact {
	
	int count;
	
	void init(){
		count = 0;
	}
	    
	@LINK void inc(){
    	log("inc invoked.");
		count++;
	}

	@LINK void getValue(OpFeedbackParam<Integer> v){
		log("getValue invoked");
		v.set(count);
	}
}
