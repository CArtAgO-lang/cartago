!test_remote.

+!test_remote <-
  println("testing remote..");
  mountWorkspace("/main","localhost","/main","b","web");
  joinWorkspace("/main/b", Wid);
  println("hello, remote world!");
  !use_remote.  

+!test_remote <-
  println("testing remote..");
  joinRemoteWorkspace("main","localhost",WspId);
  println("hello, remote world!");
  !use_remote.
  
+!use_remote <-
  makeArtifact(c0,"test.Counter",[],Id);
  focus(Id);
  inc;
  inc.

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
  
     
// not handled events      

+Ev [source(s)] : true <-
  .print(Ev," from ",S).
