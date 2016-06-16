package c4jexamples;

import cartago.*;

public class ArtifactWithFailure extends Artifact {

	@OPERATION void testFail(){		
		log("executing testFail..");
		failed("Failure msg", "fake_descr", "one_parm",303);
	}
	
}
