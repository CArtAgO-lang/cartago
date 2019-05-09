package main_test;

import cartago.*;
import cartago.tools.inspector.*;

public class TestController {
	
	public static void main(String[] args) throws Exception {		
		CartagoService.startNode();
		//CartagoService.registerLogger("main",new BasicLogger());
		new HelloAgent("Michelangelo1","c1").start(); 
		Thread.sleep(1000);
		ICartagoController controller = CartagoService.getController("main");
		ArtifactInfo info = controller.getArtifactInfo("c1");
	    log("id: "+info.getId().getName());
	    log("template: "+info.getId().getArtifactType());
	    log("creator: "+info.getId().getCreatorId());
	    log("operations: ");
	    for (OpDescriptor op: info.getOperations()){
	    	log("op "+op.getOp().getName()+" "+op.getOp().getNumParameters()+" "+op.isUI());
	    }
	    
	    for (ArtifactObsProperty obs: info.getObsProperties()){
	    	log("obs "+obs.getName()+" - "+obs);
	    }
		
	}
	
	static void log(String msg){
		System.out.println(msg);
	}
}
