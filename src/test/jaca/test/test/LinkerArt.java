package test;

import cartago.*;

@ARTIFACT_INFO(outports = {
		@OUTPORT(name = "out")
})

public class LinkerArt extends Artifact {

	@OPERATION void dummyOp() throws OperationException{
		execLinkedOp("out","gino");
		log("linking: gino called ok.");
	}
}