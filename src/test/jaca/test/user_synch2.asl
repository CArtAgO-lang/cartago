!test.

+!test
	<-	!locate_tool;
	 	.my_name(Me);
	    println(Me," 1");
	    op2;
	    println(Me," 2");
	    op2;
	    println(Me," 3");
	    op2;
	    println(Me," 4");
	    op2.

+!locate_tool: true 
  <- lookupArtifact("my_art",Id);
  		focus(Id).
  
-!locate_tool: true 
  <-.wait(10); !locate_tool.
		  