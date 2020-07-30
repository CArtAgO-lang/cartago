!hello.

// !init.

+!hello
	<- 	cartago.current_wsps(X);
		println("hello ",X).
	
+!init
  <- .my_name(Me);
     println(Me,": Ready");
  	 out("ready",Me).

+!go(Name)
  <- !go1(Name);
     !go2.
     
/*
 * Even if multiple artifacts providing the same operation "op" are available,
 * by default it is selected the artifact which has been created by the agent.
 * So in this case the expected value for X is 1
 */
+!go1(Name)
  <- makeArtifact(Name,"acme.CommonArtifact",[],_);
     op(1,X);
     println(X).
     
/*
 * In this case instead we use the shared artifact called a0,
 * so the result can be 1 or 2
 */
+!go2
  <- op(1,X)[art("a0")];
     println(X).
      
      
