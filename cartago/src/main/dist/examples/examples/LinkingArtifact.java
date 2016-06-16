package examples;

import cartago.*;

@ARTIFACT_INFO(
	outports = {
		@OUTPORT(name = "out-1")
	}
) public class LinkingArtifact extends Artifact {
	
	@OPERATION void test(){
    	log("executing test.");
    	try {
    		execLinkedOp("out-1","inc");
    	} catch (Exception ex){
    		ex.printStackTrace();
    	}
	}

	@OPERATION void test2(OpFeedbackParam<Integer> v){
	   	log("executing test2.");
	    try {
    		execLinkedOp("out-1","getValue", v);
    		log("back: "+v.get());
    	} catch (Exception ex){
    		ex.printStackTrace();
    	}
	}

	@OPERATION void test3(){
	   	log("executing test3.");
	    try {
	    	ArtifactId id = makeArtifact("new_linked", "c4jexamples.LinkableArtifact", ArtifactConfig.DEFAULT_CONFIG);
	    	execLinkedOp(id,"inc");
    	} catch (Exception ex){
    		ex.printStackTrace();
    	}
	}
}
