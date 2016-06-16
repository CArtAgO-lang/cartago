!observe.

+!observe : true 
  <- ?myTool(C);  // discover the tool
     focus(C).

+count(V) 
  <- println("observed new value: ",V).

+tick [artifact_name(Id,"c0")]  
  <- println("perceived a tick").

+?myTool(CounterId): true 
  <- lookupArtifact("c0",CounterId).

-?myTool(CounterId): true 
  <- .wait(10); 
     ?myTool(CounterId).