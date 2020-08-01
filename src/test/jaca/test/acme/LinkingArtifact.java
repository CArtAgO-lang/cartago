package acme;

import cartago.*;

@ARTIFACT_INFO(
	outports = {
		@OUTPORT(name = "out-1")
	}
) public class LinkingArtifact extends Artifact {
	
	@OPERATION void test(){
    	log("executing.");
    	try {
    		execLinkedOp("out-1","inc");
    	} catch (Exception ex){
    		ex.printStackTrace();
    	}
	}

	@OPERATION void test2(OpFeedbackParam<Integer> v){
	   	log("executing.");
	    try {
    		execLinkedOp("out-1","getValue", v);
    		log("back: "+v.get());
    	} catch (Exception ex){
    		ex.printStackTrace();
    	}
	}

	/*
	@OPERATION void test3(){
	   	log("executing.");
	    try {
	    	ArtifactId id = makeArtifact("test", "test.c4jtest.LinkableArtifact", ArtifactConfig.DEFAULT_CONFIG);
	    	execLinkedOp(id,"setProp","ok");
    	} catch (Exception ex){
    		ex.printStackTrace();
    	}
	}*/
}
