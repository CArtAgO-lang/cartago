/* 
 * to !test_remote 
 *
 * first spawn SetupRemoteNodeWebInfr
 * then launch LaunchJasonTestWebInfr 
 */

!test_remote.  // OK 

/* 
 * to !test_remote 
 *
 * first spawn LaunchRemoteNodeWebInfr
 * then spwan LaunchJasonTestWebInfr 
 */

// !test_wsp_remote_create.  // NOT OK


/* 
 *  /main
 * 		/b (linked to http://localhost/myMAS/main)
 * 			/w0 
 */

+!test_remote <-
  println("Testing distributed MAS.");
  lookupArtifact("workspace", WspArtId);
  focus(WspArtId);
  println("Linking a remote wsp belonging to another MAS called myMAS running on localhost...");
  linkWorkspace("http://localhost/myMAS/main","b");
  // alternative: mountWorkspace("http://localhost/myMAS","/main","/main/b");
  println("link ok");
  println("joining the remote wsp (some msgs will be printed in the console there)");
  joinWorkspace("/main/b", Wid);
  println("joined ", Wid);
  println("hello, remote world!");
  println("joined ", Wid, "(this is printed in the local console)")[wsp("main")];
  println("creating a remote wsp w0 as a chuld of wsp b (main)");
  createWorkspace("w0");
  println("created workspace w0")[wsp("main")];
  println("before join...")[wsp("main")];
  joinWorkspace("w0",Wid2);
  println("joined workspace w0. Creating a c0 counter... (this is printed in the remote wsp)")[wsp("/main/b")];
  makeArtifact(c0,"acme.Counter",[],Id);
  println("Counter created, using it")[wsp("main")];
  focus(Id);
  inc;
  inc;
  println("Now creating an artifact on the local main wsp...")[wsp("main")];
  makeArtifact("/main/c1","acme.Counter", [], Id4); 
  println(Id4);
  println("Lookup for the c0 artifact created in the remote wsp")[wsp("main")];  
  lookupArtifact("c0",Id5);
  println("found: ", Id5)[wsp("main")].

+linked_wsp(LinkName, WspPath)
	<- println("new wsp linked: ", WspPath, " link name: ", LinkName).  

+!use_remote <-
  makeArtifact(c0,"acme.Counter",[],Id);
  focus(Id);
  inc;
  inc;
  createWorkspace("w0");
  println("created workspace w0");
  joinWorkspace("w0",Wid);
  println("joined wsp ",Wid).

-!use_remote [action_failed(make_artifact(_,_,_),artifact_already_present)] <-
  println("artifact already created ");
  focus("c0");
  inc.
    
+count(V)[artifact_name(Id,c0)]
  <- println("perceived from remote counter: ",V)[wsp("main")].

    
// -- remote workspace creation

+!test_wsp_remote_create
  <- println("create remote wsp... ");
     createWorkspace("w1","http://localhost:15000");
     joinWorkspace("w1", Id);
     println("hello there in ", Id).
  
    
// not handled events      

+Ev [source(s)] : true <-
  .print(Ev," from ",S).
