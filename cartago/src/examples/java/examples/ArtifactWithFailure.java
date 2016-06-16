package examples;

import cartago.*;

public class ArtifactWithFailure extends Artifact {

	@OPERATION void testFail(){		
		log("executing testFail..");
		int param1 = 12;
		String param2 = "test";
		failed("This is the failure msg","failed_reason",param1,param2);
	}
	
}
