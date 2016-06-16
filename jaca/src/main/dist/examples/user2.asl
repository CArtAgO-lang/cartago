!create_and_use.

+!create_and_use : true
  <- !setupTool(Id);
     !use(Id).
     
+!use(Counter)
  <- for (.range(I,1,100)){
      inc [artifact_id(Counter)];
     }.
     
-!use(Counter) [error_msg(Msg),env_failure_reason(inc_failed("max_value_reached",Value))]  
  <- println(Msg);
     println("last value is ",Value).

// create the tool
+!setupTool(C): true 
  <- makeArtifact("c0","c4jexamples.BoundedCounter",[50],C).

  
