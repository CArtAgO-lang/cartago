!test.

+!test
  <- makeArtifact("shell","c4jexamples.Shell",[],Id);
     !test_whoami;
     focus(Id);
	 !test_traceroute.
	 
+!test_whoami : true
  <- println("who is the user:");
     whoami(User);
	 println(User).
	 
+!test_traceroute 
  <- println("Tracing the route to CArtAgO site:");
     traceroute("cartago.sourceforge.net");
     println("done.").
  
+hop(N,Addr,IP,D0,D1,D2)
  <- println(N," ",Addr," (",IP,") ",D0," ",D1," ",D2).
