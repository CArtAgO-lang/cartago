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
  

/*
+!test_remote <-
  println("testing remote..");
  spawnNode("localhost","main","web").
*/


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

    
// -- remote workspace creation

+!test_wsp_remote_create
  <- println("create remote wsp... ");
     createWorkspace("w1","http://localhost:15000");
     joinWorkspace("w1", Id);
     println("hello there in ", Id).
  
    
// not handled events      

+Ev [source(s)] : true <-
  .print(Ev," from ",S).
