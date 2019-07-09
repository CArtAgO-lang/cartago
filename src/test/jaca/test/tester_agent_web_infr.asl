// !test_wsp.  // OK

// !test_link. // OK

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
 * first spawn CartagoMiddlewareDaemon
 * then launch LaunchJasonTestWebInfr 
 */
// !test_wsp_remote_create.  // NOT OK


+!test_wsp
  <- createWorkspace("w0");
     println("created w0");
	 createWorkspace("w1");
	 println("created w1");
	 joinWorkspace("w0",W0);
	 println("joined w0");
	 joinWorkspace("/main/w1",W1);
	 println("joined w1");
  	 makeArtifact("my_counter","test.Counter", [], Id1);
     println("artifact created ", Id1);
     lookupArtifact("my_counter", Id2);
     println("artifact lookup ", Id2);
     lookupArtifact("/main/w1/my_counter",Id3);
     println("artifact lookup with full path name ", Id3);
     makeArtifact("/main/w0/my_counter2","test.Counter", [], Id4);
     println("make artifact with full path name ", Id4);
     joinWorkspace("../w0",Id5);
     println("join wsp artifact with relative path name ", Id5);
     lookupArtifact("../w1/my_counter",Id6);
     println("artifact lookup with relative path name ", Id6).

+joined_wsp(Name,Id)	
	<- println("joined wsp: ",Name," ",Id).          
          
-!test_wsp [error_msg(Msg)]
  <- println("OOOPS failed: ",Msg).


/*
+!test_remote <-
  println("testing remote..");
  spawnNode("localhost","main","web").
 
*/

+!test_remote <-
  println("testing remote..");
  mountWorkspace("http://localhost/myMAS/main","/main/b","web");
  println("mount ok");
  joinWorkspace("/main/b", Wid);
  println("joined the remote ", Wid);
  println("hello, remote world!");
  createWorkspace("w0");
  println("created workspace w0");
  println("before join...");
  joinWorkspace("w0",Wid2);
  println("joined workspace w0");
  makeArtifact(c0,"test.Counter",[],Id);
  focus(Id);
  inc;
  inc;
  makeArtifact("/main/c1","test.Counter", [], Id4); 
  .println(Id4);
  lookupArtifact("c0",Id5);
  .println(Id5).
  


+!use_remote <-
  makeArtifact(c0,"test.Counter",[],Id);
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
  <- .println("perceived from remote counter: ",V).

  
+!test_wsp 
  <- createWorkspace("w0");
     println("joining...");
     joinWorkspace("w0",WspID);
     println("making artifact...");
     makeArtifact("a0","c4jtest.MyArtifactA",[],Id2);
     println("looking up...");
     lookupArtifact("a0",Id3);
     println("hello ",Id3);
     createWorkspace("w1");
     println("joining w1...");
     joinWorkspace("w1",WspID2);
     println("computing...");
     compute(5,X,Y)[wsp("w0")];
     println(X).

+joined_wsp(Name,Id)	
	<- println("joined wsp: ",Name," ",Id).          
          
-!test_wsp [error_msg(Msg)]
  <- println("OOOPS failed: ",Msg).
  
// -- link

+!test_link
  <-  makeArtifact("myArtifact","test.LinkingArtifact",[],Id1);
      makeArtifact("count","test.LinkableArtifact",[],Id2);
	  linkArtifacts(Id1,"out-1",Id2);			
	  println("artifacts linked: going to test");
	  test;
      test2(V);
      println("value ",V);
	  println("testing link with multiple artifacts..");
	  makeArtifact("count3","test.LinkableArtifact",[],Id3);
	  linkArtifacts(Id1,"out-1",Id3);			
	  test;
      println("the test op should have been called on two count artifacts...").	  
	  
      //test3;
    //lookupArtifact("test",Id3);
    //println("test exists! ",Id3).
    //observe_property(Id3,aprop(X));
    //println("obs prop value: ",X).
 
  
// -- remote workspace creation

+!test_wsp_remote_create
  <- println("create remote wsp... ");
     createWorkspace("w1","http://localhost:15000");
     joinWorkspace("w1", Id);
     println("hello there in ", Id).
  
     
// not handled events      

+Ev [source(s)] : true <-
  .print(Ev," from ",S).
