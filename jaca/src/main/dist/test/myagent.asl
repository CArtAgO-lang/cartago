!test.

+!test 
	<- println("hello");
     makeArtifact("second_console","cartago.tools.Console",[],Id);
     println(Id).

-!test [error_msg(Msg)]
  <- println(Msg);
     lookupArtifact("second_console",Id1);
     println(Id1).
     
	