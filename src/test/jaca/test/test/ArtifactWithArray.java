package test;

import cartago.*;

public class ArtifactWithArray extends Artifact {

	@OPERATION void myOp(Object[] names){
		for (Object obj: names){
			log(obj.toString());
		}
	}
}
