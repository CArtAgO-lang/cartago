package examples;

import cartago.*;

public class Clock extends Artifact {

	  boolean working;
	  final static long TICK_TIME = 100;
	      
	  void init(){
	    working = false;
	  }
	    
	  @OPERATION void start(){
	    if (!working){
	      working = true;
	      execInternalOp("work");
	    } else {
	      failed("already_working");
	    }
	  }
	  
	  @OPERATION void stop(){
	    working = false;
	  }

	  @INTERNAL_OPERATION void work(){
	      while (working){
	        signal("tick");
	        await_time(TICK_TIME);
	      }
	  }
}
