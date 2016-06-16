package c4jexamples;

import cartago.*;

public class Clock extends Artifact {

	  boolean counting;
	  final static long TICK_TIME = 100;
	      
	  void init(){
	    counting = false;
	  }
	    
	  @OPERATION void start(){
	    if (!counting){
	      counting = true;
	      execInternalOp("count");
	    } else {
	      failed("already_counting");
	    }
	  }
	  
	  @OPERATION void stop(){
	    counting = false;
	  }

	  @INTERNAL_OPERATION void count(){
	      while (counting){
	        signal("tick");
	        await_time(TICK_TIME);
	      }
	  }
}
